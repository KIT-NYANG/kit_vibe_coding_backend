package com.nyang.backend.lecture.dto;

import com.nyang.backend.lecture.entity.Lecture;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LectureListResponseDto {

    private Long lectureId;
    private String teacherName;
    private Long lectureClassId;
    private String category;
    private String title;
    private String thumbnailUrl;
    private Integer durationSeconds;
    private Integer sequence;
    private LocalDateTime createdAt;

    public static LectureListResponseDto from(Lecture lecture) {
        return LectureListResponseDto.builder()
                .lectureId(lecture.getLectureId())
                .teacherName(lecture.getTeacher().getName())
                .lectureClassId(
                        lecture.getLectureClass() != null
                                ? lecture.getLectureClass().getLectureClassId()
                                : null
                )
                .category(lecture.getLectureClass() != null
                        ? lecture.getLectureClass().getCategory()
                        : null
                )
                .title(lecture.getTitle())
                .thumbnailUrl(lecture.getThumbnailPath())
                .durationSeconds(lecture.getDurationSeconds())
                .sequence(lecture.getSequence())
                .createdAt(lecture.getCreatedAt())
                .build();
    }
}