package com.nyang.backend.lecture.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SttQuizDto {

    @JsonProperty("quizInsertTimeSec")
    private Integer quizInsertTimeSec;

    private String question;
    private String answer;
    private String explanation;
    private String supplementalDescription;
}