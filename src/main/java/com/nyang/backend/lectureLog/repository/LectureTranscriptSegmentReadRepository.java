package com.nyang.backend.lectureLog.repository;

import com.nyang.backend.lecture.entity.LectureTranscriptSegment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureTranscriptSegmentReadRepository extends JpaRepository<LectureTranscriptSegment, Long> {

    List<LectureTranscriptSegment> findByLecture_LectureIdOrderBySegmentIndexAsc(Long lectureId);
}