package spring.servicelabbackend.user.outbound.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 카카오 토큰 요청 API 응답.
 *
 * <p>토큰은 브라우저로 전달하지 않고 백엔드의 다음 로그인 처리 단계에서만 사용한다.
 */
public record KakaoOAuthTokenRes(
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("id_token") String idToken,
        @JsonProperty("expires_in") Long expiresIn,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("refresh_token_expires_in") Long refreshTokenExpiresIn,
        String scope
) {

    public boolean hasIdToken() {
        return idToken != null && !idToken.isBlank();
    }
}
