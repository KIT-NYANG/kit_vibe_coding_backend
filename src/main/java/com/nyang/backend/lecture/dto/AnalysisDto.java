package com.nyang.backend.lecture.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AnalysisDto {
    private List<SttQuizDto> quizzes;
    private List<SttTeacherGuideDto> teacherGuides;
}