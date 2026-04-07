package com.nyang.backend.lecture.repository;

import com.nyang.backend.lecture.entity.Lecture;
import com.nyang.backend.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
    List<Lecture> findAllByOrderByCreatedAtDesc();
    List<Lecture> findByTeacherOrderByCreatedAtDesc(Users teacher);
}