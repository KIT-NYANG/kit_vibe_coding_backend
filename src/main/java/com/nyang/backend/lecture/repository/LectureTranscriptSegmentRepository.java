package com.nyang.backend.lecture.repository;

import com.nyang.backend.lecture.entity.LectureTranscriptSegment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureTranscriptSegmentRepository extends JpaRepository<LectureTranscriptSegment, Long> {
    List<LectureTranscriptSegment> findByLecture_LectureIdOrderBySegmentIndexAsc(Long lectureId);
}