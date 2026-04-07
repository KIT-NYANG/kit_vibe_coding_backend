package com.nyang.backend.lectureList.repository;

import com.nyang.backend.lectureList.dto.LectureListResponseDto;
import java.util.List;

public interface LectureListRepositoryCustom {
    List<LectureListResponseDto> findLectureListsByUserId(Long userId);
}
