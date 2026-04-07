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
    private String category;
    private String title;
    private String thumbnailUrl;
    private LocalDateTime createdAt;

    public static LectureListResponseDto from(Lecture lecture) {
        return LectureListResponseDto.builder()
                .lectureId(lecture.getLectureId())
                .teacherName(lecture.getTeacher().getName())
                .category(lecture.getCategory())
                .title(lecture.getTitle())
                .thumbnailUrl(lecture.getThumbnailPath())
                .createdAt(lecture.getCreatedAt())
                .build();
    }
}