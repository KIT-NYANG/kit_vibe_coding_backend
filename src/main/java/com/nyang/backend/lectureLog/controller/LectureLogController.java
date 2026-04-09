package com.nyang.backend.lectureLog.controller;

import com.nyang.backend.global.exception.SuccessCode;
import com.nyang.backend.global.response.ResponseDto;
import com.nyang.backend.lectureLog.dto.request.LecturePreAnalysisRequestDto;
import com.nyang.backend.lectureLog.dto.request.LectureViewLogRequestDto;
import com.nyang.backend.lectureLog.dto.response.LastWatchPositionResponseDto;
import com.nyang.backend.lectureLog.dto.response.LectureAggregateAnalysisResponseDto;
import com.nyang.backend.lectureLog.dto.response.LectureLogSaveResponseDto;
import com.nyang.backend.lectureLog.dto.response.LecturePreAnalysisResponseDto;
import com.nyang.backend.lectureLog.service.LectureLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lectures/{lectureId}/logs")
public class LectureLogController {

    private final LectureLogService lectureLogService;

    /**
     * 강의 시청 로그 저장
     *
     * 예:
     * PLAY, PAUSE, SEEK, HEARTBEAT, ENDED, PAGE_EXIT
     */
    @PostMapping
    public ResponseEntity<ResponseDto<LectureLogSaveResponseDto>> saveLectureLog(
            @PathVariable Long lectureId,
            @RequestBody LectureViewLogRequestDto requestDto,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();

        LectureLogSaveResponseDto response =
                lectureLogService.saveLectureLog(lectureId, userEmail, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success(SuccessCode.OK,response));
    }

    /**
     * 사용자의 마지막 시청 위치 조회
     * 이어보기 기능용
     */
    @GetMapping("/last-position")
    public ResponseEntity<ResponseDto<LastWatchPositionResponseDto>> getLastWatchPosition(
            @PathVariable Long lectureId,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();

        LastWatchPositionResponseDto response =
                lectureLogService.getLastWatchPosition(lectureId, userEmail);

        return ResponseEntity.ok(ResponseDto.success(SuccessCode.OK,response));
    }

    /**
     * 로그 쌓이기 전 사전 분석
     * STT segment만 가지고 퀴즈 위치 / 퀴즈 / 해설 / 교사용 가이드 생성
     */
    @PostMapping("/analysis/pre")
    public ResponseEntity<ResponseDto<LecturePreAnalysisResponseDto>> requestPreAnalysis(
            @PathVariable Long lectureId,
            @RequestBody(required = false) LecturePreAnalysisRequestDto requestDto
    ) {
        String additionalPrompt = requestDto != null ? requestDto.getAdditionalPrompt() : null;

        LecturePreAnalysisResponseDto response =
                lectureLogService.requestPreAnalysis(lectureId, additionalPrompt);

        return ResponseEntity.ok(ResponseDto.success(SuccessCode.OK,response));
    }

    @PostMapping("/analysis/aggregate")
    public ResponseEntity<ResponseDto<LectureAggregateAnalysisResponseDto>> requestAggregateAnalysis(
            @PathVariable Long lectureId
    ) {
        LectureAggregateAnalysisResponseDto response =
                lectureLogService.requestAggregateAnalysis(lectureId);

        return ResponseEntity.ok(ResponseDto.success(SuccessCode.OK,response));
    }
}