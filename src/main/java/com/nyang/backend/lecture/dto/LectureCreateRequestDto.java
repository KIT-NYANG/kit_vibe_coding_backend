package com.nyang.backend.lecture.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class LectureCreateRequestDto {
    private Long lectureClassId;
    private String title;
    private String description;
    private MultipartFile videoFile;
    private MultipartFile thumbnailFile;
}