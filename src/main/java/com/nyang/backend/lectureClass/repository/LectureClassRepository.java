package com.nyang.backend.lectureClass.repository;

import com.nyang.backend.lectureClass.entity.LectureClass;
import com.nyang.backend.user.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LectureClassRepository extends JpaRepository<LectureClass, Long> {

    // 삭제되지 않은 강좌 1개 조회
    Optional<LectureClass> findByLectureClassIdAndIsDeletedFalse(Long lectureClassId);

    // 삭제되지 않은 강좌가 존재하는지 확인
    boolean existsByLectureClassIdAndIsDeletedFalse(Long lectureClassId);

    // 삭제되지 않은 전체 강좌를 페이지 단위로 조회
    Page<LectureClass> findAllByIsDeletedFalse(Pageable pageable);

    // 특정 강사가 등록한 삭제되지 않은 강좌 목록 조회
    Page<LectureClass> findByTeacherAndIsDeletedFalse(Users teacher, Pageable pageable);

    // 삭제되지 않은 강좌 중 특정 카테고리에 해당하는 강좌 조회
    Page<LectureClass> findByIsDeletedFalseAndCategory(String category, Pageable pageable);

    // 삭제되지 않은 강좌 중 제목에 keyword가 포함된 강좌 조회
    Page<LectureClass> findByIsDeletedFalseAndTitleContaining(String keyword, Pageable pageable);

    // 삭제되지 않은 강좌 중 카테고리가 일치하고 제목에 keyword가 포함된 강좌 조회
    Page<LectureClass> findByIsDeletedFalseAndCategoryAndTitleContaining(
            String category, String keyword, Pageable pageable
    );

}