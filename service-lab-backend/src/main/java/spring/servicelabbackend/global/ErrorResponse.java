package spring.servicelabbackend.global;

/**
 * 클라이언트에 전달하는 공통 에러 응답 DTO.
 * HTTP 상태코드는 응답 상태로 전달한다.
 */
public record ErrorResponse(
        String code,
        String message
) {

    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage()
        );
    }
}