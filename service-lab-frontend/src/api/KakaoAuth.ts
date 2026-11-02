/**
 * BE의 KakaoOAuthConfigRes에 대응하는 응답 타입.
 */
export interface KakaoOAuthConfig {
    clientId: string;
    redirectUri: string;
    responseType: string;
    scope: string;
    state: string;
    nonce: string;
}

/**
 * 인가 요청 설정을 조회한다.
 * 서버 세션을 유지할 수 있도록 쿠키를 포함한다.
 */
export async function getKakaoOAuthConfig(): Promise<KakaoOAuthConfig> {
    const response = await fetch(
        "http://localhost:8080/api/auth/kakao/config",
        {
            method: "GET",
            credentials: "include",
            cache: "no-store",
        },
    );

    if (!response.ok) {
        throw new Error("카카오 로그인 설정을 불러오지 못했습니다.");
    }

    return response.json();
}

export interface SocialSignupContext {
    email: string;
}

export interface SocialSignupRequest {
    nickname: string;
    phoneNumber: string;
    email: string;
}

export async function getSocialSignupContext(): Promise<SocialSignupContext> {
    const response = await fetch(
        "http://localhost:8080/api/auth/kakao/signup",
        { credentials: "include" },
    );

    if (!response.ok) {
        throw new Error("소셜 회원가입 정보를 불러오지 못했습니다.");
    }

    return response.json();
}

export async function submitSocialSignup(
    request: SocialSignupRequest,
): Promise<void> {
    const response = await fetch(
        "http://localhost:8080/api/auth/kakao/signup",
        {
            method: "POST",
            credentials: "include",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(request),
        },
    );

    if (!response.ok) {
        throw new Error("소셜 회원가입에 실패했습니다.");
    }
}
