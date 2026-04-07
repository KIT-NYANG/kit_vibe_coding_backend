package com.nyang.backend.lecture.repository;

import com.nyang.backend.lecture.entity.Lecture;
import com.nyang.backend.lectureClass.entity.LectureClass;
import com.nyang.backend.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
    List<Lecture> findAllByIsDeletedFalseOrderByCreatedAtDesc();
    List<Lecture> findByTeacherAndIsDeletedFalseOrderByCreatedAtDesc(Users teacher);
    Optional<Lecture> findByLectureIdAndIsDeletedFalse(Long lectureId);
    List<Lecture> findByLectureClassAndIsDeletedFalseOrderBySequenceAscCreatedAtAsc(LectureClass lectureClass);
}