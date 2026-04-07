package com.nyang.backend.lecture.service;

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
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (teacher.getRole() != Role.TEACHER) {
            throw new IllegalArgumentException("강의 업로드는 강사만 가능합니다.");
        }

        if (requestDto.getVideoFile() == null || requestDto.getVideoFile().isEmpty()) {
            throw new IllegalArgumentException("영상 파일은 필수입니다.");
        }

        String videoPath = fileStorageService.saveVideo(requestDto.getVideoFile());
        String thumbnailPath = fileStorageService.saveThumbnail(requestDto.getThumbnailFile());

        Lecture lecture = Lecture.create(
                teacher,
                requestDto.getCategory(),
                requestDto.getTitle(),
                requestDto.getDescription(),
                videoPath,
                thumbnailPath
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
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));

        return LectureResponseDto.from(lecture);
    }

    @Override
    public List<LectureListResponseDto> getMyLectures(String userEmail) {
        Users teacher = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (teacher.getRole() != Role.TEACHER) {
            throw new IllegalArgumentException("강사만 본인 강의를 조회할 수 있습니다.");
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
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));

        if (!lecture.getTeacher().getUserId().equals(teacher.getUserId())) {
            throw new IllegalArgumentException("본인이 업로드한 강의만 삭제할 수 있습니다.");
        }

        fileStorageService.deleteFile(lecture.getVideoPath());
        fileStorageService.deleteFile(lecture.getThumbnailPath());

        lectureRepository.delete(lecture);

        return "강의가 삭제되었습니다.";
    }
}