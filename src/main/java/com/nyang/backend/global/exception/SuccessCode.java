package com.nyang.backend.global.exception;

import lombok.*;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {
    OK("OK", "조회 성공"),
    CREATED("CREATED", "생성 성공");

    private final String code;
    private final String message;
}