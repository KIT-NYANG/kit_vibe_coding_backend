package com.nyang.backend.lecture.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SttSegmentDto {
    private Integer index;
    private Double start;
    private Double end;
    private String text;
}