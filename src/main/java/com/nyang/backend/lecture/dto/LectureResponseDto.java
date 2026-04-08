package com.nyang.backend.lecture.dto;

import com.nyang.backend.lecture.entity.Lecture;
import com.nyang.backend.lecture.entity.SttStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LectureResponseDto {

    private Long lectureId;
    private String teacherName;
    private Long lectureClassId;
    private String title;
    private String description;
    private Integer durationSeconds;
    private String videoUrl;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private SttStatus sttStatus;
    private String transcriptFullText;
    private String transcriptLanguage;
    private String sttErrorMessage;
    private LocalDateTime sttCompletedAt;

    public static LectureResponseDto from(Lecture lecture) {
        return LectureResponseDto.builder()
                .lectureId(lecture.getLectureId())
                .teacherName(lecture.getTeacher().getName())
                .lectureClassId(lecture.getLectureClass() != null ? lecture.getLectureClass().getLectureClassId() : null)
                .title(lecture.getTitle())
                .description(lecture.getDescription())
                .durationSeconds(lecture.getDurationSeconds())
                .videoUrl(lecture.getVideoPath())
                .thumbnailUrl(lecture.getThumbnailPath())
                .sttStatus(lecture.getSttStatus())
                .transcriptFullText(lecture.getTranscriptFullText())
                .transcriptLanguage(lecture.getTranscriptLanguage())
                .sttCompletedAt(lecture.getSttCompletedAt())
                .sttErrorMessage(lecture.getSttErrorMessage())
                .createdAt(lecture.getCreatedAt())
                .build();
    }
}