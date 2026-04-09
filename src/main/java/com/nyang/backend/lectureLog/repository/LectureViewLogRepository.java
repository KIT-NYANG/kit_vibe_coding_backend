package com.nyang.backend.lectureLog.repository;

import com.nyang.backend.lectureLog.entity.LectureViewLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureViewLogRepository extends JpaRepository<LectureViewLog, Long> {

    List<LectureViewLog> findByLecture_LectureIdOrderByOccurredAtAsc(Long lectureId);
}