package com.nyang.backend.lectureClass.service;

import com.nyang.backend.global.response.PageResponseDto;
import com.nyang.backend.lecture.dto.LectureListResponseDto;
import com.nyang.backend.lectureClass.dto.LectureClassCreateRequestDto;
import com.nyang.backend.lectureClass.dto.LectureClassListResponseDto;
import com.nyang.backend.lectureClass.dto.LectureClassResponseDto;

import java.util.List;

public interface LectureClassService {
    // 강사 계정으로 새 강좌를 생성
    LectureClassResponseDto createLectureClass(String userEmail, LectureClassCreateRequestDto requestDto);

    // 전체 강좌 목록 조회
    PageResponseDto<LectureClassListResponseDto> getAllLectureClasses(
            int page, int size, String category, String keyword
    );

    // 현재 로그인한 강사가 등록한 본인 강좌 목록 조회
    PageResponseDto<LectureClassListResponseDto> getMyLectureClasses(String userEmail, int page, int size);

    // 강좌 상세 조회
    LectureClassResponseDto getLectureClassDetail(Long lectureClassId);

    // 특정 강좌에 속한 강의 목록 조회
    PageResponseDto<LectureListResponseDto> getLecturesByLectureClass(Long lectureClassId, int page, int size);

    // 강좌 삭제
    String deleteLectureClass(String userEmail, Long lectureClassId);
}
