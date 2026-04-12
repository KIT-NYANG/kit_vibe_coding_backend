package com.nyang.backend.lectureLog.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyang.backend.global.exception.BusinessException;
import com.nyang.backend.global.exception.ErrorCode;
import com.nyang.backend.lecture.entity.LectureTranscriptSegment;
import com.nyang.backend.lectureLog.dto.request.LectureAggregateAnalysisRequestDto;
import com.nyang.backend.lectureLog.dto.request.TranscriptSegmentDto;
import com.nyang.backend.lectureLog.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * 지금은 실제 AI 서버 호출 대신 임시 mock 응답을 주는 클라이언트
 * 나중에 WebClient로 교체하면 된다.
 */
@Component
@RequiredArgsConstructor
public class LectureAiClient {

    private final ObjectMapper objectMapper;
    private final WebClient lectureAiWebClient;

    /**
     * STT segment를 바탕으로
     * - 퀴즈 삽입 위치
     * - 문제/정답/해설
     * - 선생님용 가이드
     * 를 생성한다고 가정
     */
    @Qualifier("lectureAiWebClient")
    public LectureAggregateAnalysisResponseDto requestAggregateAnalysis(
            List<CandidateRangeDto> candidateRanges,
            List<LectureTranscriptSegment> transcriptSegments
    ) {
        List<TranscriptSegmentDto> segmentDtos = transcriptSegments.stream()
                .map(TranscriptSegmentDto::from)
                .toList();

        LectureAggregateAnalysisRequestDto requestDto =
                LectureAggregateAnalysisRequestDto.builder()
                        .candidateRanges(candidateRanges)
                        .segments(segmentDtos)
                        .build();

        LectureAggregateAnalysisResponseDto response = lectureAiWebClient.post()
                .uri("/api/analysis/aggregate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(LectureAggregateAnalysisResponseDto.class)
                .block();

        if (response == null) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return response;
    }
    /**
     * DB 저장용 JSON 직렬화
     */
    public String toJson(Object responseDto) {
        try {
            return objectMapper.writeValueAsString(responseDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("AI 응답 JSON 변환 실패", e);
        }
    }
}