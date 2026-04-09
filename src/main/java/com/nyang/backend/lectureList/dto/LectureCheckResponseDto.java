package com.nyang.backend.lectureList.dto;

import lombok.*;

@Getter
@AllArgsConstructor
public class LectureCheckResponseDto {
    // 수강 중인 강좌인지 check
    private Boolean isEnrolled;
}
