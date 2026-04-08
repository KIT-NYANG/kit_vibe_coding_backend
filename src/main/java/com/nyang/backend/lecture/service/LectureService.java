package com.nyang.backend.lecture.service;

import com.nyang.backend.global.response.PageResponseDto;
import com.nyang.backend.lecture.dto.LectureCreateRequestDto;
import com.nyang.backend.lecture.dto.LectureListResponseDto;
import com.nyang.backend.lecture.dto.LectureResponseDto;

import java.util.List;

public interface LectureService {
    //강의 업로드(강사만 가능)
    LectureResponseDto createLecture(String userEmail, LectureCreateRequestDto requestDto);

    //모든 강의 불러오기
    PageResponseDto<LectureListResponseDto> getAllLectures(
            int page, int size, Long lectureClassId, String keyword
    );

    //강의 불러오기(시청)
    LectureResponseDto getLectureDetail(Long lectureId);

    //내가 업로드한 강의 목록
    PageResponseDto<LectureListResponseDto> getMyLectures(
            String userEmail, int page, int size, Long lectureClassId, String keyword
    );

    //강의 삭제
    String deleteLecture(String userEmail, Long lectureId);
}