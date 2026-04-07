package com.nyang.backend.lectureList.dto;

import com.nyang.backend.lectureClass.entity.LectureClass;
import com.nyang.backend.lectureList.entity.LectureList;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class MyLectureListResponseDto {
    private Long lectureListId;
    private Long lectureClassId;
    private String title;
    private String category;
    private String description;
    private String thumbnailUrl;
    private LocalDateTime createdAt;

    public static MyLectureListResponseDto from(LectureList lectureList) {
        LectureClass lectureClass = lectureList.getLectureClass();

        return MyLectureListResponseDto.builder()
                .lectureListId(lectureList.getLectureListId())
                .lectureClassId(lectureClass.getLectureClassId())
                .title(lectureClass.getTitle())
                .category(lectureClass.getCategory())
                .description(lectureClass.getDescription())
                .thumbnailUrl(lectureClass.getThumbnailPath())
                .createdAt(lectureList.getCreatedAt())
                .build();
    }
}