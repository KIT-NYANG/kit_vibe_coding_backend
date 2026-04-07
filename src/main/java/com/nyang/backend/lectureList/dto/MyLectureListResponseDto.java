package com.nyang.backend.lectureList.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyLectureListResponseDto {
    private Long lectureListId;
    private Long lectureClassId;
    private String title;
    private String category;
    private String description;
    private String thumbnailPath;
    private LocalDateTime createdAt;
}