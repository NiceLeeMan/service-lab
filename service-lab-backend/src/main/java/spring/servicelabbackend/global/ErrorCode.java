package spring.servicelabbackend.global;

import org.springframework.http.HttpStatus;


/**
 * 각 도메인의 에러코드가 구현하는 공통 인터페이스.
 */
public interface ErrorCode {

    // HTTP 응답 상태
    HttpStatus getStatus();

    // 비즈니스 에러 식별 코드
    String getCode();

    // 클라이언트에 전달할 메시지
    String getMessage();
}