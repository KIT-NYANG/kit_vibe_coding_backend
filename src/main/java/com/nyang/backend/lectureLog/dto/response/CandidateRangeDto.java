package com.nyang.backend.lectureLog.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CandidateRangeDto {

    private Integer startSec;
    private Integer endSec;

    private Integer pauseCount;
    private Integer seekBackCount;
    private Integer affectedUserCount;

    private Double score;
    private List<String> reasons;
}