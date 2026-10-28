package spring.servicelabbackend.user.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import spring.servicelabbackend.user.outbound.kakao.KakaoOAuthConfigRes;
import spring.servicelabbackend.user.outbound.kakao.KakaoOAuthTokenException;
import spring.servicelabbackend.user.outbound.kakao.KakaoOAuthTokenRes;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * 카카오 OIDC 인가 요청에 필요한 데이터를 생성한다.
 */
@Service
public class KakaoOAuthService {

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final RestClient kakaoAuthClient;
    private final SecureRandom secureRandom = new SecureRandom();

    @Autowired
    public KakaoOAuthService(
            @Value("${oauth.kakao.client-id}") String clientId,
            @Value("${oauth.kakao.client-secret:}") String clientSecret,
            @Value("${oauth.kakao.redirect-uri}") String redirectUri
    ) {
        this(
                clientId,
                clientSecret,
                redirectUri,
                RestClient.create("https://kauth.kakao.com")
        );
    }

    KakaoOAuthService(
            String clientId,
            String clientSecret,
            String redirectUri,
            RestClient kakaoAuthClient
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.kakaoAuthClient = kakaoAuthClient;
    }

    /**
     * 로그인 요청마다 새로운 state와 nonce를 생성한다.
     *
     * @return 프론트엔드에서 카카오 인증 URL을 구성할 설정값
     */
    public KakaoOAuthConfigRes createConfig() {
        return KakaoOAuthConfigRes.of(
                clientId,
                redirectUri,
                generateRandomValue(),
                generateRandomValue()
        );
    }

    /**
     * 카카오에서 받은 인가 코드를 액세스 토큰과 ID 토큰으로 교환한다.
     *
     * @param code 카카오 콜백으로 전달된 일회용 인가 코드
     * @return 카카오 토큰 응답
     */
    public KakaoOAuthTokenRes exchangeCodeForTokens(String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", clientId);
        form.add("redirect_uri", redirectUri);
        form.add("code", code);

        if (clientSecret != null && !clientSecret.isBlank()) {
            form.add("client_secret", clientSecret);
        }

        try {
            KakaoOAuthTokenRes tokenResponse = kakaoAuthClient.post()
                    .uri("/oauth/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(KakaoOAuthTokenRes.class);

            if (tokenResponse == null) {
                throw new KakaoOAuthTokenException("카카오 토큰 응답이 비어 있습니다.");
            }
            if (!tokenResponse.hasIdToken()) {
                throw new KakaoOAuthTokenException(
                        "카카오 토큰 응답에 ID 토큰이 없습니다. OpenID Connect 설정을 확인해 주세요."
                );
            }

            return tokenResponse;
        } catch (KakaoOAuthTokenException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new KakaoOAuthTokenException(
                    "카카오 토큰 API 요청에 실패했습니다.",
                    exception
            );
        }
    }

    /**
     * URL 파라미터에 사용할 수 있는 암호학적 난수를 생성한다.
     */
    private String generateRandomValue() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}
