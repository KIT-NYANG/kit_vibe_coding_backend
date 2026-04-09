package com.nyang.backend.lecture.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SttResponseDto {
    private String language;

    @JsonProperty("duration_sec")
    private Double durationSec;

    @JsonProperty("full_text")
    private String fullText;

    private List<SttSegmentDto> segments;

    private SttSummaryDto summarize;
}