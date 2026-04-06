package com.nyang.backend.global.exception;

import com.nyang.backend.global.response.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // BusinessException이 발생했을 때 실행
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseDto<Void>> handleBusinessException(BusinessException ex) {
        return ResponseEntity
                .status(ex.getErrorCode().getStatus())
                .body(ResponseDto.error(ex.getErrorCode())); // 응답 body를 ResponseDto 형식으로
    }

    // 입력값 검증 에러 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.error(ErrorCode.INVALID_INPUT_VALUE));
    }

    // 예상 밖의 에러 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Void>> handleUnexpectedException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 예상 못 한 모든 에러를 500 서버 에러로 통일
                .body(ResponseDto.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}