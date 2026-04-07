package com.nyang.backend.lectureList.service;

import com.nyang.backend.global.exception.BusinessException;
import com.nyang.backend.global.exception.ErrorCode;
import com.nyang.backend.lectureList.dto.LectureListCreateRequestDto;
import com.nyang.backend.lectureList.dto.LectureListResponseDto;
import com.nyang.backend.lectureList.dto.LectureProgressUpdateRequestDto;
import com.nyang.backend.lectureList.entity.LectureList;
import com.nyang.backend.lectureList.repository.LectureListRepository;
import com.nyang.backend.lecture.entity.Lecture;
import com.nyang.backend.lecture.repository.LectureRepository;
import com.nyang.backend.user.entity.Users;
import com.nyang.backend.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureListService {

    private final LectureListRepository lectureListRepository;
    private final UsersRepository usersRepository;
    private final LectureRepository lectureRepository;

    // 수강 신청 메서드
    @Transactional
    public String enrollLecture(LectureListCreateRequestDto requestDto) {
        Users users = usersRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)); // 사용자 먼저 조회

        Lecture lecture = lectureRepository.findById(requestDto.getLectureId())
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND)); // 강의 조회

        // 중복 수강 신청 검사
        boolean alreadyEnrolled = lectureListRepository
                .existsByUser_UserIdAndLecture_LectureIdAndIsDeletedFalse(
                        requestDto.getUserId(),
                        requestDto.getLectureId()
                );

        // 이미 신청했으면 예외처리
        if (alreadyEnrolled) {
            throw new BusinessException(ErrorCode.LECTURE_ALREADY_ENROLLED);
        }

        // 엔티티 생성 (수강 기록 객체)
        LectureList lectureList = LectureList.builder()
                .users(users)
                .lecture(lecture)
                .watchTimeSeconds(0)
                .progressPercent(0)
                .build();

        lectureListRepository.save(lectureList);
        return "수강 신청 완료";
    }

    // 사용자의 수강 목록 조회
    public List<LectureListResponseDto> getLectureLists(Long userId) {
        return lectureListRepository.findLectureListsByUserId(userId); // custom impl로 넘김 (lecture 테이블이랑 조인하기 때문)
    }

    // 수강 진행도 수정 메서드
    @Transactional
    public String updateProgress(Long lectureListId, LectureProgressUpdateRequestDto requestDto) {
        // 수정 대상 강의 찾기
        LectureList lectureList = lectureListRepository.findByLectureListIdAndIsDeletedFalse(lectureListId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_LIST_NOT_FOUND)); // 수강 목록 조회 실패 시

        validateWatchTime(requestDto.getWatchTimeSeconds()); // 시청 시간이 정상 범위인지 검사

        Lecture lecture = lectureList.getLecture();
        Integer durationSeconds = lecture.getDurationSeconds();

        if (durationSeconds == null || durationSeconds <= 0) {
            throw new BusinessException(ErrorCode.INVALID_LECTURE_DURATION);
        }

        int requestedWatchTime = Math.min(requestDto.getWatchTimeSeconds(), durationSeconds); // 요청 시간이 영상 길이를 넘지 못하게 자름

        int newProgressPercent = (int) (((double) requestedWatchTime / durationSeconds) * 100);

        int currentWatchTime = lectureList.getWatchTimeSeconds() == null ? 0 : lectureList.getWatchTimeSeconds();
        int currentProgress = lectureList.getProgressPercent() == null ? 0 : lectureList.getProgressPercent();

        int finalWatchTime = Math.max(currentWatchTime, requestedWatchTime); // 기존보다 작은 값이면 유지
        int finalProgressPercent = Math.max(currentProgress, newProgressPercent); // 기존보다 큰 값이면 갱신

        // 조회한 lecture_list 값 변경 (더티 체킹)
        lectureList.updateProgress(finalWatchTime, finalProgressPercent);

        return "수강 진행도 업데이트 완료";
    }

    // 시청 시간 유효성 검사
    private void validateWatchTime(Integer watchTimeSeconds) {
        if (watchTimeSeconds == null || watchTimeSeconds < 0) {
            throw new BusinessException(ErrorCode.INVALID_WATCH_TIME);
        }
    }

    // 수강 기록 삭제 메서드 (soft하게 구현했기 때문에 기록은 보존됨)
    @Transactional
    public String deleteLectureList(Long lectureListId) {
        LectureList lectureList = lectureListRepository.findByLectureListIdAndIsDeletedFalse(lectureListId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_LIST_NOT_FOUND));

        lectureList.softDelete();
        return "수강 기록 삭제 완료";
    }

}