export interface SignupRequest {
    password: string;
    nickname: string;
    phoneNumber: string;
    email: string;
}

interface ErrorResponse {
    message?: string;
}

export async function signup(request: SignupRequest): Promise<void> {
    const response = await fetch("http://localhost:8080/api/users/signup", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(request),
    });

    if (!response.ok) {
        const error = await response.json().catch(() => ({})) as ErrorResponse;
        throw new Error(error.message ?? "회원가입에 실패했습니다.");
    }
}
