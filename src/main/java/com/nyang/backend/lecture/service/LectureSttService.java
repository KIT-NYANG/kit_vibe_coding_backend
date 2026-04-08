package com.nyang.backend.lecture.service;

import com.nyang.backend.global.exception.BusinessException;
import com.nyang.backend.global.exception.ErrorCode;
import com.nyang.backend.lecture.dto.SttResponseDto;
import com.nyang.backend.lecture.entity.Lecture;
import com.nyang.backend.lecture.entity.LectureTranscriptSegment;
import com.nyang.backend.lecture.repository.LectureRepository;
import com.nyang.backend.lecture.repository.LectureTranscriptSegmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LectureSttService {

    private final LectureRepository lectureRepository;
    private final LectureTranscriptSegmentRepository segmentRepository;
    private final SttClientService sttClientService;
// 기존 비동기-> 동기 방식 처리 STT 까지 완료 후 업로드
//    public void requestSttAsync(Long lectureId, File tempFile) {
//        Lecture lecture = lectureRepository.findById(lectureId)
//                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));
//
//        lecture.markSttProcessing();
//        lectureRepository.save(lecture);
//
//        sttClientService.sendToSttServer(tempFile)
//                .subscribe(
//                        response -> {
//                            try {
//                                saveTranscriptResult(lectureId, response);
//                            } finally {
//                                deleteTempFile(tempFile);
//                            }
//                        },
//                        error -> {
//                            try {
//                                markFailed(lectureId, error);
//                            } finally {
//                                deleteTempFile(tempFile);
//                            }
//                        }
//                );
//    }
@Transactional
public void requestSttAndSave(Long lectureId, File tempFile) {
    Lecture lecture = lectureRepository.findById(lectureId)
            .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));

    try {
        lecture.markSttProcessing();
        lectureRepository.save(lecture);

        SttResponseDto response = sttClientService.sendToSttServer(tempFile);

        List<LectureTranscriptSegment> segments = response.getSegments().stream()
                .map(segment -> LectureTranscriptSegment.builder()
                        .lecture(lecture)
                        .segmentIndex(segment.getIndex())
                        .startMs(toMs(segment.getStart()))
                        .endMs(toMs(segment.getEnd()))
                        .text(segment.getText())
                        .build())
                .toList();

        segmentRepository.saveAll(segments);

        lecture.markSttCompleted(
                response.getFullText(),
                response.getLanguage(),
                LocalDateTime.now()
        );
        lectureRepository.save(lecture);

    } catch (Exception e) {
        lecture.markSttFailed(e.getMessage());
        lectureRepository.save(lecture);

        log.error("STT failed. lectureId={}, message={}", lectureId, e.getMessage(), e);
        throw new BusinessException(ErrorCode.STT_PROCESS_FAILED);
    } finally {
        deleteTempFile(tempFile);
    }
}
    @Transactional
    public void saveTranscriptResult(Long lectureId, SttResponseDto response) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));

        List<LectureTranscriptSegment> segments = response.getSegments().stream()
                .map(segment -> LectureTranscriptSegment.builder()
                        .lecture(lecture)
                        .segmentIndex(segment.getIndex())
                        .startMs(toMs(segment.getStart()))
                        .endMs(toMs(segment.getEnd()))
                        .text(segment.getText())
                        .build())
                .toList();

        segmentRepository.saveAll(segments);

        lecture.markSttCompleted(
                response.getFullText(),
                response.getLanguage(),
                LocalDateTime.now()
        );
        lectureRepository.save(lecture);
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