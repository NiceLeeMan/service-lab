package spring.servicelabbackend.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import spring.servicelabbackend.global.ErrorCode;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * 회원 도메인에서 사용하는 에러코드.
 */
@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    NICKNAME_REQUIRED(
            BAD_REQUEST, "닉네임을 입력해 주세요."),
    PHONE_NUMBER_REQUIRED(
            BAD_REQUEST, "전화번호를 입력해 주세요."),
    EMAIL_REQUIRED(
            BAD_REQUEST, "이메일을 입력해 주세요."),
    PASSWORD_REQUIRED(
            BAD_REQUEST, "비밀번호를 입력해 주세요."),

    INVALID_NICKNAME_LENGTH(
            BAD_REQUEST, "닉네임은 20자 이하여야 합니다."),
    INVALID_PHONE_NUMBER_LENGTH(
            BAD_REQUEST, "전화번호는 20자 이하여야 합니다."),
    INVALID_PHONE_NUMBER_FORMAT(
            BAD_REQUEST, "올바른 한국 전화번호 형식으로 입력해 주세요."),
    INVALID_EMAIL_LENGTH(
            BAD_REQUEST, "이메일은 20자 이하여야 합니다."),
    INVALID_EMAIL_FORMAT(
            BAD_REQUEST, "올바른 이메일 형식으로 입력해 주세요."),
    INVALID_PASSWORD_LENGTH(
            BAD_REQUEST, "비밀번호는 8~20자여야 합니다."),
    INVALID_PASSWORD_FORMAT(
            BAD_REQUEST, "비밀번호는 영문과 특수문자를 포함해야 합니다."),

    NICKNAME_ALREADY_EXISTS(
            CONFLICT, "이미 사용 중인 닉네임입니다."),
    EMAIL_ALREADY_EXISTS(
            CONFLICT, "이미 가입된 이메일입니다.");

    private final HttpStatus status;
    private final String message;

    @Override
    public String getCode() {
        return name();
    }
}