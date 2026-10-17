package spring.servicelabbackend.global;

import lombok.Getter;

/**
 * 비즈니스 규칙 위반 시 에러코드를 담아 던지는 공통 예외.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}