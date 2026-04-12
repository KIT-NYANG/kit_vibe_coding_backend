package com.nyang.backend.lectureClass.entity;

import lombok.*;

@Getter
@RequiredArgsConstructor
public enum LectureClassCategory {
    BACKEND("백엔드"),
    FRONTEND("프론트엔드"),
    AI("AI"),
    INFRA("인프라"),
    DATABASE("데이터베이스"),
    DEVOPS("데브옵스"),
    CS("컴퓨터 공학");

    private final String description; // UI에 보여줄 한글명
}