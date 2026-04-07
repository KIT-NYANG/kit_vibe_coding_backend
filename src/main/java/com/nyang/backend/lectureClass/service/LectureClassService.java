package com.nyang.backend.lectureClass.service;

import com.nyang.backend.lecture.dto.LectureListResponseDto;
import com.nyang.backend.lectureClass.dto.LectureClassCreateRequestDto;
import com.nyang.backend.lectureClass.dto.LectureClassListResponseDto;
import com.nyang.backend.lectureClass.dto.LectureClassResponseDto;

import java.util.List;

public interface LectureClassService {

    LectureClassResponseDto createLectureClass(String userEmail, LectureClassCreateRequestDto requestDto);

    List<LectureClassListResponseDto> getAllLectureClasses();

    List<LectureClassListResponseDto> getMyLectureClasses(String userEmail);

    LectureClassResponseDto getLectureClassDetail(Long lectureClassId);

    List<LectureListResponseDto> getLecturesByLectureClass(Long lectureClassId);

    String deleteLectureClass(String userEmail, Long lectureClassId);
}
