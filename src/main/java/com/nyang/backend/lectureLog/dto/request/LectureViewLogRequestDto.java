package com.nyang.backend.lectureLog.dto.request;

import com.nyang.backend.lectureLog.entity.LogEventType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class LectureViewLogRequestDto {

    /**
     * 프론트에서 생성한 sessionId
     */
    private String sessionId;

    private LogEventType eventType;

    private Integer currentTimeSec;
    private Integer fromTimeSec;
    private Integer toTimeSec;
    private Double playbackRate;

    /**
     * 프론트 이벤트 발생 시간
     */
    private LocalDateTime occurredAt;
}