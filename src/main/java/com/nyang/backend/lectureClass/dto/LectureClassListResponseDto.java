package com.nyang.backend.lectureClass.dto;

import com.nyang.backend.lectureClass.entity.LectureClass;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LectureClassListResponseDto {

    private Long lectureClassId;
    private String teacherName;
    private String title;
    private String category;
    private String description;
    private String thumbnailUrl;
    private LocalDateTime createdAt;

    public static LectureClassListResponseDto from(LectureClass lectureClass) {
        return LectureClassListResponseDto.builder()
                .lectureClassId(lectureClass.getLectureClassId())
                .teacherName(lectureClass.getTeacher().getName())
                .title(lectureClass.getTitle())
                .category(lectureClass.getCategory().name())
                .description(lectureClass.getDescription())
                .thumbnailUrl(lectureClass.getThumbnailPath())
                .createdAt(lectureClass.getCreatedAt())
                .build();
    }
}