package com.nyang.backend.lectureList.service;

import com.nyang.backend.global.exception.BusinessException;
import com.nyang.backend.global.exception.ErrorCode;
import com.nyang.backend.global.response.PageResponseDto;
import com.nyang.backend.lectureClass.entity.LectureClass;
import com.nyang.backend.lectureList.dto.LectureEnrollmentRequestDto;
import com.nyang.backend.lectureList.dto.MyLectureListResponseDto;
import com.nyang.backend.lectureList.entity.LectureList;
import com.nyang.backend.lectureList.repository.LectureListRepository;
import com.nyang.backend.lectureClass.repository.LectureClassRepository;
import com.nyang.backend.user.entity.Users;
import com.nyang.backend.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureListService {

    private final LectureListRepository lectureListRepository;
    private final UsersRepository usersRepository;
    private final LectureClassRepository lectureClassRepository;

    // 수강 신청 메서드
    @Transactional
    public String enrollLecture(LectureEnrollmentRequestDto requestDto, String UserEmail) {
        Users users = usersRepository.findByEmail(UserEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)); // 사용자 먼저 조회

        LectureClass lectureClass = lectureClassRepository.findByLectureClassIdAndIsDeletedFalse(requestDto.getLectureClassId())
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_CLASS_NOT_FOUND)); // 강좌 조회

        // 중복 수강 신청 검사
        boolean alreadyEnrolled = lectureListRepository
                .existsByUsers_UserIdAndLectureClass_LectureClassIdAndIsDeletedFalse(
                        users.getUserId(),
                        requestDto.getLectureClassId()
                );

        // 이미 신청했으면 예외처리
        if (alreadyEnrolled) {
            throw new BusinessException(ErrorCode.LECTURE_ALREADY_ENROLLED);
        }

        // 엔티티 생성 (수강 기록 객체)
        LectureList lectureList = LectureList.create(users, lectureClass);

        lectureListRepository.save(lectureList);
        return "수강 신청 완료";
    }

    // 사용자의 수강 목록 조회
    public PageResponseDto<MyLectureListResponseDto> getLectureLists(
            Long userId,
            int page,
            int size,
            String category,
            String keyword
    ) {
        if (!usersRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        category = (category == null) ? null : category.trim();
        keyword = (keyword == null) ? null : keyword.trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<MyLectureListResponseDto> result = lectureListRepository.findLectureListsByUserId(
                userId, category, keyword, pageable
        ); // custom impl로 넘김 (lecture 테이블이랑 조인하기 때문)

        return PageResponseDto.from(result);
    }

    // 본인의 수강 목록 조회
    public PageResponseDto<MyLectureListResponseDto> getMyLectureLists(
            String userEmail,
            int page,
            int size,
            String category,
            String keyword
    ) {
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        category = (category == null) ? null : category.trim();
        keyword = (keyword == null) ? null : keyword.trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<MyLectureListResponseDto> result = lectureListRepository.findLectureListsByUserId(
                user.getUserId(), category, keyword, pageable
        );

        return PageResponseDto.from(result);
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