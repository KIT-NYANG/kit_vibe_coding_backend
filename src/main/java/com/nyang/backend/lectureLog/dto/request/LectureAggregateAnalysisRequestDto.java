package com.nyang.backend.lectureLog.dto.request;

import com.nyang.backend.lectureLog.dto.response.CandidateRangeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureAggregateAnalysisRequestDto {
    private List<CandidateRangeDto> candidateRanges;
    private List<TranscriptSegmentDto> segments;
}