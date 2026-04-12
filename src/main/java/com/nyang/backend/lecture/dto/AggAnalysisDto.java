package com.nyang.backend.lecture.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class AggAnalysisDto {
    private List<SttQuizDto> quizzes;
    private List<SttTeacherGuideDto> teacherGuides;
    private AnalysisDto toAnalysisDto(AggAnalysisDto aggAnalysisDto) {
        return AnalysisDto.builder()
                .quizzes(aggAnalysisDto.getQuizzes())
                .teacherGuides(aggAnalysisDto.getTeacherGuides())
                .build();
    }
}
