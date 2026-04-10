package com.nyang.backend.lecture.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyang.backend.global.exception.BusinessException;
import com.nyang.backend.global.exception.ErrorCode;
import com.nyang.backend.lecture.dto.PreAnalysisDto;
import com.nyang.backend.lecture.dto.SttResponseDto;
import com.nyang.backend.lecture.dto.SttSegmentDto;
import com.nyang.backend.lecture.entity.Lecture;
import com.nyang.backend.lecture.entity.LectureTranscriptSegment;
import com.nyang.backend.lecture.repository.LectureRepository;
import com.nyang.backend.lecture.repository.LectureTranscriptSegmentRepository;
import com.nyang.backend.lectureLog.entity.LectureLogAnalysis;
import com.nyang.backend.lectureLog.repository.LectureLogAnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LectureSttService {

    private final LectureRepository lectureRepository;
    private final LectureTranscriptSegmentRepository segmentRepository;
    private final SttClientService sttClientService;
    private final LectureLogAnalysisRepository lectureLogAnalysisRepository;
    private final ObjectMapper objectMapper;

    public void requestSttAsync(Long lectureId, File tempFile) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));

        lecture.markSttProcessing();
        lectureRepository.save(lecture);

        sttClientService.sendToSttServer(tempFile)
                .subscribe(
                        response -> {
                            try {
                                saveTranscriptResult(lectureId, response);
                            } finally {
                                deleteTempFile(tempFile);
                            }
                        },
                        error -> {
                            try {
                                markFailed(lectureId, error);
                            } finally {
                                deleteTempFile(tempFile);
                            }
                        }
                );
    }
    // 기존 비동기-> 동기 방식 처리 STT 까지 완료 후 업로드 -> 다시 비동기
//@Transactional
//public void requestSttAndSave(Long lectureId, File tempFile) {
//    Lecture lecture = lectureRepository.findById(lectureId)
//            .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));
//
//    try {
//        lecture.markSttProcessing();
//        lectureRepository.save(lecture);
//
//        SttResponseDto response = sttClientService.sendToSttServer(tempFile);
//
//        List<LectureTranscriptSegment> segments = response.getSegments().stream()
//                .map(segment -> LectureTranscriptSegment.builder()
//                        .lecture(lecture)
//                        .segmentIndex(segment.getIndex())
//                        .startMs(toMs(segment.getStart()))
//                        .endMs(toMs(segment.getEnd()))
//                        .text(segment.getText())
//                        .build())
//                .toList();
//
//        segmentRepository.saveAll(segments);
//
//        lecture.markSttCompleted(
//                response.getFullText(),
//                response.getLanguage(),
//                LocalDateTime.now()
//        );
//        lectureRepository.save(lecture);
//
//    } catch (Exception e) {
//        lecture.markSttFailed(e.getMessage());
//        lectureRepository.save(lecture);
//
//        log.error("STT failed. lectureId={}, message={}", lectureId, e.getMessage(), e);
//        throw new BusinessException(ErrorCode.STT_PROCESS_FAILED);
//    } finally {
//        deleteTempFile(tempFile);
//    }
//}
    @Transactional
    public void saveTranscriptResult(Long lectureId, SttResponseDto response) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));

        List<LectureTranscriptSegment> mergedSegments = mergeSegmentsForCaption(lecture, response.getSegments());

        segmentRepository.saveAll(mergedSegments);

        String summaryText = null;
        String summaryKeywords = null;

        if (response.getSummarize() != null) {
            summaryText = response.getSummarize().getSummaryText();

            if (response.getSummarize().getKeywords() != null) {
                summaryKeywords = String.join(",", response.getSummarize().getKeywords());
            }
        }

        lecture.markSttCompleted(
                response.getFullText(),
                response.getLanguage(),
                summaryText,
                summaryKeywords,
                LocalDateTime.now()
        );
        lectureRepository.save(lecture);
        savePreAnalysis(lecture, response.getPreAnalysis());
    }

    private List<LectureTranscriptSegment> mergeSegmentsForCaption(
            Lecture lecture,
            List<SttSegmentDto> rawSegments
    ) {
        if (rawSegments == null || rawSegments.isEmpty()) {
            return List.of();
        }

        final double MAX_GAP_SEC = 0.8;          // 앞뒤 자막 간 간격이 이하면 합침
        final int SHORT_TEXT_LENGTH = 12;        // 너무 짧은 자막이면 합침 시도
        final int MAX_TEXT_LENGTH = 45;          // 너무 길어지면 합치지 않음
        final double MAX_DURATION_SEC = 8.0;     // 한 자막 구간이 너무 길어지면 끊음

        List<LectureTranscriptSegment> result = new ArrayList<>();

        SttSegmentDto current = rawSegments.get(0);
        int mergedIndex = 0;

        double currentStart = current.getStart();
        double currentEnd = current.getEnd();
        String currentText = normalizeText(current.getText());

        for (int i = 1; i < rawSegments.size(); i++) {
            SttSegmentDto next = rawSegments.get(i);

            double nextStart = next.getStart();
            double nextEnd = next.getEnd();
            String nextText = normalizeText(next.getText());

            double gap = nextStart - currentEnd;
            String mergedTextCandidate = currentText + " " + nextText;
            double mergedDuration = nextEnd - currentStart;

            boolean shouldMerge =
                    gap <= MAX_GAP_SEC &&
                            (
                                    currentText.length() <= SHORT_TEXT_LENGTH ||
                                            nextText.length() <= SHORT_TEXT_LENGTH
                            ) &&
                            mergedTextCandidate.length() <= MAX_TEXT_LENGTH &&
                            mergedDuration <= MAX_DURATION_SEC;

            if (shouldMerge) {
                currentEnd = nextEnd;
                currentText = mergedTextCandidate.trim();
            } else {
                result.add(LectureTranscriptSegment.builder()
                        .lecture(lecture)
                        .segmentIndex(mergedIndex++)
                        .startMs(toMs(currentStart))
                        .endMs(toMs(currentEnd))
                        .text(currentText)
                        .build());

                currentStart = nextStart;
                currentEnd = nextEnd;
                currentText = nextText;
            }
        }

        result.add(LectureTranscriptSegment.builder()
                .lecture(lecture)
                .segmentIndex(mergedIndex)
                .startMs(toMs(currentStart))
                .endMs(toMs(currentEnd))
                .text(currentText)
                .build());

        return result;
    }

    private String normalizeText(String text) {
        if (text == null) {
            return "";
        }
        return text.trim().replaceAll("\\s+", " ");
    }
    private void savePreAnalysis(Lecture lecture, PreAnalysisDto preAnalysis) {
        if (preAnalysis == null) {
            return;
        }
        LectureLogAnalysis analysis = lectureLogAnalysisRepository.findByLecture_LectureId(lecture.getLectureId())
                .orElseGet(() -> LectureLogAnalysis.create(lecture));

        try {
            String preResultJson = objectMapper.writeValueAsString(preAnalysis);
            analysis.updatePreResult(preResultJson);
            lectureLogAnalysisRepository.save(analysis);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_SERIALIZATION_ERROR);
        }
    }

    @Transactional
    public void markFailed(Long lectureId, Throwable error) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FILE_CONVERSION_FAILED));

        lecture.markSttFailed(error.getMessage());
        lectureRepository.save(lecture);
        log.error("STT failed. lectureId={}, message={}", lectureId, error.getMessage(), error);
    }

    private Long toMs(Double second) {
        if (second == null) return 0L;
        return Math.round(second * 1000);
    }

    private void deleteTempFile(File tempFile) {
        if (tempFile != null && tempFile.exists()) {
            boolean deleted = tempFile.delete();
            if (!deleted) {
                log.warn("임시파일 삭제 실패: {}", tempFile.getAbsolutePath());
            }
        }
    }
}