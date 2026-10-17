package spring.servicelabbackend.user.outbound.kakao;


/**
 * 프론트엔드에 전달하는 카카오 OIDC 인가 요청 설정 응답.
 *
 * <p>프론트엔드는 각 필드를 카카오 요청 파라미터로 변환하여
 * {@code https://kauth.kakao.com/oauth/authorize}로 브라우저를 이동시킨다.
 *
 * @param clientId 카카오 애플리케이션의 REST API 키.
 *                 요청 파라미터 {@code client_id}에 사용한다.
 * @param redirectUri 인가 코드 또는 오류를 전달받을 우리 서버의 콜백 URI.
 *                    카카오디벨로퍼스에 등록된 URI와 일치해야 한다.
 * @param responseType 요청할 응답 유형. {@code "code"}를 사용한다.
 * @param scope 요청할 동의항목. OIDC 사용을 위해 {@code "openid"}를 포함한다.
 *              여러 항목을 요청하면 쉼표로 구분한다.
 * @param state 서버가 로그인 요청마다 생성하는 임의의 문자열.
 *              서버는 이를 요청과 연결해 보관하고 콜백의 값과 비교한다.
 * @param nonce 서버가 생성하는 ID 토큰 재사용 공격 방지용 임의의 문자열.
 *              사용한 경우 서버는 ID 토큰의 nonce 클레임과 비교한다.
 *              사용하지 않으면 null을 전달한다.
 * @see <a href="https://developers.kakao.com/docs/ko/kakaologin/rest-api#request-code">
 *     카카오 로그인 REST API — 인가 코드 요청</a>
 */
public record KakaoOAuthConfigRes(
        String clientId,
        String redirectUri,
        String responseType,
        String scope,
        String state,
        String nonce
) {

    /**
     * 응답 유형과 동의항목을 각각 code, openid로 설정한다.
     */
    public static KakaoOAuthConfigRes of(
            String clientId,
            String redirectUri,
            String state,
            String nonce
    ) {
        return new KakaoOAuthConfigRes(
                clientId,
                redirectUri,
                "code",
                "openid",
                state,
                nonce
        );
    }
}