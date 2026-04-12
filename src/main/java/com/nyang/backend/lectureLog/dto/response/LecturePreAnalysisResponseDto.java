package com.nyang.backend.lectureLog.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LecturePreAnalysisResponseDto {
    private Long analysisId;
    private Long lectureId;
    private List<QuizItemResponseDto> quizzes;
    private List<TeacherGuideResponseDto> teacherGuides;
}