package com.nyang.backend.lectureList.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LectureListResponseDto {
    private Long lectureListId;
    private Long lectureId;
    private String title;
    private String thumbnail;
    private Integer progressPercent;
    private Integer watchTimeSeconds;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}