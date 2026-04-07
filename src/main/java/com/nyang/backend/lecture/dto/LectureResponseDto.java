package com.nyang.backend.lecture.dto;

import com.nyang.backend.lecture.entity.Lecture;
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
                .createdAt(lecture.getCreatedAt())
                .build();
    }
}