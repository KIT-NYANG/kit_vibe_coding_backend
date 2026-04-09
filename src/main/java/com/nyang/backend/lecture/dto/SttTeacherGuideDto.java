package com.nyang.backend.lecture.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SttTeacherGuideDto {
    private String predictedDifficultSection;
    private String predictedReason;
    private String improvementSuggestion;
}