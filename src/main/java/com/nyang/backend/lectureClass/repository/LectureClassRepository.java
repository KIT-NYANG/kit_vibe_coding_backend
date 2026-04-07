package com.nyang.backend.lectureClass.repository;

import com.nyang.backend.lectureClass.entity.LectureClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LectureClassRepository extends JpaRepository<LectureClass, Long> {

    // 삭제되지 않은 강좌 1개 조회
    Optional<LectureClass> findByLectureClassIdAndIsDeletedFalse(Long lectureClassId);

    // 특정 강사가 등록한 강좌 목록 조회 - 아직 사용 안 합니다.
    List<LectureClass> findByTeacher_UserIdAndIsDeletedFalse(Long teacherId);
}