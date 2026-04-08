package com.nyang.backend.lectureClass.service;

import com.nyang.backend.global.exception.BusinessException;
import com.nyang.backend.global.exception.ErrorCode;
import com.nyang.backend.global.response.PageResponseDto;
import com.nyang.backend.lecture.dto.LectureListResponseDto;
import com.nyang.backend.lecture.repository.LectureRepository;
import com.nyang.backend.lecture.storage.FileStorageService;
import com.nyang.backend.lectureClass.dto.LectureClassCreateRequestDto;
import com.nyang.backend.lectureClass.dto.LectureClassListResponseDto;
import com.nyang.backend.lectureClass.dto.LectureClassResponseDto;
import com.nyang.backend.lectureClass.entity.LectureClass;
import com.nyang.backend.lectureClass.repository.LectureClassRepository;
import com.nyang.backend.user.entity.Role;
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
public class LectureClassServiceImpl implements LectureClassService {

    private final LectureClassRepository lectureClassRepository;
    private final LectureRepository lectureRepository;
    private final UsersRepository usersRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public LectureClassResponseDto createLectureClass(String userEmail, LectureClassCreateRequestDto requestDto) {
        Users teacher = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 강사 권한이 아닌 경우 강좌 생성 불가
        if (teacher.getRole() != Role.TEACHER) {
            throw new BusinessException(ErrorCode.ONLY_TEACHER_CAN_UPLOAD);
        }

        // 썸네일 파일 저장 후 저장 경로 반환
        String thumbnailPath = fileStorageService.saveThumbnail(requestDto.getThumbnailFile());

        // 강좌 엔티티 생성
        LectureClass lectureClass = LectureClass.create(
                teacher,
                requestDto.getTitle(),
                requestDto.getCategory(),
                requestDto.getDescription(),
                thumbnailPath
        );

        LectureClass savedLectureClass = lectureClassRepository.save(lectureClass);
        return LectureClassResponseDto.from(savedLectureClass);
    }

    @Override
    public PageResponseDto<LectureClassListResponseDto> getAllLectureClasses(
            int page, int size, String category, String keyword
    ) {
        // 페이지 번호, 크기, 정렬 조건 설정
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        boolean hasCategory = category != null && !category.trim().isEmpty();
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

        Page<LectureClass> lectureClassPage;

        // category + keyword 둘 다 있을 때
        if (hasCategory && hasKeyword) {
            lectureClassPage = lectureClassRepository
                    .findByIsDeletedFalseAndCategoryAndTitleContaining(category, keyword, pageable);
        } else if (hasCategory) { // category만 있을 때
            lectureClassPage = lectureClassRepository
                    .findByIsDeletedFalseAndCategory(category, pageable);
        } else if (hasKeyword) { // keyword만 있을 때
            lectureClassPage = lectureClassRepository
                    .findByIsDeletedFalseAndTitleContaining(keyword, pageable);
        } else { // 필터 없이 전체 조회
            lectureClassPage = lectureClassRepository
                    .findAllByIsDeletedFalse(pageable);
        }

        Page<LectureClassListResponseDto> dtoPage  = lectureClassPage.map(LectureClassListResponseDto::from);

        return PageResponseDto.from(dtoPage);
    }

    @Override
    public PageResponseDto<LectureClassListResponseDto> getMyLectureClasses(
            String userEmail, int page, int size
    ) {
        Users teacher = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 강사 권한이 아닌 경우 본인 강좌 목록 조회 불가
        if (teacher.getRole() != Role.TEACHER) {
            throw new BusinessException(ErrorCode.ONLY_TEACHER_CAN_VIEW_OWN_LECTURES);
        }

        // 페이지 번호, 크기, 정렬 조건 설정
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 해당 강사가 등록한 강좌 목록 조회 후 DTO 변환
        Page<LectureClassListResponseDto> result = lectureClassRepository
                .findByTeacherAndIsDeletedFalse(teacher, pageable)
                .map(LectureClassListResponseDto::from);

        return PageResponseDto.from(result);
    }

    @Override
    public LectureClassResponseDto getLectureClassDetail(Long lectureClassId) {
        // 삭제되지 않은 강좌 상세 조회
        LectureClass lectureClass = lectureClassRepository.findByLectureClassIdAndIsDeletedFalse(lectureClassId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_CLASS_NOT_FOUND));

        return LectureClassResponseDto.from(lectureClass);
    }

    @Override
    public PageResponseDto<LectureListResponseDto> getLecturesByLectureClass(
            Long lectureClassId, int page, int size
    ) {
        // 강좌 존재 여부 확인
        LectureClass lectureClass = lectureClassRepository.findByLectureClassIdAndIsDeletedFalse(lectureClassId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_CLASS_NOT_FOUND));

        // 강의 목록 페이지 조건 설정
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));

        // 해당 강좌에 속한 강의 목록 조회 후 DTO 변환
        Page<LectureListResponseDto> result = lectureRepository
                .findByLectureClassAndIsDeletedFalse(lectureClass, pageable)
                .map(LectureListResponseDto::from);


        return PageResponseDto.from(result);
    }

    @Override
    @Transactional
    public String deleteLectureClass(String userEmail, Long lectureClassId) {
        Users teacher = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        LectureClass lectureClass = lectureClassRepository.findByLectureClassIdAndIsDeletedFalse(lectureClassId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_CLASS_NOT_FOUND));

        // 본인이 등록한 강좌가 아니면 삭제 불가
        if (!lectureClass.getTeacher().getUserId().equals(teacher.getUserId())) {
            throw new BusinessException(ErrorCode.ONLY_OWNER_CAN_DELETE_LECTURE);
        }

        // soft delete 처리
        lectureClass.softDelete();
        return "강좌가 삭제되었습니다.";
    }
}
