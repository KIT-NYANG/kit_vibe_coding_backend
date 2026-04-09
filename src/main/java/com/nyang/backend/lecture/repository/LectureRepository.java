package com.nyang.backend.lecture.repository;

import com.nyang.backend.lecture.entity.Lecture;
import com.nyang.backend.lectureClass.entity.LectureClass;
import com.nyang.backend.user.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

    // 삭제되지 않은 강의 1건 상세 조회
    Optional<Lecture> findByLectureIdAndIsDeletedFalse(Long lectureId);

    // 특정 강좌에 속한 삭제되지 않은 강의를 페이지 단위로 조회
    Page<Lecture> findByLectureClassAndIsDeletedFalse(LectureClass lectureClass, Pageable pageable);

    // 전체 강의 조회
    Page<Lecture> findAllByIsDeletedFalse(Pageable pageable);

    // 전체 강의 조회 - 특정 강좌 ID에 해당하는 강의만 조회
    Page<Lecture> findByIsDeletedFalseAndLectureClass_LectureClassId(
            Long lectureClassId, Pageable pageable
    );

    // 내 강의 조회
    Page<Lecture> findByTeacherAndIsDeletedFalse(
            Users teacher, Pageable pageable
    );

    // 내 강의 조회 - 특정 강사가 등록한 강의 중 특정 강좌에 속한 강의만 조회
    Page<Lecture> findByTeacherAndIsDeletedFalseAndLectureClass_LectureClassId(
            Users teacher, Long lectureClassId, Pageable pageable
    );

    // 공백 무시 제목 검색
    @Query("""
        SELECT l
        FROM Lecture l
        WHERE l.isDeleted = false
          AND REPLACE(l.title, ' ', '') LIKE CONCAT('%', :keyword, '%')
    """)
    Page<Lecture> findByIsDeletedFalseAndTitleContainingIgnoreSpace(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // 공백 무시 강좌 + 제목 검색
    @Query("""
        SELECT l
        FROM Lecture l
        WHERE l.isDeleted = false
          AND l.lectureClass.lectureClassId = :lectureClassId
          AND REPLACE(l.title, ' ', '') LIKE CONCAT('%', :keyword, '%')
    """)
    Page<Lecture> findByIsDeletedFalseAndLectureClass_LectureClassIdAndTitleContainingIgnoreSpace(
            @Param("lectureClassId") Long lectureClassId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // 공백 무시 내 강의 제목 검색
    @Query("""
        SELECT l
        FROM Lecture l
        WHERE l.isDeleted = false
          AND l.teacher = :teacher
          AND REPLACE(l.title, ' ', '') LIKE CONCAT('%', :keyword, '%')
    """)
    Page<Lecture> findByTeacherAndIsDeletedFalseAndTitleContainingIgnoreSpace(
            @Param("teacher") Users teacher,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // 공백 무시 내 강의 강좌 + 제목 검색
    @Query("""
        SELECT l
        FROM Lecture l
        WHERE l.isDeleted = false
          AND l.teacher = :teacher
          AND l.lectureClass.lectureClassId = :lectureClassId
          AND REPLACE(l.title, ' ', '') LIKE CONCAT('%', :keyword, '%')
    """)
    Page<Lecture> findByTeacherAndIsDeletedFalseAndLectureClass_LectureClassIdAndTitleContainingIgnoreSpace(
            @Param("teacher") Users teacher,
            @Param("lectureClassId") Long lectureClassId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}