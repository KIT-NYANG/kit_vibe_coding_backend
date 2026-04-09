package com.nyang.backend.lectureLog.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 1차 버전에서는 lectureId만으로도 충분할 수 있다.
 * 추후 프롬프트 옵션 등을 받고 싶으면 여기 추가하면 된다.
 */
@Getter
@NoArgsConstructor
public class LecturePreAnalysisRequestDto {
    private String additionalPrompt;
}