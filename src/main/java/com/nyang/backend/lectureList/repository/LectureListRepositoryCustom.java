package com.nyang.backend.lectureList.repository;

import com.nyang.backend.lectureList.dto.MyLectureListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LectureListRepositoryCustom {
    // 특정 사용자의 수강 목록을 페이지 단위로 조회
    Page<MyLectureListResponseDto> findLectureListsByUserId(
            Long userId, String category, String keyword, Pageable pageable
    );
}
