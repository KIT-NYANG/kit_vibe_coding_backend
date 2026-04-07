package com.nyang.backend.lectureList.controller;

import com.nyang.backend.lectureList.dto.LectureListCreateRequestDto;
import com.nyang.backend.lectureList.dto.LectureListResponseDto;
import com.nyang.backend.lectureList.service.LectureListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lecture-list")
public class LectureListController {

    private final LectureListService lectureListService;

    // 수강 신청 api
    @PostMapping
    public ResponseEntity<String> enrollLecture(@RequestBody LectureListCreateRequestDto requestDto) {
        return ResponseEntity.ok(lectureListService.enrollLecture(requestDto));
    }

    // 수강 목록 조회 api
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LectureListResponseDto>> getLectureLists(@PathVariable Long userId) {
        return ResponseEntity.ok(lectureListService.getLectureLists(userId));
    }

    // 수강 삭제 api
    @DeleteMapping("/{lectureListId}")
    public ResponseEntity<String> deleteLectureList(@PathVariable Long lectureListId) {
        return ResponseEntity.ok(lectureListService.deleteLectureList(lectureListId));
    }
}
