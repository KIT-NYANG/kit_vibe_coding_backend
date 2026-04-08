package com.nyang.backend.lectureList.repository;

import com.nyang.backend.lectureList.entity.LectureList;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LectureListRepository extends JpaRepository<LectureList, Long>, LectureListRepositoryCustom {
    // 특정 사용자가 특정 강좌를 이미 수강 신청했는지 확인
    boolean existsByUsers_UserIdAndLectureClass_LectureClassIdAndIsDeletedFalse(Long userId, Long lectureClassId); // soft del 제외하고 조회

    // 수강 목록 단건 조회
    Optional<LectureList> findByLectureListIdAndIsDeletedFalse(Long lectureListId);
}