package spring.servicelabbackend.user.outbound.kakao;

/**
 * 카카오 인가 코드를 토큰으로 교환하지 못했을 때 발생한다.
 */
public class KakaoOAuthTokenException extends RuntimeException {

    public KakaoOAuthTokenException(String message) {
        super(message);
    }

    public KakaoOAuthTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
