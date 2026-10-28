import { useEffect, useState, type SubmitEvent } from "react";
import FormInput from "../components/common/FormInput";
import SocialLoginButton from "../components/auth/SocialLoginButton";
import { getKakaoOAuthConfig } from "../api/KakaoAuth";
import kakaoLoginImage from "../asset/kakao_login.png";

function getKakaoOAuthNotice() {
    const params = new URLSearchParams(window.location.search);

    if (params.get("oauth") !== "kakao") {
        return "";
    }

    const status = params.get("status");
    const reason = params.get("reason");

    if (status === "success") {
        return "카카오에서 ID 토큰을 정상적으로 발급받았습니다.";
    }
    if (status === "cancelled") {
        return "카카오 로그인이 취소되었습니다.";
    }
    if (reason === "invalid_state") {
        return "카카오 로그인 요청을 확인할 수 없습니다. 다시 시도해 주세요.";
    }
    if (reason === "missing_code") {
        return "카카오에서 인가 코드를 받지 못했습니다.";
    }

    return "카카오 토큰 발급에 실패했습니다. 다시 시도해 주세요.";
}

export default function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [notice, setNotice] = useState(getKakaoOAuthNotice);

    useEffect(() => {
        const params = new URLSearchParams(window.location.search);

        if (params.get("oauth") === "kakao") {
            window.history.replaceState({}, "", window.location.pathname);
        }
    }, []);

    // API 연결 전에는 안내 메시지만 표시한다.
    function handleLogin(event: SubmitEvent<HTMLFormElement>) {
        event.preventDefault();
        setNotice("이메일 로그인을 준비 중이에요.");
    }

    async function handleKakaoLogin() {
        setNotice("");

        try {
            // 1. 우리 서버에서 인가 요청 설정 수신
            const config = await getKakaoOAuthConfig();

            // 2. 응답값을 카카오 요청 파라미터로 변환
            const url = new URL("https://kauth.kakao.com/oauth/authorize");

            url.search = new URLSearchParams({
                client_id: config.clientId,
                redirect_uri: config.redirectUri,
                response_type: config.responseType,
                scope: config.scope,
                state: config.state,
                nonce: config.nonce,
            }).toString();

            // 3. 현재 탭을 이동시키며 카카오에 GET 인가 요청
            window.location.assign(url.toString());
        } catch (error) {
            setNotice(
                error instanceof Error
                    ? error.message
                    : "카카오 로그인을 시작하지 못했습니다.",
            );
        }
    }

    return (
        <main className="min-h-screen bg-gradient-to-br from-white via-sky-50 to-blue-100 text-slate-900">
            <header className="mx-auto max-w-7xl px-6 py-8 lg:px-12">
                <a
                    href="/"
                    aria-label="Service Lab 홈"
                    className="text-2xl font-bold tracking-tight"
                >
                    Service <span className="text-sky-600">Lab</span>
                </a>
            </header>

            <div className="mx-auto grid max-w-7xl items-center gap-12 px-6 pb-12 pt-4 lg:min-h-[calc(100vh-100px)] lg:grid-cols-2 lg:px-12 lg:pt-0">
                <section className="text-center lg:text-left">
                    <p className="mb-6 text-xs font-medium tracking-[0.25em] text-slate-500">
                        PEOPLE · IDEAS · BETTER SERVICES
                    </p>

                    <h1 className="text-4xl font-bold leading-tight tracking-tight sm:text-5xl lg:text-6xl">
                        새로운 시작을
                        <br />
                        <span className="text-sky-600">함께해요</span>
                    </h1>

                    <p className="mt-6 text-base leading-relaxed text-slate-500 sm:text-lg">
                        더 나은 서비스를 향한 첫걸음,
                        <br />
                        Service Lab과 함께 시작해 보세요.
                    </p>

                    {/* 커스텀 장식 이미지를 추가할 영역 */}
                    <div aria-hidden="true" className="hidden h-56 lg:block" />
                </section>

                <section
                    aria-labelledby="login-title"
                    className="mx-auto w-full max-w-lg rounded-3xl border border-sky-100 bg-white p-7 shadow-xl shadow-sky-900/5 sm:p-12"
                >
                    <h2
                        id="login-title"
                        className="text-3xl font-bold tracking-tight sm:text-4xl"
                    >
                        로그인
                    </h2>

                    <p className="mt-3 text-sm text-slate-500">
                        Service Lab에 오신 것을 환영해요.
                    </p>

                    <form onSubmit={handleLogin} className="mt-8 space-y-5">
                        <FormInput
                            label="이메일"
                            name="email"
                            type="email"
                            autoComplete="username"
                            placeholder="이메일을 입력해 주세요"
                            value={email}
                            onChange={(event) => setEmail(event.target.value)}
                            required
                        />

                        <FormInput
                            label="비밀번호"
                            name="password"
                            type="password"
                            autoComplete="current-password"
                            placeholder="비밀번호를 입력해 주세요"
                            value={password}
                            onChange={(event) => setPassword(event.target.value)}
                            required
                        />

                        <button
                            type="submit"
                            className="h-12 w-full cursor-pointer rounded-lg bg-sky-600 font-semibold text-white transition hover:bg-sky-700 focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-sky-200 focus-visible:ring-offset-2"
                        >
                            로그인
                        </button>
                    </form>

                    <div className="my-7 flex items-center gap-4">
                        <div aria-hidden="true" className="h-px flex-1 bg-slate-200" />
                        <span className="text-xs text-slate-500">
              또는 소셜 계정으로 로그인
            </span>
                        <div aria-hidden="true" className="h-px flex-1 bg-slate-200" />
                    </div>

                    <div className="space-y-3">
                        <SocialLoginButton
                            provider="kakao"
                            imageSrc={kakaoLoginImage}
                            onClick={handleKakaoLogin}
                        />
                        <SocialLoginButton provider="naver" disabled />
                        <SocialLoginButton provider="google" disabled />
                    </div>

                    <p
                        role="status"
                        className="mt-4 min-h-5 text-center text-sm text-slate-500"
                    >
                        {notice}
                    </p>

                    <div className="mt-6 flex flex-wrap items-center justify-center gap-2 text-sm">
                        <span className="text-slate-500">아직 계정이 없으신가요?</span>
                        <button
                            type="button"
                            onClick={() => setNotice("회원가입을 준비 중이에요.")}
                            className="cursor-pointer rounded font-semibold text-sky-600 hover:text-sky-700 focus-visible:outline-2 focus-visible:outline-sky-500"
                        >
                            회원가입
                        </button>
                    </div>
                </section>
            </div>
        </main>
    );
}
