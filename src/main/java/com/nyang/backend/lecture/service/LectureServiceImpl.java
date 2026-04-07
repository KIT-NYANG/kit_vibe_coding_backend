package com.nyang.backend.lecture.service;

import com.nyang.backend.global.exception.BusinessException;
import com.nyang.backend.global.exception.ErrorCode;
import com.nyang.backend.lecture.dto.LectureCreateRequestDto;
import com.nyang.backend.lecture.dto.LectureListResponseDto;
import com.nyang.backend.lecture.dto.LectureResponseDto;
import com.nyang.backend.lecture.entity.Lecture;
import com.nyang.backend.lecture.repository.LectureRepository;
import com.nyang.backend.lecture.storage.FileStorageService;
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
public class LectureServiceImpl implements LectureService {

    private final LectureRepository lectureRepository;
    private final UsersRepository usersRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public LectureResponseDto createLecture(String userEmail, LectureCreateRequestDto requestDto) {
        Users teacher = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (teacher.getRole() != Role.TEACHER) {
            throw new BusinessException(ErrorCode.ONLY_TEACHER_CAN_UPLOAD);
        }

        if (requestDto.getVideoFile() == null || requestDto.getVideoFile().isEmpty()) {
            throw new BusinessException(ErrorCode.VIDEO_FILE_REQUIRED);
        }

        Lecture lecture = Lecture.create(
                teacher,
                requestDto.getCategory(),
                requestDto.getTitle(),
                requestDto.getDescription(),
                fileStorageService.saveVideo(requestDto.getVideoFile()),
                fileStorageService.saveThumbnail(requestDto.getThumbnailFile())
        );

        Lecture savedLecture = lectureRepository.save(lecture);
        return LectureResponseDto.from(savedLecture);
    }

    @Override
    public List<LectureListResponseDto> getAllLectures() {
        return lectureRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(LectureListResponseDto::from)
                .toList();
    }

    @Override
    public LectureResponseDto getLectureDetail(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));

        return LectureResponseDto.from(lecture);
    }

    @Override
    public List<LectureListResponseDto> getMyLectures(String userEmail) {
        Users teacher = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (teacher.getRole() != Role.TEACHER) {
            throw new BusinessException(ErrorCode.ONLY_TEACHER_CAN_VIEW_OWN_LECTURES);
        }

        return lectureRepository.findByTeacherOrderByCreatedAtDesc(teacher)
                .stream()
                .map(LectureListResponseDto::from)
                .toList();
    }

    @Override
    @Transactional
    public String deleteLecture(String userEmail, Long lectureId) {
        Users teacher = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));

        if (!lecture.getTeacher().getUserId().equals(teacher.getUserId())) {
            throw new BusinessException(ErrorCode.ONLY_OWNER_CAN_DELETE_LECTURE);
        }

        fileStorageService.deleteFile(lecture.getVideoPath());
        fileStorageService.deleteFile(lecture.getThumbnailPath());

        lectureRepository.delete(lecture);

        return "강의가 삭제되었습니다.";
    }
}