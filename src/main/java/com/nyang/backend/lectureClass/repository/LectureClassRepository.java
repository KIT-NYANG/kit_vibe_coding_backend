package com.nyang.backend.lectureClass.repository;

import com.nyang.backend.lectureClass.entity.LectureClass;
import com.nyang.backend.lectureClass.entity.LectureClassCategory;
import com.nyang.backend.user.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LectureClassRepository extends JpaRepository<LectureClass, Long> {

    // 삭제되지 않은 강좌 1개 조회
    Optional<LectureClass> findByLectureClassIdAndIsDeletedFalse(Long lectureClassId);

    // 삭제되지 않은 전체 강좌를 페이지 단위로 조회
    Page<LectureClass> findAllByIsDeletedFalse(Pageable pageable);

    // 특정 강사가 등록한 삭제되지 않은 강좌 목록 조회
    Page<LectureClass> findByTeacherAndIsDeletedFalse(Users teacher, Pageable pageable);

    // 삭제되지 않은 강좌 중 특정 카테고리에 해당하는 강좌 조회
    Page<LectureClass> findByIsDeletedFalseAndCategory(LectureClassCategory category, Pageable pageable);

    // 공백 무시 제목 검색
    @Query("""
        SELECT lc
        FROM LectureClass lc
        WHERE lc.isDeleted = false
          AND REPLACE(lc.title, ' ', '') LIKE CONCAT('%', :keyword, '%')
    """)
    Page<LectureClass> findByIsDeletedFalseAndTitleContainingIgnoreSpace(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // 공백 무시 카테고리 + 제목 검색
    @Query("""
        SELECT lc
        FROM LectureClass lc
        WHERE lc.isDeleted = false
          AND lc.category = :category
          AND REPLACE(lc.title, ' ', '') LIKE CONCAT('%', :keyword, '%')
    """)
    Page<LectureClass> findByIsDeletedFalseAndCategoryAndTitleContainingIgnoreSpace(
            @Param("category") LectureClassCategory category,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}