package com.nyang.backend.lecture.dto;

import com.nyang.backend.lecture.entity.LectureTranscriptSegment;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LectureSegmentResponseDto {

    private Long startMs;
    private Long endMs;
    private String text;

    public static LectureSegmentResponseDto from(LectureTranscriptSegment segment) {
        return LectureSegmentResponseDto.builder()
                .startMs(segment.getStartMs())
                .endMs(segment.getEndMs())
                .text(segment.getText())
                .build();
    }
}