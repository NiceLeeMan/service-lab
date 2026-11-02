import { useEffect, useState, type SubmitEvent } from "react";
import {
    getSocialSignupContext,
    submitSocialSignup,
} from "../api/KakaoAuth";
import FormInput from "../components/common/FormInput";

export default function SocialSignupPage() {
    const [nickname, setNickname] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");
    const [email, setEmail] = useState("");
    const [notice, setNotice] = useState("");

    useEffect(() => {
        getSocialSignupContext()
            .then((context) => setEmail(context.email))
            .catch((error: unknown) => {
                setNotice(
                    error instanceof Error
                        ? error.message
                        : "소셜 회원가입 정보를 불러오지 못했습니다.",
                );
            });
    }, []);

    async function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
        event.preventDefault();
        setNotice("");

        try {
            await submitSocialSignup({ nickname, phoneNumber, email });
            window.location.assign("/?oauth=kakao&status=success");
        } catch (error) {
            setNotice(
                error instanceof Error
                    ? error.message
                    : "소셜 회원가입에 실패했습니다.",
            );
        }
    }

    return (
        <main className="min-h-screen bg-gradient-to-br from-white via-sky-50 to-blue-100 text-slate-900">
            <header className="mx-auto max-w-7xl px-6 py-8 lg:px-12">
                <a href="/" className="text-2xl font-bold tracking-tight">
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
                        필요한 정보만 입력하면
                        <br />
                        Service Lab 가입이 완료됩니다.
                    </p>
                    <div aria-hidden="true" className="hidden h-56 lg:block" />
                </section>

                <section className="mx-auto w-full max-w-lg rounded-3xl border border-sky-100 bg-white p-7 shadow-xl shadow-sky-900/5 sm:p-12">
                    <h2 className="text-3xl font-bold tracking-tight sm:text-4xl">
                        소셜 회원가입
                    </h2>
                    <p className="mt-3 text-sm text-slate-500">
                        Service Lab에서 사용할 정보를 입력해 주세요.
                    </p>

                    <form onSubmit={handleSubmit} className="mt-8 space-y-5">
                        <FormInput
                            label="닉네임"
                            name="nickname"
                            placeholder="닉네임을 입력해 주세요"
                            value={nickname}
                            onChange={(event) => setNickname(event.target.value)}
                            required
                        />
                        <FormInput
                            label="전화번호"
                            name="phoneNumber"
                            type="tel"
                            placeholder="전화번호를 입력해 주세요"
                            value={phoneNumber}
                            onChange={(event) => setPhoneNumber(event.target.value)}
                            required
                        />
                        <FormInput
                            label="이메일"
                            name="email"
                            type="email"
                            value={email}
                            readOnly
                            required
                        />

                        <button
                            type="submit"
                            disabled={!email}
                            className="h-12 w-full cursor-pointer rounded-lg bg-sky-600 font-semibold text-white transition hover:bg-sky-700 focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-sky-200 focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:bg-slate-300"
                        >
                            가입하기
                        </button>
                    </form>

                    <p role="status" className="mt-4 min-h-5 text-center text-sm text-red-600">
                        {notice}
                    </p>
                </section>
            </div>
        </main>
    );
}
