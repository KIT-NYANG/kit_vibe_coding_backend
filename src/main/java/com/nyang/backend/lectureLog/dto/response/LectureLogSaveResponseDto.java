package com.nyang.backend.lectureLog.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LectureLogSaveResponseDto {
    private Long lectureId;
    private String sessionId;
    private Integer lastPositionSec;
    private String message;
}