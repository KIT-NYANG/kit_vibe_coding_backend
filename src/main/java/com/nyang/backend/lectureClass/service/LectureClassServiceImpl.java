package com.nyang.backend.lectureClass.service;

import com.nyang.backend.global.exception.BusinessException;
import com.nyang.backend.global.exception.ErrorCode;
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

        if (teacher.getRole() != Role.TEACHER) {
            throw new BusinessException(ErrorCode.ONLY_TEACHER_CAN_UPLOAD);
        }

        String thumbnailPath = fileStorageService.saveThumbnail(requestDto.getThumbnailFile());

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
    public List<LectureClassListResponseDto> getAllLectureClasses() {
        return lectureClassRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(LectureClassListResponseDto::from)
                .toList();
    }

    @Override
    public List<LectureClassListResponseDto> getMyLectureClasses(String userEmail) {
        Users teacher = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (teacher.getRole() != Role.TEACHER) {
            throw new BusinessException(ErrorCode.ONLY_TEACHER_CAN_VIEW_OWN_LECTURES);
        }

        return lectureClassRepository.findByTeacherAndIsDeletedFalseOrderByCreatedAtDesc(teacher)
                .stream()
                .map(LectureClassListResponseDto::from)
                .toList();
    }

    @Override
    public LectureClassResponseDto getLectureClassDetail(Long lectureClassId) {
        LectureClass lectureClass = lectureClassRepository.findByLectureClassIdAndIsDeletedFalse(lectureClassId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_CLASS_NOT_FOUND));

        return LectureClassResponseDto.from(lectureClass);
    }

    @Override
    public List<LectureListResponseDto> getLecturesByLectureClass(Long lectureClassId) {
        LectureClass lectureClass = lectureClassRepository.findByLectureClassIdAndIsDeletedFalse(lectureClassId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_CLASS_NOT_FOUND));

        return lectureRepository.findByLectureClassAndIsDeletedFalseOrderByCreatedAtAsc(lectureClass)
                .stream()
                .map(LectureListResponseDto::from)
                .toList();
    }

    @Override
    @Transactional
    public String deleteLectureClass(String userEmail, Long lectureClassId) {
        Users teacher = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        LectureClass lectureClass = lectureClassRepository.findByLectureClassIdAndIsDeletedFalse(lectureClassId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_CLASS_NOT_FOUND));

        if (!lectureClass.getTeacher().getUserId().equals(teacher.getUserId())) {
            throw new BusinessException(ErrorCode.ONLY_OWNER_CAN_DELETE_LECTURE);
        }

        lectureClass.softDelete();
        return "강좌가 삭제되었습니다.";
    }
}
