package com.nyang.backend.lecture.controller;

import com.nyang.backend.global.response.ResponseDto;
import com.nyang.backend.lecture.dto.LectureCreateRequestDto;
import com.nyang.backend.lecture.dto.LectureListResponseDto;
import com.nyang.backend.lecture.dto.LectureResponseDto;
import com.nyang.backend.lecture.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lectures")
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;

    // 강의 업로드
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto<LectureResponseDto>> createLecture(
            @ModelAttribute LectureCreateRequestDto requestDto,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        LectureResponseDto result = lectureService.createLecture(userEmail, requestDto);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    // 강의 상세 조회
    @GetMapping("/{lectureId}")
    public ResponseEntity<ResponseDto<LectureResponseDto>> getLectureDetail(@PathVariable Long lectureId) {
        LectureResponseDto result = lectureService.getLectureDetail(lectureId);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    // 내가 올린 강의 조회
    @GetMapping("/my")
    public ResponseEntity<ResponseDto<List<LectureListResponseDto>>> getMyLectures(Authentication authentication) {
        String userEmail = authentication.getName();
        List<LectureListResponseDto> result = lectureService.getMyLectures(userEmail);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    // 강의 삭제
    @DeleteMapping("/{lectureId}")
    public ResponseEntity<ResponseDto<String>> deleteLecture(
            @PathVariable Long lectureId,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        String result = lectureService.deleteLecture(userEmail, lectureId);
        return ResponseEntity.ok(ResponseDto.success(result));
    }
}