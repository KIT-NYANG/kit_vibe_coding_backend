package com.nyang.backend.lectureClass.controller;

import com.nyang.backend.global.response.ResponseDto;
import com.nyang.backend.lecture.dto.LectureCreateRequestDto;
import com.nyang.backend.lecture.dto.LectureListResponseDto;
import com.nyang.backend.lecture.dto.LectureResponseDto;
import com.nyang.backend.lectureClass.dto.LectureClassCreateRequestDto;
import com.nyang.backend.lectureClass.dto.LectureClassListResponseDto;
import com.nyang.backend.lectureClass.dto.LectureClassResponseDto;
import com.nyang.backend.lectureClass.service.LectureClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lecture-class")
@RequiredArgsConstructor
public class LectureClassController {

    private final LectureClassService lectureClassService;

    // 강좌 등록
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // 썸네일 업로드 시
    public ResponseEntity<ResponseDto<LectureClassResponseDto>> createLectureClass(
            @ModelAttribute LectureClassCreateRequestDto requestDto,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        LectureClassResponseDto result = lectureClassService.createLectureClass(userEmail, requestDto);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    // 전체 강좌 조회
    @GetMapping
    public ResponseEntity<ResponseDto<List<LectureClassListResponseDto>>> getAllLectureClasses() {
        List<LectureClassListResponseDto> result = lectureClassService.getAllLectureClasses();
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    // 내가 올린 강좌 조회
    @GetMapping("/my")
    public ResponseEntity<ResponseDto<List<LectureClassListResponseDto>>> getMyLectureClasses(
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        List<LectureClassListResponseDto> result = lectureClassService.getMyLectureClasses(userEmail);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    // 강좌 상세 조회
    @GetMapping("/{lectureClassId}")
    public ResponseEntity<ResponseDto<LectureClassResponseDto>> getLectureClassDetail(
            @PathVariable Long lectureClassId
    ) {
        LectureClassResponseDto result = lectureClassService.getLectureClassDetail(lectureClassId);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    // 강좌 삭제
    @DeleteMapping("/{lectureClassId}")
    public ResponseEntity<ResponseDto<String>> deleteLectureClass(
            @PathVariable Long lectureClassId,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        String result = lectureClassService.deleteLectureClass(userEmail, lectureClassId);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    // 특정 강좌에 속한 강의 영상 목록 조회
    @GetMapping("/{lectureClassId}/lectures")
    public ResponseEntity<ResponseDto<List<LectureListResponseDto>>> getLecturesByLectureClass(
            @PathVariable Long lectureClassId
    ) {
        List<LectureListResponseDto> result = lectureClassService.getLecturesByLectureClass(lectureClassId);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

}
