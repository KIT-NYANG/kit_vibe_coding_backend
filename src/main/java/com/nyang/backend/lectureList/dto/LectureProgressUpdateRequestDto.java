package com.nyang.backend.lectureList.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LectureProgressUpdateRequestDto {
    // 수강 진행도 업데이트 dto
    private Integer watchTimeSeconds; // 강의 시청 시간
    private Integer progressPercent; // 수강 진행도
}