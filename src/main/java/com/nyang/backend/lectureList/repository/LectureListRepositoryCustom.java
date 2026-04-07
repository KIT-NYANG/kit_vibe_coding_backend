package com.nyang.backend.lectureList.repository;

import com.nyang.backend.lectureList.dto.MyLectureListResponseDto;
import java.util.List;

public interface LectureListRepositoryCustom {
    List<MyLectureListResponseDto> findLectureListsByUserId(Long userId);
}
