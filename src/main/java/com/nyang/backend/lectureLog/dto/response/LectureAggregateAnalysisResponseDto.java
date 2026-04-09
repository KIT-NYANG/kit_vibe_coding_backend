package com.nyang.backend.lectureLog.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LectureAggregateAnalysisResponseDto {

    private Long analysisId;
    private Long lectureId;
    private Integer analyzedLogCount;
    private List<QuizItemResponseDto> quizzes;
    private List<TeacherGuideResponseDto> teacherGuides;
}