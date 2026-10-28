package spring.servicelabbackend.user.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import spring.servicelabbackend.user.application.KakaoOAuthService;
import spring.servicelabbackend.user.outbound.kakao.KakaoOAuthConfigRes;
import spring.servicelabbackend.user.outbound.kakao.KakaoOAuthTokenException;

import java.net.URI;
import java.util.Objects;

/**
 * 카카오 로그인 시작에 필요한 설정을 제공한다.
 */
@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"
)
@RestController
@RequestMapping("/api/auth/kakao")
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthController {

    private static final String OAUTH_STATE_SESSION_KEY = "KAKAO_OAUTH_STATE";
    private static final String OAUTH_NONCE_SESSION_KEY = "KAKAO_OAUTH_NONCE";

    private final KakaoOAuthService kakaoOAuthService;

    @Value("${oauth.kakao.frontend-redirect-uri}")
    private String frontendRedirectUri;

    /**
     * 인가 요청 설정을 반환하고 검증용 값을 세션에 보관한다.
     *
     * <p>state는 콜백에서, nonce는 ID 토큰 검증 시 비교한다.
     * 요청마다 생성되는 값이므로 응답을 캐시하지 않는다.
     *
     * @param session 로그인 요청과 콜백을 연결할 HTTP 세션
     * @return 카카오 인가 요청 설정
     */
    @GetMapping("/config")
    public ResponseEntity<KakaoOAuthConfigRes> getConfig(
            HttpSession session
    ) {
        KakaoOAuthConfigRes config = kakaoOAuthService.createConfig();

        session.setAttribute(OAUTH_STATE_SESSION_KEY, config.state());
        session.setAttribute(OAUTH_NONCE_SESSION_KEY, config.nonce());

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(config);
    }

    /**
     * 카카오가 전달한 인가 코드를 토큰으로 교환하고 프론트엔드로 돌아간다.
     *
     * <p>현재 단계에서는 ID 토큰을 수신했는지만 확인한다. 사용자 조회와 서비스 로그인
     * 처리는 이후 단계에서 토큰 응답을 이용해 구현한다. 토큰 자체는 URL에 노출하지 않는다.
     */
    @GetMapping("/callback")
    public ResponseEntity<Void> callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            HttpSession session
    ) {
        String expectedState = (String) session.getAttribute(OAUTH_STATE_SESSION_KEY);
        session.removeAttribute(OAUTH_STATE_SESSION_KEY);
        session.removeAttribute(OAUTH_NONCE_SESSION_KEY);

        if (expectedState == null || !Objects.equals(expectedState, state)) {
            return redirectToFrontend("error", "invalid_state");
        }

        if (error != null) {
            String status = "access_denied".equals(error) ? "cancelled" : "error";
            return redirectToFrontend(status, "authorization_failed");
        }

        if (code == null || code.isBlank()) {
            return redirectToFrontend("error", "missing_code");
        }

        try {
            kakaoOAuthService.exchangeCodeForTokens(code);
            return redirectToFrontend("success", null);
        } catch (KakaoOAuthTokenException exception) {
            log.warn("Kakao OAuth token exchange failed: {}", exception.getMessage());
            return redirectToFrontend("error", "token_exchange_failed");
        }
    }

    private ResponseEntity<Void> redirectToFrontend(String status, String reason) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(frontendRedirectUri)
                .queryParam("oauth", "kakao")
                .queryParam("status", status);

        if (reason != null) {
            uriBuilder.queryParam("reason", reason);
        }

        URI location = uriBuilder.build().encode().toUri();

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(location)
                .build();
    }
}
