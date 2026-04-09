package com.nyang.backend.lectureLog.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyang.backend.lecture.entity.LectureTranscriptSegment;
import com.nyang.backend.lectureLog.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 지금은 실제 AI 서버 호출 대신 임시 mock 응답을 주는 클라이언트
 * 나중에 WebClient로 교체하면 된다.
 */
@Component
@RequiredArgsConstructor
public class LectureAiClient {

    private final ObjectMapper objectMapper;

    /**
     * STT segment를 바탕으로
     * - 퀴즈 삽입 위치
     * - 문제/정답/해설
     * - 선생님용 가이드
     * 를 생성한다고 가정
     */
    public LecturePreAnalysisResponseDto requestPreAnalysis(Long lectureId, List<LectureTranscriptSegment> segments, String additionalPrompt) {
        // TODO:
        // 여기서 나중에는 Python/FastAPI 서버로 WebClient 요청 보내면 된다.
        // 지금은 일단 mock 결과를 리턴

        return LecturePreAnalysisResponseDto.builder()
                .lectureId(lectureId)
                .quizzes(List.of(
                        QuizItemResponseDto.builder()
                                .quizInsertTimeSec(545)
                                .question("Round Robin 방식의 특징으로 가장 알맞은 것은?")
                                .answer("각 프로세스에 동일한 시간 할당량을 부여한다.")
                                .explanation("이 구간의 핵심은 선점형 스케줄링과 시간 할당 방식입니다.")
                                .supplementalDescription("Round Robin은 CPU를 공정하게 나누기 위해 일정 시간 단위로 프로세스를 번갈아 실행합니다.")
                                .build()
                ))
                .teacherGuides(List.of(
                        TeacherGuideResponseDto.builder()
                                .predictedDifficultSection("08:00~09:00")
                                .predictedReason("개념 설명이 추상적이고 처음 접하는 학생에게 스케줄링 방식 구분이 어려울 수 있습니다.")
                                .improvementSuggestion("예시 프로세스를 3개 정도 두고 실제 순서를 그림처럼 보여주면 이해도가 높아질 수 있습니다.")
                                .build()
                ))
                .build();
    }
    public LectureAggregateAnalysisResponseDto requestAggregateAnalysis(
            Long lectureId,
            List<CandidateRangeDto> candidateRanges,
            List<LectureTranscriptSegment> transcriptSegments
    ) {
        // TODO:
        // 나중에는 WebClient로 Python 서버 호출
        // 지금은 mock 응답

        return LectureAggregateAnalysisResponseDto.builder()
                .lectureId(lectureId)
                .analyzedLogCount(candidateRanges.size())
                .quizzes(List.of(
                        QuizItemResponseDto.builder()
                                .quizInsertTimeSec(125)
                                .question("Round Robin 방식의 특징으로 가장 알맞은 것은?")
                                .answer("각 프로세스에 동일한 시간 할당량을 부여한다.")
                                .explanation("학생들이 반복해서 멈추고 되돌려본 구간의 핵심 개념입니다.")
                                .supplementalDescription("Round Robin은 각 프로세스에 일정한 시간 조각을 주고 순서대로 CPU를 할당하는 방식입니다.")
                                .build(),
                        QuizItemResponseDto.builder()
                                .quizInsertTimeSec(545)
                                .question("선점형 스케줄링의 특징은 무엇인가요?")
                                .answer("실행 중인 프로세스를 중단하고 다른 프로세스에 CPU를 할당할 수 있다.")
                                .explanation("이 구간은 선점 여부를 헷갈려하는 학생이 많아 확인용 퀴즈를 생성했습니다.")
                                .supplementalDescription("선점형은 운영체제가 필요한 시점에 현재 작업을 멈추고 다른 작업으로 전환할 수 있습니다.")
                                .build()
                ))
                .teacherGuides(List.of(
                        TeacherGuideResponseDto.builder()
                                .predictedDifficultSection("01:30~02:30")
                                .predictedReason("여러 학생이 pause 및 seek backward를 반복한 구간입니다.")
                                .improvementSuggestion("Round Robin과 FCFS를 표로 비교하면 개념 구분에 도움이 됩니다.")
                                .build()
                ))
                .build();
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