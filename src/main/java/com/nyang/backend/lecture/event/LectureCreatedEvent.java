package com.nyang.backend.lecture.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LectureCreatedEvent {
    private final Long lectureId;
}