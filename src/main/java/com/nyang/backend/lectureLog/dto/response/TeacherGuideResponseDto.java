package com.nyang.backend.lectureLog.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeacherGuideResponseDto {
    private String predictedDifficultSection;
    private String predictedReason;
    private String improvementSuggestion;
}