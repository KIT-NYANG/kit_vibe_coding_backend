package com.nyang.backend.lectureList.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LectureProgressUpdateRequestDto {
    // 수강 진행도 업데이트 dto
    private Integer watchTimeSeconds; // 강의 시청 시간 - 시청 위치만 보내고 서버가 계산하는 방식으로 진행
}