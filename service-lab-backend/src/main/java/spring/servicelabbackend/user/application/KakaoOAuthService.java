package spring.servicelabbackend.user.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import spring.servicelabbackend.user.outbound.kakao.KakaoOAuthConfigRes;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * 카카오 OIDC 인가 요청에 필요한 데이터를 생성한다.
 */
@Service
public class KakaoOAuthService {

    private final String clientId;
    private final String redirectUri;
    private final SecureRandom secureRandom = new SecureRandom();

    public KakaoOAuthService(
            @Value("${oauth.kakao.client-id}") String clientId,
            @Value("${oauth.kakao.redirect-uri}") String redirectUri
    ) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
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