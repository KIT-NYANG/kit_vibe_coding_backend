package com.nyang.backend.lecture.controller;

import com.nyang.backend.global.exception.SuccessCode;
import com.nyang.backend.global.response.PageResponseDto;
import com.nyang.backend.global.response.ResponseDto;
import com.nyang.backend.lecture.dto.LectureCreateRequestDto;
import com.nyang.backend.lecture.dto.LectureListResponseDto;
import com.nyang.backend.lecture.dto.LectureResponseDto;
import com.nyang.backend.lecture.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success(SuccessCode.CREATED, result));
    }

    // 강의 전체 조회
    @GetMapping
    public ResponseEntity<ResponseDto<PageResponseDto<LectureListResponseDto>>> getAllLectures(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long lectureClassId,
            @RequestParam(required = false) String keyword
    ) {
        PageResponseDto<LectureListResponseDto> result =
                lectureService.getAllLectures(page, size, lectureClassId, keyword);
        return ResponseEntity.ok(ResponseDto.success(SuccessCode.OK, result));
    }

    // 강의 상세 조회
    @GetMapping("/{lectureId}")
    public ResponseEntity<ResponseDto<LectureResponseDto>> getLectureDetail(@PathVariable Long lectureId) {
        LectureResponseDto result = lectureService.getLectureDetail(lectureId);
        return ResponseEntity.ok(ResponseDto.success(SuccessCode.OK, result));
    }

    // 내가 올린 강의 조회
    @GetMapping("/my")
    public ResponseEntity<ResponseDto<PageResponseDto<LectureListResponseDto>>> getMyLectures(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long lectureClassId,
            @RequestParam(required = false) String keyword
    ) {
        String userEmail = authentication.getName();
        PageResponseDto<LectureListResponseDto> result = lectureService.getMyLectures(userEmail, page, size, lectureClassId, keyword);
        return ResponseEntity.ok(ResponseDto.success(SuccessCode.OK, result));
    }

    // 강의 삭제
    @DeleteMapping("/{lectureId}")
    public ResponseEntity<Void> deleteLecture(
            @PathVariable Long lectureId,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        lectureService.deleteLecture(userEmail, lectureId);
        return ResponseEntity.noContent().build();
    }
}