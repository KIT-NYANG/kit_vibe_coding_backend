package com.nyang.backend.lecture.service;

import com.nyang.backend.global.exception.BusinessException;
import com.nyang.backend.global.exception.ErrorCode;
import com.nyang.backend.lecture.dto.LectureCreateRequestDto;
import com.nyang.backend.lecture.dto.LectureListResponseDto;
import com.nyang.backend.lecture.dto.LectureResponseDto;
import com.nyang.backend.lecture.entity.Lecture;
import com.nyang.backend.lecture.event.LectureCreatedEvent;
import com.nyang.backend.lecture.repository.LectureRepository;
import com.nyang.backend.lecture.storage.FileStorageService;
import com.nyang.backend.lecture.dto.StoredVideoInfo;
import com.nyang.backend.lectureClass.entity.LectureClass;
import com.nyang.backend.lectureClass.repository.LectureClassRepository;
import com.nyang.backend.user.entity.Role;
import com.nyang.backend.user.entity.Users;
import com.nyang.backend.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureServiceImpl implements LectureService {

    private final LectureRepository lectureRepository;
    private final LectureClassRepository lectureClassRepository;
    private final UsersRepository usersRepository;
    private final FileStorageService fileStorageService;
    private final ApplicationEventPublisher eventPublisher;
    private final LectureSttService lectureSttService;

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

        LectureClass lectureClass = lectureClassRepository.findById(requestDto.getLectureClassId())
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_CLASS_NOT_FOUND));

        StoredVideoInfo videoInfo = fileStorageService.saveVideo(requestDto.getVideoFile());
        String thumbnailPath = fileStorageService.saveThumbnail(requestDto.getThumbnailFile());

        Lecture lecture = Lecture.create(
                teacher,
                requestDto.getTitle(),
                requestDto.getDescription(),
                lectureClass,
                videoInfo.getDurationSeconds(),
                videoInfo.getVideoPath(),
                thumbnailPath
        );

        Lecture savedLecture = lectureRepository.save(lecture);

        File tempFile = createTempFile(requestDto.getVideoFile());
        lectureSttService.requestSttAndSave(savedLecture.getLectureId(), tempFile);

        return LectureResponseDto.from(savedLecture);
    }

    @Override
    public List<LectureListResponseDto> getAllLectures() {
        return lectureRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(LectureListResponseDto::from)
                .toList();
    }

    @Override
    public LectureResponseDto getLectureDetail(Long lectureId) {
        Lecture lecture = lectureRepository.findByLectureIdAndIsDeletedFalse(lectureId)
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

        return lectureRepository.findByTeacherAndIsDeletedFalseOrderByCreatedAtDesc(teacher)
                .stream()
                .map(LectureListResponseDto::from)
                .toList();
    }

    @Override
    @Transactional
    public String deleteLecture(String userEmail, Long lectureId) {
        Users teacher = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Lecture lecture = lectureRepository.findByLectureIdAndIsDeletedFalse(lectureId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));

        if (!lecture.getTeacher().getUserId().equals(teacher.getUserId())) {
            throw new BusinessException(ErrorCode.ONLY_OWNER_CAN_DELETE_LECTURE);
        }

        fileStorageService.deleteFile(lecture.getVideoPath());
        fileStorageService.deleteFile(lecture.getThumbnailPath());

        lectureRepository.delete(lecture);

        return "강의가 삭제되었습니다.";
    }


    private File createTempFile(MultipartFile multipartFile) {
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            String suffix = ".tmp";

            if (originalFilename != null && originalFilename.contains(".")) {
                suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            File tempFile = File.createTempFile("stt-upload-", suffix);
            multipartFile.transferTo(tempFile);
            return tempFile;
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.VIDEO_SAVE_FAILED);
        }
    }
}