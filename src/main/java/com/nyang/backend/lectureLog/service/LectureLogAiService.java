package com.nyang.backend.lectureLog.service;

import com.nyang.backend.lectureLog.dto.response.LecturePreAnalysisResponseDto;

public interface LectureLogAiService {
    LecturePreAnalysisResponseDto requestPreAnalysis(Long lectureId, String additionalPrompt);
}