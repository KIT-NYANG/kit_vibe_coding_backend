package com.nyang.backend.lecture.service;

import com.nyang.backend.lecture.dto.LectureCreateRequestDto;
import com.nyang.backend.lecture.dto.LectureListResponseDto;
import com.nyang.backend.lecture.dto.LectureResponseDto;

import java.util.List;

public interface LectureService {
    LectureResponseDto createLecture(String userEmail, LectureCreateRequestDto requestDto); //강의 업로드(강사만 가능)
    List<LectureListResponseDto> getAllLectures(); //모든 강의 불러오기 (페이지 네이션 추가 예정)
    LectureResponseDto getLectureDetail(Long lectureId); //강의 불러오기(시청)
    List<LectureListResponseDto> getMyLectures(String userEmail); //내가 업로드한 강의 목록
    String deleteLecture(String userEmail, Long lectureId); //강의 삭제
}