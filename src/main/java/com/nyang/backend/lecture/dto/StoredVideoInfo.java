package com.nyang.backend.lecture.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class StoredVideoInfo {
    private String videoPath;
    private Integer durationSeconds;
    private File tempFile;

    public StoredVideoInfo(String videoPath, Integer durationSeconds){
        this.videoPath = videoPath;
        this.durationSeconds = durationSeconds;
    }
    public StoredVideoInfo(String videoPath, Integer durationSeconds,File tempFile){
        this.videoPath = videoPath;
        this.durationSeconds = durationSeconds;
        this.tempFile = tempFile;
    }
}