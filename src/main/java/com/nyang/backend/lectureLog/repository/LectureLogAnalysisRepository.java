package com.nyang.backend.lectureLog.repository;

import com.nyang.backend.lectureLog.entity.LectureLogAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LectureLogAnalysisRepository extends JpaRepository<LectureLogAnalysis, Long> {

    Optional<LectureLogAnalysis> findByLecture_LectureId(Long lectureId);
}