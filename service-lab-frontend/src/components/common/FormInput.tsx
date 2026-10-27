import { useId, useState, type InputHTMLAttributes } from "react";

interface FormInputProps extends InputHTMLAttributes<HTMLInputElement> {
    label: string;
    error?: string;
}

/**
 * 라벨과 오류 메시지를 포함하는 재사용 입력 컴포넌트.
 * type="password"이면 비밀번호 표시·숨기기 버튼을 제공한다.
 */
export default function FormInput({
                                      label,
                                      error,
                                      id,
                                      type = "text",
                                      disabled,
                                      className = "",
                                      "aria-describedby": ariaDescribedBy,
                                      "aria-invalid": ariaInvalid,
                                      ...inputProps
                                  }: FormInputProps) {
    const generatedId = useId();
    const inputId = id ?? generatedId;
    const errorId = `${inputId}-error`;

    const [showPassword, setShowPassword] = useState(false);
    const isPassword = type === "password";
    const inputType = isPassword && showPassword ? "text" : type;

    const describedBy =
        [ariaDescribedBy, error ? errorId : undefined]
            .filter(Boolean)
            .join(" ") || undefined;

    return (
        <div className="w-full">
            <label
                htmlFor={inputId}
                className="mb-2 block text-sm font-medium text-slate-800"
            >
                {label}
            </label>

            <div className="relative">
                <input
                    {...inputProps}
                    id={inputId}
                    type={inputType}
                    disabled={disabled}
                    aria-invalid={error ? true : ariaInvalid}
                    aria-describedby={describedBy}
                    className={[
                        "w-full rounded-lg border bg-white px-4 py-3 text-base",
                        "text-slate-900 placeholder:text-slate-400",
                        "transition-colors focus:outline-none focus:ring-4",
                        "disabled:cursor-not-allowed disabled:bg-slate-100",
                        error
                            ? "border-red-400 focus:border-red-500 focus:ring-red-100"
                            : "border-slate-300 focus:border-sky-500 focus:ring-sky-100",
                        isPassword ? "pr-12" : "",
                        className,
                    ].join(" ")}
                />

                {isPassword && (
                    <button
                        type="button"
                        disabled={disabled}
                        aria-label={showPassword ? "비밀번호 숨기기" : "비밀번호 보기"}
                        aria-controls={inputId}
                        aria-pressed={showPassword}
                        onClick={() => setShowPassword((previous) => !previous)}
                        className={[
                            "absolute inset-y-0 right-0 flex w-12 items-center justify-center",
                            "rounded-r-lg text-slate-400 hover:text-sky-600",
                            "focus-visible:outline-2 focus-visible:outline-sky-500",
                            "disabled:cursor-not-allowed disabled:opacity-50",
                        ].join(" ")}
                    >
                        <svg
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth={1.8}
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            className="h-5 w-5"
                            aria-hidden="true"
                        >
                            <path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12Z" />
                            <circle cx="12" cy="12" r="3" />
                            {showPassword && <path d="m3 3 18 18" />}
                        </svg>
                    </button>
                )}
            </div>

            {error && (
                <p id={errorId} role="alert" className="mt-2 text-sm text-red-600">
                    {error}
                </p>
            )}
        </div>
    );
}