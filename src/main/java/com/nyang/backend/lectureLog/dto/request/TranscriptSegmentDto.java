package com.nyang.backend.lectureLog.dto.request;

import com.nyang.backend.lecture.entity.LectureTranscriptSegment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptSegmentDto {
    private Integer index;
    private Double start;
    private Double end;
    private String text;

    public static TranscriptSegmentDto from(LectureTranscriptSegment segment) {
        return TranscriptSegmentDto.builder()
                .index(segment.getSegmentIndex())
                .start(segment.getStartMs() / 1000.0)
                .end(segment.getEndMs() / 1000.0)
                .text(segment.getText())
                .build();
    }
}