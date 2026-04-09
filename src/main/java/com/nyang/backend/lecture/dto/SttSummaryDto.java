package com.nyang.backend.lecture.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
public class SttSummaryDto {
    // 강의 요약 내용과 키워드
    @JsonProperty("summary_text")
    private String summaryText;

    private List<String> keywords;
}
