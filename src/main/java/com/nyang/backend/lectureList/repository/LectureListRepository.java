package com.nyang.backend.lectureList.repository;

import com.nyang.backend.lectureList.entity.LectureList;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LectureListRepository extends JpaRepository<LectureList, Long>, LectureListRepositoryCustom {
    boolean existsByUsers_UserIdAndLectureClass_LectureClassIdAndIsDeletedFalse(Long userId, Long lectureClassId); // soft del 제외하고 조회

    Optional<LectureList> findByLectureListIdAndIsDeletedFalse(Long lectureListId);

}