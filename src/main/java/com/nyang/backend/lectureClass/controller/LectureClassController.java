package com.nyang.backend.lectureClass.controller;

import com.nyang.backend.global.exception.SuccessCode;
import com.nyang.backend.global.response.PageResponseDto;
import com.nyang.backend.global.response.ResponseDto;
import com.nyang.backend.lecture.dto.LectureCreateRequestDto;
import com.nyang.backend.lecture.dto.LectureListResponseDto;
import com.nyang.backend.lecture.dto.LectureResponseDto;
import com.nyang.backend.lectureClass.dto.LectureClassCreateRequestDto;
import com.nyang.backend.lectureClass.dto.LectureClassListResponseDto;
import com.nyang.backend.lectureClass.dto.LectureClassResponseDto;
import com.nyang.backend.lectureClass.service.LectureClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success(SuccessCode.CREATED, result));
    }

    // 전체 강좌 조회
    @GetMapping
    public ResponseEntity<ResponseDto<PageResponseDto<LectureClassListResponseDto>>> getAllLectureClasses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword
    ) {
        PageResponseDto<LectureClassListResponseDto> result =
                lectureClassService.getAllLectureClasses(page, size, category, keyword);
        return ResponseEntity.ok(ResponseDto.success(SuccessCode.OK, result));
    }

    // 내가 올린 강좌 조회
    @GetMapping("/my")
    public ResponseEntity<ResponseDto<PageResponseDto<LectureClassListResponseDto>>> getMyLectureClasses(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String userEmail = authentication.getName();
        PageResponseDto<LectureClassListResponseDto> result = lectureClassService.getMyLectureClasses(userEmail, page, size);
        return ResponseEntity.ok(ResponseDto.success(SuccessCode.OK, result));
    }

    // 강좌 상세 조회
    @GetMapping("/{lectureClassId}")
    public ResponseEntity<ResponseDto<LectureClassResponseDto>> getLectureClassDetail(
            @PathVariable Long lectureClassId
    ) {
        LectureClassResponseDto result = lectureClassService.getLectureClassDetail(lectureClassId);
        return ResponseEntity.ok(ResponseDto.success(SuccessCode.OK, result));
    }

    // 강좌 삭제
    @DeleteMapping("/{lectureClassId}")
    public ResponseEntity<Void> deleteLectureClass(
            @PathVariable Long lectureClassId,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        lectureClassService.deleteLectureClass(userEmail, lectureClassId);
        return ResponseEntity.noContent().build();
    }

    // 특정 강좌에 속한 강의 영상 목록 조회
    @GetMapping("/{lectureClassId}/lectures")
    public ResponseEntity<ResponseDto<PageResponseDto<LectureListResponseDto>>> getLecturesByLectureClass(
            @PathVariable Long lectureClassId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponseDto<LectureListResponseDto> result =
                lectureClassService.getLecturesByLectureClass(lectureClassId, page, size);
        return ResponseEntity.ok(ResponseDto.success(SuccessCode.OK, result));
    }

}
