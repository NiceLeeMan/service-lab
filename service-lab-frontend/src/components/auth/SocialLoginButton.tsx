import type { ButtonHTMLAttributes } from "react";

type SocialProvider = "kakao" | "naver" | "google";

interface SocialLoginButtonProps
    extends Omit<ButtonHTMLAttributes<HTMLButtonElement>, "children" | "type"> {
    provider: SocialProvider;
    imageSrc?: string;
}

const providerConfig = {
    kakao: {
        label: "카카오로 로그인",
        style: "border-transparent bg-[#FEE500] text-black",
    },
    naver: {
        label: "네이버로 로그인",
        style: "border-transparent bg-[#03A94D] text-white",
    },
    google: {
        label: "Google로 로그인",
        style: "border-slate-300 bg-white text-slate-800",
    },
} satisfies Record<SocialProvider, { label: string; style: string }>;

/**
 * 제공자별 소셜 로그인 버튼.
 *
 * imageSrc가 있으면 완성형 버튼 이미지를 표시한다.
 * API 호출과 브라우저 이동은 부모가 전달한 onClick에서 처리한다.
 */
export default function SocialLoginButton({
                                              provider,
                                              imageSrc,
                                              disabled = false,
                                              className = "",
                                              ...buttonProps
                                          }: SocialLoginButtonProps) {
    const { label, style } = providerConfig[provider];

    return (
        <button
            {...buttonProps}
            type="button"
            disabled={disabled}
            aria-label={label}
            className={[
                "flex h-12 w-full items-center justify-center",
                "overflow-hidden rounded-lg border text-sm font-semibold",
                "transition duration-150",
                "enabled:cursor-pointer enabled:hover:brightness-95",
                "focus-visible:outline-none focus-visible:ring-4",
                "focus-visible:ring-sky-200 focus-visible:ring-offset-2",
                "disabled:cursor-not-allowed disabled:opacity-50",
                style,
                className,
            ].join(" ")}
        >
            {imageSrc ? (
                <img
                    src={imageSrc}
                    alt=""
                    aria-hidden="true"
                    draggable={false}
                    className="max-h-full max-w-full object-contain"
                />
            ) : (
                label
            )}
        </button>
    );
}