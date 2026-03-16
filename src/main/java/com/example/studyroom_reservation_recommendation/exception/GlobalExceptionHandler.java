package com.example.studyroom_reservation_recommendation.exception;

import com.example.studyroom_reservation_recommendation.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 에러 가로챔
@Slf4j // 로그
public class GlobalExceptionHandler {
    // 잘못된 파라미터가 넘어왔을 때
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> illegalArgumentExceptionHandler(IllegalArgumentException e) {
        log.error("잘못된 요청 발생: {}", e.getMessage());
        ErrorResponse response = new ErrorResponse("BAD_REQUEST", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 서버 내부에서 우리가 예상치 못한 런타임 에러가 터졌을 때
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeRuntimeException(RuntimeException e) {
        log.error("서버 내부 오류 발생: ", e);
        ErrorResponse response = new ErrorResponse("SERVER_ERROR", "서버 내부에서 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
