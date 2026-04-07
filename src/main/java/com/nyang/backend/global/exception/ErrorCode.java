package com.nyang.backend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 인증 & 인가
    UNAUTHORIZED(401, "AUTH001", "비밀번호가 일치하지 않습니다."),
    FORBIDDEN(403, "AUTH002", "접근 권한이 없습니다."),
    ACCESS_TOKEN_NOT_FOUND(401, "AUTH003", "Access Token이 없습니다."),
    INVALID_TOKEN(401, "AUTH004", "유효하지 않은 토큰입니다."),

    // 회원 관련
    USER_NOT_FOUND(404, "USER001", "회원이 존재하지 않습니다."),
    EMAIL_DUPLICATED(400, "EMAIL001", "이메일이 이미 존재합니다."),
    PASSWORD_MISMATCH(400, "USER002", "비밀번호와 비밀번호 확인이 일치하지 않습니다."),

    // 요청값 관련
    INVALID_INPUT_VALUE(400, "COMMON002", "입력값이 올바르지 않습니다."),

    // 서버 오류
    INTERNAL_SERVER_ERROR(500, "COMMON001", "서버 오류입니다.");

    private final int status;
    private final String code;
    private final String message;
}