package com.nyang.backend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 인증 & 인가
    UNAUTHORIZED(401, "AUTH001", "비밀번호가 일치하지 않습니다."),
    FORBIDDEN(403, "AUTH002", "접근 권한이 없습니다."),
    ACCESS_TOKEN_NOT_FOUND(401, "AUTH003", "Access Token이 없습니다."),
    INVALID_TOKEN(401, "AUTH004", "유효하지 않은 토큰입니다."),

    // 회원 관련
    USER_NOT_FOUND(404, "USER001", "회원이 존재하지 않습니다."),
    EMAIL_DUPLICATED(400, "EMAIL001", "이메일이 이미 존재합니다."),
    PASSWORD_MISMATCH(400, "USER002", "비밀번호와 비밀번호 확인이 일치하지 않습니다."),

    // 강좌 관련
    LECTURE_CLASS_NOT_FOUND(404, "LECTURE_CLASS001", "강좌를 찾을 수 없습니다."),

    // 강의 관련
    LECTURE_NOT_FOUND(404, "LECTURE001", "강의를 찾을 수 없습니다."),
    ONLY_TEACHER_CAN_UPLOAD(403, "LECTURE002", "강의 업로드는 강사만 가능합니다."),
    ONLY_TEACHER_CAN_VIEW_OWN_LECTURES(403, "LECTURE003", "강사만 본인 강의를 조회할 수 있습니다."),
    ONLY_OWNER_CAN_DELETE_LECTURE(403, "LECTURE004", "본인이 업로드한 강의만 삭제할 수 있습니다."),
    // 파일 관련
    VIDEO_FILE_REQUIRED(400, "LECTURE005", "영상 파일은 필수입니다."),
    VIDEO_METADATA_EXTRACT_FAILED(500, "LECTURE006", "영상 메타데이터 추출에 실패했습니다."),
    THUMBNAIL_SAVE_FAILED(500, "LECTURE007", "썸네일 저장에 실패했습니다."),
    VIDEO_SAVE_FAILED(500, "LECTURE008", "영상 저장에 실패했습니다."),
    FILE_DELETE_FAILED(500, "LECTURE009", "파일 삭제에 실패했습니다."),

    // 요청값 관련
    INVALID_INPUT_VALUE(400, "COMMON002", "입력값이 올바르지 않습니다."),

    // 서버 오류
    INTERNAL_SERVER_ERROR(500, "COMMON001", "서버 오류입니다."),
    STT_PROCESS_FAILED(500, "STT_500_1", "STT 처리 중 오류가 발생했습니다."),
    FILE_CONVERSION_FAILED(500, "FILE_500_1", "임시 파일 생성에 실패했습니다."),
    // 수강 목록 CRUD 관련
    LECTURE_LIST_NOT_FOUND(404, "LECTURE_LIST001", "존재하지 않는 수강 기록입니다."),
    LECTURE_ALREADY_ENROLLED(409, "LECTURE_LIST002", "이미 수강 신청한 강의입니다."),
    INVALID_WATCH_TIME(400, "LECTURE_LIST004", "시청 시간은 0 이상이어야 합니다."),
    INVALID_LECTURE_DURATION(400, "LECTURE_LIST005", "강의 영상 길이가 올바르지 않습니다.");

    private final int status;
    private final String code;
    private final String message;
}