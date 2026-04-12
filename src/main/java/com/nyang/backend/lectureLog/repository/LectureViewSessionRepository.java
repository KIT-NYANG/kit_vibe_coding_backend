package com.nyang.backend.lectureLog.repository;

import com.nyang.backend.lectureLog.entity.LectureViewSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LectureViewSessionRepository extends JpaRepository<LectureViewSession, Long> {

    Optional<LectureViewSession> findBySessionId(String sessionId);

    Optional<LectureViewSession> findTopByUser_UserIdAndLecture_LectureIdOrderByUpdatedAtDesc(Long userId, Long lectureId);

    List<LectureViewSession> findByLecture_LectureId(Long lectureId);

    long countByLecture_LectureIdAndCompletedTrue(Long lectureId);
}