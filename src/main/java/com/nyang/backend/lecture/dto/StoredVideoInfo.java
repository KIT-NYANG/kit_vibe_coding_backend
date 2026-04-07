package com.nyang.backend.lecture.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoredVideoInfo {
    private String videoPath;
    private Integer durationSeconds;
}