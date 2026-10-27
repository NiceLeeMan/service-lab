package spring.servicelabbackend.user.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.servicelabbackend.user.application.KakaoOAuthService;
import spring.servicelabbackend.user.outbound.kakao.KakaoOAuthConfigRes;

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
public class KakaoOAuthController {

    private final KakaoOAuthService kakaoOAuthService;

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

        session.setAttribute("KAKAO_OAUTH_STATE", config.state());
        session.setAttribute("KAKAO_OAUTH_NONCE", config.nonce());

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(config);
    }
}