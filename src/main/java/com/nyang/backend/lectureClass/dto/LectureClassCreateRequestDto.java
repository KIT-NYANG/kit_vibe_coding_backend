package com.nyang.backend.lectureClass.dto;

import com.nyang.backend.lectureClass.entity.LectureClassCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LectureClassCreateRequestDto {

    private String title;
    private LectureClassCategory category;
    private String description;
    private MultipartFile thumbnailFile;
}
