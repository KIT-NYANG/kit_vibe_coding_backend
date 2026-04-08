package com.nyang.backend.global.response;

import com.nyang.backend.global.exception.ErrorCode;
import com.nyang.backend.global.exception.SuccessCode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseDto<T> {
    private String code;
    private String message;
    private T data;

    // 성공 응답 - OK: 조회 성공, CREATED: 생성 성공
    public static <T> ResponseDto<T> success(SuccessCode successCode, T data) {
        return ResponseDto.<T>builder()
                .code(successCode.getCode())
                .message(successCode.getMessage())
                .data(data)
                .build();
    }

    // 성공 응답 (성공 메시지를 같이 넘기는 구조)
    public static <T> ResponseDto<T> success(SuccessCode successCode, String message, T data) {
        return ResponseDto.<T>builder()
                .code(successCode.getCode())
                .message(message)
                .data(data)
                .build();
    }

    // 실패 응답 (status, code, message만 내려주면 충분할 때)
    public static <T> ResponseDto<T> error(ErrorCode errorCode) {
        return ResponseDto.<T>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .data(null)
                .build();
    }

    // 실패 응답 (추가 data 내려줄 때 사용)
    public static <T> ResponseDto<T> error(ErrorCode errorCode,T data) {
        return ResponseDto.<T>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .data(data)
                .build();
    }
}