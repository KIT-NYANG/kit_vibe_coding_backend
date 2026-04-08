package com.nyang.backend.lectureList.controller;

import com.nyang.backend.global.exception.SuccessCode;
import com.nyang.backend.global.response.PageResponseDto;
import com.nyang.backend.global.response.ResponseDto;
import com.nyang.backend.lectureList.dto.LectureEnrollmentRequestDto;
import com.nyang.backend.lectureList.dto.MyLectureListResponseDto;
import com.nyang.backend.lectureList.service.LectureListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lecture-list")
public class LectureListController {

    private final LectureListService lectureListService;

    // 수강 신청 api
    @PostMapping
    public ResponseEntity<ResponseDto<String>> enrollLecture(@RequestBody LectureEnrollmentRequestDto requestDto) {
        String result = lectureListService.enrollLecture(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success(SuccessCode.CREATED, result));
    }

    // 수강 목록 조회 api
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseDto<PageResponseDto<MyLectureListResponseDto>>> getLectureLists(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword
    ) {
        PageResponseDto<MyLectureListResponseDto> result =
                lectureListService.getLectureLists(userId, page, size, category, keyword);
        return ResponseEntity.ok(ResponseDto.success(SuccessCode.OK, result));
    }

    // 본인의 수강 목록 조회 api
    @GetMapping("/my")
    public ResponseEntity<ResponseDto<PageResponseDto<MyLectureListResponseDto>>> getMyLectureLists(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword
    ) {
        String userEmail = authentication.getName();
        PageResponseDto<MyLectureListResponseDto> result =
                lectureListService.getMyLectureLists(userEmail, page, size, category, keyword);
        return ResponseEntity.ok(ResponseDto.success(SuccessCode.OK, result));
    }

    // 수강 삭제 api
    @DeleteMapping("/{lectureListId}")
    public ResponseEntity<Void> deleteLectureList(@PathVariable Long lectureListId) {
        lectureListService.deleteLectureList(lectureListId);
        return ResponseEntity.noContent().build();
    }
}
