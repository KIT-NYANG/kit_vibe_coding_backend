package com.nyang.backend.lectureList.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LectureListCreateRequestDto {
    // 수강 신청 요청 dto
    private Long userId;
    private Long lectureId;
}
