package com.nyang.backend.lectureLog.service;

import com.nyang.backend.global.exception.BusinessException;
import com.nyang.backend.global.exception.ErrorCode;
import com.nyang.backend.lecture.entity.Lecture;
import com.nyang.backend.lecture.entity.LectureTranscriptSegment;
import com.nyang.backend.lecture.repository.LectureRepository;
import com.nyang.backend.lectureLog.client.LectureAiClient;
import com.nyang.backend.lectureLog.dto.request.LectureViewLogRequestDto;
import com.nyang.backend.lectureLog.dto.response.*;
import com.nyang.backend.lectureLog.entity.LectureLogAnalysis;
import com.nyang.backend.lectureLog.entity.LectureViewLog;
import com.nyang.backend.lectureLog.entity.LectureViewSession;
import com.nyang.backend.lectureLog.entity.LogEventType;
import com.nyang.backend.lectureLog.repository.LectureLogAnalysisRepository;
import com.nyang.backend.lectureLog.repository.LectureTranscriptSegmentReadRepository;
import com.nyang.backend.lectureLog.repository.LectureViewLogRepository;
import com.nyang.backend.lectureLog.repository.LectureViewSessionRepository;
import com.nyang.backend.user.entity.Users;
import com.nyang.backend.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureLogService {

    private final LectureRepository lectureRepository;
    private final UsersRepository userRepository;
    private final LectureViewLogRepository lectureViewLogRepository;
    private final LectureViewSessionRepository lectureViewSessionRepository;
    private final LectureTranscriptSegmentReadRepository lectureTranscriptSegmentReadRepository;
    private final LectureLogAnalysisRepository lectureLogAnalysisRepository;
    private final LectureAiClient lectureAiClient;

    /**
     * 로그 저장 + 세션 요약 갱신 + 종료 시 자동 aggregate 분석 체크
     */
    @Transactional
    public LectureLogSaveResponseDto saveLectureLog(Long lectureId, String userEmail, LectureViewLogRequestDto requestDto) {
        Users user = getUserByEmail(userEmail);
        Lecture lecture = getLectureById(lectureId);

        if (requestDto.getSessionId() == null || requestDto.getSessionId().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        LectureViewSession session = lectureViewSessionRepository.findBySessionId(requestDto.getSessionId())
                .orElseGet(() -> LectureViewSession.create(
                        user,
                        lecture,
                        requestDto.getSessionId(),
                        requestDto.getOccurredAt() != null ? requestDto.getOccurredAt() : LocalDateTime.now()
                ));

        LectureViewLog log = LectureViewLog.builder()
                .user(user)
                .lecture(lecture)
                .sessionId(requestDto.getSessionId())
                .eventType(requestDto.getEventType())
                .currentTimeSec(requestDto.getCurrentTimeSec())
                .fromTimeSec(requestDto.getFromTimeSec())
                .toTimeSec(requestDto.getToTimeSec())
                .playbackRate(requestDto.getPlaybackRate())
                .occurredAt(requestDto.getOccurredAt() != null ? requestDto.getOccurredAt() : LocalDateTime.now())
                .build();

        lectureViewLogRepository.save(log);

        session.applyLog(
                requestDto.getEventType(),
                requestDto.getCurrentTimeSec(),
                requestDto.getFromTimeSec(),
                requestDto.getToTimeSec(),
                requestDto.getPlaybackRate()
        );

        lectureViewSessionRepository.save(session);

        // 세션 종료 시점에만 자동 분석 체크
        if (requestDto.getEventType() == LogEventType.ENDED
                || requestDto.getEventType() == LogEventType.PAGE_EXIT) {
            triggerAggregateAnalysisIfNeeded(lectureId);
        }

        return LectureLogSaveResponseDto.builder()
                .lectureId(lectureId)
                .sessionId(session.getSessionId())
                .lastPositionSec(session.getLastPositionSec())
                .message("강의 로그 저장 완료")
                .build();
    }

    /**
     * 이어보기용 마지막 시청 위치 조회
     */
    public LastWatchPositionResponseDto getLastWatchPosition(Long lectureId, String userEmail) {
        Users user = getUserByEmail(userEmail);

        LectureViewSession latestSession = lectureViewSessionRepository
                .findTopByUser_UserIdAndLecture_LectureIdOrderByUpdatedAtDesc(user.getUserId(), lectureId)
                .orElse(null);

        if (latestSession == null) {
            return LastWatchPositionResponseDto.builder()
                    .lectureId(lectureId)
                    .lastPositionSec(0)
                    .sessionId(null)
                    .build();
        }

        return LastWatchPositionResponseDto.builder()
                .lectureId(lectureId)
                .lastPositionSec(latestSession.getLastPositionSec())
                .sessionId(latestSession.getSessionId())
                .build();
    }

    /**
     * STT 기반 사전 분석
     */
    @Transactional
    public LecturePreAnalysisResponseDto requestPreAnalysis(Long lectureId, String additionalPrompt) {
        Lecture lecture = getLectureById(lectureId);

        List<LectureTranscriptSegment> segments =
                lectureTranscriptSegmentReadRepository.findByLecture_LectureIdOrderBySegmentIndexAsc(lectureId);

        if (segments.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        LectureLogAnalysis analysis = lectureLogAnalysisRepository.findByLecture_LectureId(lectureId)
                .orElseGet(() -> lectureLogAnalysisRepository.save(LectureLogAnalysis.create(lecture)));

        LecturePreAnalysisResponseDto responseDto =
                lectureAiClient.requestPreAnalysis(lectureId, segments, additionalPrompt);

        String resultJson = lectureAiClient.toJson(responseDto);
        analysis.updatePreResult(resultJson);

        return LecturePreAnalysisResponseDto.builder()
                .analysisId(analysis.getId())
                .lectureId(responseDto.getLectureId())
                .quizzes(responseDto.getQuizzes())
                .teacherGuides(responseDto.getTeacherGuides())
                .build();
    }

    /**
     * 수동 aggregate 분석 API
     */
    @Transactional
    public LectureAggregateAnalysisResponseDto requestAggregateAnalysis(Long lectureId) {
        Lecture lecture = getLectureById(lectureId);

        LectureLogAnalysis analysis = lectureLogAnalysisRepository.findByLecture_LectureId(lectureId)
                .orElseGet(() -> lectureLogAnalysisRepository.save(LectureLogAnalysis.create(lecture)));

        long validSessionCount = getValidSessionCount(lectureId);

        LectureAggregateAnalysisResponseDto responseDto =
                executeAggregateAnalysis(lectureId);

        String resultJson = lectureAiClient.toJson(responseDto);
        analysis.updateAggregateResult(resultJson, (int) validSessionCount);

        return LectureAggregateAnalysisResponseDto.builder()
                .analysisId(analysis.getId())
                .lectureId(responseDto.getLectureId())
                .analyzedLogCount(responseDto.getAnalyzedLogCount())
                .quizzes(responseDto.getQuizzes())
                .teacherGuides(responseDto.getTeacherGuides())
                .build();
    }

    /**
     * 자동 aggregate 분석 트리거
     * 유효 session 수가 10 / 100 / 1000 단위가 되었을 때만 실행
     */
    @Transactional
    public void triggerAggregateAnalysisIfNeeded(Long lectureId) {
        Lecture lecture = getLectureById(lectureId);

        long validSessionCount = getValidSessionCount(lectureId);
        int triggerUnit = resolveAggregateTriggerUnit(validSessionCount);

        if (triggerUnit == 0) {
            return;
        }

        LectureLogAnalysis analysis = lectureLogAnalysisRepository.findByLecture_LectureId(lectureId)
                .orElseGet(() -> lectureLogAnalysisRepository.save(LectureLogAnalysis.create(lecture)));

        if (analysis.getLastAggregatedSessionCount() >= triggerUnit) {
            return;
        }

        LectureAggregateAnalysisResponseDto responseDto = executeAggregateAnalysis(lectureId);
        String resultJson = lectureAiClient.toJson(responseDto);

        analysis.updateAggregateResult(resultJson, triggerUnit);
    }

    /**
     * 실제 aggregate 분석 수행 본체
     */
    private LectureAggregateAnalysisResponseDto executeAggregateAnalysis(Long lectureId) {
        List<LectureViewSession> allSessions = lectureViewSessionRepository.findByLecture_LectureId(lectureId);

        List<LectureViewSession> validSessions = allSessions.stream()
                .filter(LectureViewSession::isValidForAggregateAnalysis)
                .toList();

        if (validSessions.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        Set<String> validSessionIds = validSessions.stream()
                .map(LectureViewSession::getSessionId)
                .collect(Collectors.toSet());

        List<LectureViewLog> allLogs = lectureViewLogRepository.findByLecture_LectureIdOrderByOccurredAtAsc(lectureId);

        List<LectureViewLog> logs = allLogs.stream()
                .filter(log -> validSessionIds.contains(log.getSessionId()))
                .toList();

        if (logs.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        List<LectureTranscriptSegment> allSegments =
                lectureTranscriptSegmentReadRepository.findByLecture_LectureIdOrderBySegmentIndexAsc(lectureId);

        if (allSegments.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        List<CandidateRangeDto> candidateRanges = buildCandidateRanges(logs);

        if (candidateRanges.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        List<LectureTranscriptSegment> filteredSegments =
                filterTranscriptSegments(allSegments, candidateRanges);

        LectureAggregateAnalysisResponseDto responseDto =
                lectureAiClient.requestAggregateAnalysis(lectureId, candidateRanges, filteredSegments);

        return LectureAggregateAnalysisResponseDto.builder()
                .analysisId(null)
                .lectureId(responseDto.getLectureId())
                .analyzedLogCount(logs.size())
                .quizzes(responseDto.getQuizzes())
                .teacherGuides(responseDto.getTeacherGuides())
                .build();
    }

    /**
     * 유효 session 수 계산
     */
    private long getValidSessionCount(Long lectureId) {
        return lectureViewSessionRepository.findByLecture_LectureId(lectureId).stream()
                .filter(LectureViewSession::isValidForAggregateAnalysis)
                .count();
    }

    /**
     * 10, 100, 1000 ... 단위 자동 분석 기준 계산
     */
    private int resolveAggregateTriggerUnit(long validSessionCount) {
        if (validSessionCount < 10) {
            return 0;
        }

        int unit = 10;
        while (validSessionCount >= (long) unit * 10) {
            unit *= 10;
        }
        return unit;
    }

    private static class BucketStat {
        int startSec;
        int endSec;
        int pauseCount;
        int seekBackCount;
        Set<Long> userIds = new HashSet<>();

        BucketStat(int startSec, int endSec) {
            this.startSec = startSec;
            this.endSec = endSec;
        }

        double score() {
            return (pauseCount * 1.0) + (seekBackCount * 2.0) + (userIds.size() * 1.5);
        }
    }

    private List<CandidateRangeDto> buildCandidateRanges(List<LectureViewLog> logs) {
        final int BUCKET_SIZE = 30;

        int maxTime = logs.stream()
                .map(log -> {
                    Integer current = log.getCurrentTimeSec();
                    Integer from = log.getFromTimeSec();
                    Integer to = log.getToTimeSec();

                    int a = current != null ? current : 0;
                    int b = from != null ? from : 0;
                    int c = to != null ? to : 0;

                    return Math.max(a, Math.max(b, c));
                })
                .max(Integer::compareTo)
                .orElse(0);

        int bucketCount = (maxTime / BUCKET_SIZE) + 1;
        List<BucketStat> buckets = new ArrayList<>();

        for (int i = 0; i < bucketCount; i++) {
            int start = i * BUCKET_SIZE;
            int end = start + BUCKET_SIZE;
            buckets.add(new BucketStat(start, end));
        }

        for (LectureViewLog log : logs) {
            Long userId = log.getUser().getUserId();

            switch (log.getEventType()) {
                case PAUSE -> {
                    if (log.getCurrentTimeSec() != null) {
                        int index = log.getCurrentTimeSec() / BUCKET_SIZE;
                        if (index < buckets.size()) {
                            BucketStat bucket = buckets.get(index);
                            bucket.pauseCount += 1;
                            bucket.userIds.add(userId);
                        }
                    }
                }

                case SEEK -> {
                    Integer from = log.getFromTimeSec();
                    Integer to = log.getToTimeSec();

                    if (from != null && to != null && from > to) {
                        int startBucket = to / BUCKET_SIZE;
                        int endBucket = from / BUCKET_SIZE;

                        for (int i = startBucket; i <= endBucket && i < buckets.size(); i++) {
                            BucketStat bucket = buckets.get(i);
                            bucket.seekBackCount += 1;
                            bucket.userIds.add(userId);
                        }
                    }
                }

                default -> {
                }
            }
        }

        List<BucketStat> strongBuckets = buckets.stream()
                .filter(bucket -> bucket.score() >= 5.0)
                .toList();

        List<CandidateRangeDto> merged = mergeAdjacentBuckets(strongBuckets);

        return merged.stream()
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(3)
                .toList();
    }

    private List<CandidateRangeDto> mergeAdjacentBuckets(List<BucketStat> strongBuckets) {
        List<CandidateRangeDto> result = new ArrayList<>();

        if (strongBuckets.isEmpty()) {
            return result;
        }

        BucketStat current = strongBuckets.get(0);

        int mergedStart = current.startSec;
        int mergedEnd = current.endSec;
        int mergedPause = current.pauseCount;
        int mergedSeek = current.seekBackCount;
        Set<Long> mergedUsers = new HashSet<>(current.userIds);

        for (int i = 1; i < strongBuckets.size(); i++) {
            BucketStat next = strongBuckets.get(i);

            if (mergedEnd == next.startSec) {
                mergedEnd = next.endSec;
                mergedPause += next.pauseCount;
                mergedSeek += next.seekBackCount;
                mergedUsers.addAll(next.userIds);
            } else {
                result.add(toCandidateRangeDto(mergedStart, mergedEnd, mergedPause, mergedSeek, mergedUsers));

                mergedStart = next.startSec;
                mergedEnd = next.endSec;
                mergedPause = next.pauseCount;
                mergedSeek = next.seekBackCount;
                mergedUsers = new HashSet<>(next.userIds);
            }
        }

        result.add(toCandidateRangeDto(mergedStart, mergedEnd, mergedPause, mergedSeek, mergedUsers));
        return result;
    }

    private CandidateRangeDto toCandidateRangeDto(
            int startSec,
            int endSec,
            int pauseCount,
            int seekBackCount,
            Set<Long> userIds
    ) {
        List<String> reasons = new ArrayList<>();

        if (pauseCount > 0) reasons.add("pause_many");
        if (seekBackCount > 0) reasons.add("seek_back_many");

        double score = (pauseCount * 1.0) + (seekBackCount * 2.0) + (userIds.size() * 1.5);

        return CandidateRangeDto.builder()
                .startSec(startSec)
                .endSec(endSec)
                .pauseCount(pauseCount)
                .seekBackCount(seekBackCount)
                .affectedUserCount(userIds.size())
                .score(score)
                .reasons(reasons)
                .build();
    }

    private List<LectureTranscriptSegment> filterTranscriptSegments(
            List<LectureTranscriptSegment> allSegments,
            List<CandidateRangeDto> candidateRanges
    ) {
        return allSegments.stream()
                .filter(segment -> {
                    int segStart = Math.toIntExact(segment.getStartMs() / 1000);
                    int segEnd = Math.toIntExact(segment.getEndMs() / 1000);

                    return candidateRanges.stream().anyMatch(range ->
                            segStart <= range.getEndSec() && segEnd >= range.getStartSec()
                    );
                })
                .toList();
    }

    private Users getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private Lecture getLectureById(Long lectureId) {
        return lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));
    }
}