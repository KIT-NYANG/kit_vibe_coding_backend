package com.nyang.backend.lectureLog.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizItemResponseDto {
    private Integer quizInsertTimeSec;
    private String question;
    private String answer;
    private String explanation;
    private String supplementalDescription;
}