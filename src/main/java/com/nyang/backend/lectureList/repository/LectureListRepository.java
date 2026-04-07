package com.nyang.backend.lectureList.repository;

import com.nyang.backend.lectureList.entity.LectureList;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LectureListRepository extends JpaRepository<LectureList, Long>, LectureListRepositoryCustom {
    boolean existsByUser_UserIdAndLecture_LectureIdAndIsDeletedFalse(Long userId, Long lectureId); // soft del 제외하고 조회

    Optional<LectureList> findByLectureListIdAndIsDeletedFalse(Long lectureListId);

    List<LectureList> findByUser_UserIdAndIsDeletedFalse(Long userId);
}