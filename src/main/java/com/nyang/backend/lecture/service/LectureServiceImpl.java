package com.nyang.backend.lecture.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyang.backend.global.exception.BusinessException;
import com.nyang.backend.global.exception.ErrorCode;
import com.nyang.backend.global.response.PageResponseDto;
import com.nyang.backend.lecture.dto.*;
import com.nyang.backend.lecture.entity.Lecture;
import com.nyang.backend.lecture.event.LectureCreatedEvent;
import com.nyang.backend.lecture.repository.LectureRepository;
import com.nyang.backend.lecture.storage.FileStorageService;
import com.nyang.backend.lectureClass.entity.LectureClass;
import com.nyang.backend.lectureClass.repository.LectureClassRepository;
import com.nyang.backend.lectureLog.entity.LectureLogAnalysis;
import com.nyang.backend.lectureLog.repository.LectureLogAnalysisRepository;
import com.nyang.backend.user.entity.Role;
import com.nyang.backend.user.entity.Users;
import com.nyang.backend.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
    private final LectureLogAnalysisRepository lectureLogAnalysisRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public LectureResponseDto createLecture(String userEmail, LectureCreateRequestDto requestDto) {
        Users teacher = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 강사 권한이 아니면 강의 업로드 불가
        if (teacher.getRole() != Role.TEACHER) {
            throw new BusinessException(ErrorCode.ONLY_TEACHER_CAN_UPLOAD);
        }

        // 영상 파일은 필수값 검증
        if (requestDto.getVideoFile() == null || requestDto.getVideoFile().isEmpty()) {
            throw new BusinessException(ErrorCode.VIDEO_FILE_REQUIRED);
        }

        // 강의가 속할 강좌 조회
        LectureClass lectureClass = lectureClassRepository.findById(requestDto.getLectureClassId())
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_CLASS_NOT_FOUND));

        // 영상 저장 후 경로 및 재생 시간 정보 반환
        StoredVideoInfo videoInfo = fileStorageService.saveVideo(requestDto.getVideoFile());
        // 썸네일 저장 후 경로 반환
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

//        File tempFile = createTempFile(requestDto.getVideoFile());
//        lectureSttService.requestSttAndSave(savedLecture.getLectureId(), tempFile);
        File tempFile = createTempFile(requestDto.getVideoFile());
        lectureSttService.requestSttAsync(savedLecture.getLectureId(), tempFile);
        return LectureResponseDto.from(savedLecture);
    }

    @Override
    public PageResponseDto<LectureListResponseDto> getAllLectures(
            int page, int size, Long lectureClassId, String keyword
    ) {
        // 검색어 공백 제거
        keyword = (keyword == null) ? null : keyword.replaceAll("\\s+", "").trim();

        // 페이지 번호, 크기, 정렬 조건 설정
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        boolean hasLectureClassId = lectureClassId != null;
        boolean hasKeyword = keyword != null && !keyword.isEmpty();

        Page<Lecture> lecturePage;

        // 강좌 필터 + 제목 검색
        if (hasLectureClassId && hasKeyword) {
            lecturePage = lectureRepository
                    .findByIsDeletedFalseAndLectureClass_LectureClassIdAndTitleContainingIgnoreSpace(
                            lectureClassId, keyword, pageable
                    );
        } else if (hasLectureClassId) { // 강좌 필터만 적용
            lecturePage = lectureRepository
                    .findByIsDeletedFalseAndLectureClass_LectureClassId(
                            lectureClassId, pageable
                    );
        } else if (hasKeyword) { // 제목 검색만 적용
            lecturePage = lectureRepository
                    .findByIsDeletedFalseAndTitleContainingIgnoreSpace(
                            keyword, pageable
                    );
        } else { // 조건 없이 전체 조회
            lecturePage = lectureRepository.findAllByIsDeletedFalse(pageable);
        }

        return PageResponseDto.from(
                lecturePage.map(LectureListResponseDto::from)
        );
    }

    @Override
    public LectureResponseDto getLectureDetail(Long lectureId) {
        Lecture lecture = lectureRepository.findByLectureIdAndIsDeletedFalse(lectureId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));

        AnalysisDto analysisDto = null;

        Optional<LectureLogAnalysis> analysisOpt =
                lectureLogAnalysisRepository.findByLecture_LectureId(lectureId);

        if (analysisOpt.isPresent()) {
            LectureLogAnalysis analysis = analysisOpt.get();

            try {
                if (analysis.getAggregateResultJson() != null && !analysis.getAggregateResultJson().isBlank()) {
                    AggAnalysisDto aggAnalysisDto = objectMapper.readValue(
                            analysis.getAggregateResultJson(),
                            AggAnalysisDto.class
                    );
                    analysisDto = toAnalysisDto(aggAnalysisDto);
                } else if (analysis.getPreResultJson() != null && !analysis.getPreResultJson().isBlank()) {
                    PreAnalysisDto preAnalysisDto = objectMapper.readValue(
                            analysis.getPreResultJson(),
                            PreAnalysisDto.class
                    );
                    analysisDto = toAnalysisDto(preAnalysisDto);
                }
            } catch (JsonProcessingException e) {
                throw new BusinessException(ErrorCode.JSON_SERIALIZATION_ERROR);
            }
        }

        return LectureResponseDto.from(lecture, analysisDto);
    }

    @Override
    public PageResponseDto<LectureListResponseDto> getMyLectures(
            String userEmail, int page, int size, Long lectureClassId, String keyword
    ) {
        Users teacher = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 강사 권한이 아니면 본인 강의 조회 불가
        if (teacher.getRole() != Role.TEACHER) {
            throw new BusinessException(ErrorCode.ONLY_TEACHER_CAN_VIEW_OWN_LECTURES);
        }

        // 검색어 공백 제거
        keyword = (keyword == null) ? null : keyword.replaceAll("\\s+", "").trim();

        // 페이지 번호, 크기, 정렬 조건 설정
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        boolean hasLectureClassId = lectureClassId != null;
        boolean hasKeyword = keyword != null && !keyword.isEmpty();

        Page<Lecture> lecturePage;

        // 내 강의 조회 - 강좌 필터 + 제목 검색
        if (hasLectureClassId && hasKeyword) {
            lecturePage = lectureRepository
                    .findByTeacherAndIsDeletedFalseAndLectureClass_LectureClassIdAndTitleContainingIgnoreSpace(
                            teacher, lectureClassId, keyword, pageable
                    );
        } else if (hasLectureClassId) { // 내 강의 조회 - 강좌 필터만 적용
            lecturePage = lectureRepository
                    .findByTeacherAndIsDeletedFalseAndLectureClass_LectureClassId(
                            teacher, lectureClassId, pageable
                    );
        } else if (hasKeyword) { // 내 강의 조회 - 제목 검색만 적용
            lecturePage = lectureRepository
                    .findByTeacherAndIsDeletedFalseAndTitleContainingIgnoreSpace(
                            teacher, keyword, pageable
                    );
        } else { // 내 강의 전체 조회
            lecturePage = lectureRepository
                    .findByTeacherAndIsDeletedFalse(teacher, pageable);
        }

        return PageResponseDto.from(
                lecturePage.map(LectureListResponseDto::from)
        );
    }

    @Override
    @Transactional
    public String deleteLecture(String userEmail, Long lectureId) {
        Users teacher = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 삭제되지 않은 강의 조회
        Lecture lecture = lectureRepository.findByLectureIdAndIsDeletedFalse(lectureId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));

        // 본인이 등록한 강의가 아니면 삭제 불가
        if (!lecture.getTeacher().getUserId().equals(teacher.getUserId())) {
            throw new BusinessException(ErrorCode.ONLY_OWNER_CAN_DELETE_LECTURE);
        }

        fileStorageService.deleteFile(lecture.getVideoPath()); // 저장된 영상 파일 삭제
        fileStorageService.deleteFile(lecture.getThumbnailPath()); // 저장된 썸네일 파일 삭제

        lecture.softDelete();

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

    private AnalysisDto toAnalysisDto(PreAnalysisDto preAnalysisDto) {
        return AnalysisDto.builder()
                .quizzes(preAnalysisDto.getQuizzes())
                .teacherGuides(preAnalysisDto.getTeacherGuides())
                .build();
    }
    private AnalysisDto toAnalysisDto(AggAnalysisDto aggAnalysisDto) {
        return AnalysisDto.builder()
                .quizzes(aggAnalysisDto.getQuizzes())
                .teacherGuides(aggAnalysisDto.getTeacherGuides())
                .build();
    }
}