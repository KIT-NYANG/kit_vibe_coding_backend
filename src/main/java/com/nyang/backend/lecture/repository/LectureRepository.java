package com.nyang.backend.lecture.repository;

import com.nyang.backend.lecture.entity.Lecture;
import com.nyang.backend.lectureClass.entity.LectureClass;
import com.nyang.backend.user.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
    // 삭제되지 않은 전체 강의를 생성일 기준 내림차순으로 조회
    List<Lecture> findAllByIsDeletedFalseOrderByCreatedAtDesc();

    // 특정 강사가 등록한 삭제되지 않은 강의를 생성일 기준 내림차순으로 조회
    List<Lecture> findByTeacherAndIsDeletedFalseOrderByCreatedAtDesc(Users teacher);

    // 삭제되지 않은 강의 1건 상세 조회
    Optional<Lecture> findByLectureIdAndIsDeletedFalse(Long lectureId);

    // 특정 강좌에 속한 삭제되지 않은 강의를 생성일 기준 오름차순으로 조회
    List<Lecture> findByLectureClassAndIsDeletedFalseOrderByCreatedAtAsc(LectureClass lectureClass);

    // 특정 강좌에 속한 삭제되지 않은 강의를 페이지 단위로 조회
    Page<Lecture> findByLectureClassAndIsDeletedFalse(LectureClass lectureClass, Pageable pageable);

    // 전체 강의 조회
    Page<Lecture> findAllByIsDeletedFalse(Pageable pageable);

    // 전체 강의 조회 - 특정 강좌 ID에 해당하는 강의만 조회
    Page<Lecture> findByIsDeletedFalseAndLectureClass_LectureClassId(
            Long lectureClassId, Pageable pageable
    );

    // 전체 강의 조회 - 제목 검색
    Page<Lecture> findByIsDeletedFalseAndTitleContaining(
            String keyword, Pageable pageable
    );

    // 전체 강의 조회 - 강좌 + 제목 검색
    Page<Lecture> findByIsDeletedFalseAndLectureClass_LectureClassIdAndTitleContaining(
            Long lectureClassId, String keyword, Pageable pageable
    );

    // 내 강의 조회
    Page<Lecture> findByTeacherAndIsDeletedFalse(
            Users teacher, Pageable pageable
    );

    // 내 강의 조회 - 특정 강사가 등록한 강의 중 특정 강좌에 속한 강의만 조회
    Page<Lecture> findByTeacherAndIsDeletedFalseAndLectureClass_LectureClassId(
            Users teacher, Long lectureClassId, Pageable pageable
    );

    // 내 강의 조회 - 제목 검색
    Page<Lecture> findByTeacherAndIsDeletedFalseAndTitleContaining(
            Users teacher, String keyword, Pageable pageable
    );

    // 내 강의 조회 - 강좌 + 제목 검색
    Page<Lecture> findByTeacherAndIsDeletedFalseAndLectureClass_LectureClassIdAndTitleContaining(
            Users teacher, Long lectureClassId, String keyword, Pageable pageable
    );
}