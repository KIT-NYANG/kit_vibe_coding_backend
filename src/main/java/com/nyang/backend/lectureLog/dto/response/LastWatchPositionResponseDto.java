package com.nyang.backend.lectureLog.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LastWatchPositionResponseDto {
    private Long lectureId;
    private Integer lastPositionSec;
    private String sessionId;
}