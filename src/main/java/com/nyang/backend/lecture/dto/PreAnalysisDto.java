package com.nyang.backend.lecture.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PreAnalysisDto {
    private List<SttQuizDto> quizzes;
    private List<SttTeacherGuideDto> teacherGuides;

    private AnalysisDto toAnalysisDto(PreAnalysisDto preAnalysisDto) {
        return AnalysisDto.builder()
                .quizzes(preAnalysisDto.getQuizzes())
                .teacherGuides(preAnalysisDto.getTeacherGuides())
                .build();
    }
}