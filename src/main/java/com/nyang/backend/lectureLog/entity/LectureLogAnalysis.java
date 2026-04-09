package com.nyang.backend.lectureLog.entity;

import com.nyang.backend.lecture.entity.Lecture;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lecture_log_analysis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LectureLogAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    /**
     * STT 기반 사전 분석 결과
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String preResultJson;

    /**
     * 로그 집계 기반 분석 결과
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String aggregateResultJson;

    /**
     * 마지막 aggregate 분석이 실행된 기준 session 수
     * 예: 0, 10, 100, 1000
     */
    @Column(nullable = false)
    private Integer lastAggregatedSessionCount;

    private LocalDateTime preAnalyzedAt;
    private LocalDateTime aggregateAnalyzedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static LectureLogAnalysis create(Lecture lecture) {
        return LectureLogAnalysis.builder()
                .lecture(lecture)
                .preResultJson(null)
                .aggregateResultJson(null)
                .lastAggregatedSessionCount(0)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void updatePreResult(String preResultJson) {
        this.preResultJson = preResultJson;
        this.preAnalyzedAt = LocalDateTime.now();
    }

    public void updateAggregateResult(String aggregateResultJson, int aggregatedSessionCount) {
        this.aggregateResultJson = aggregateResultJson;
        this.lastAggregatedSessionCount = aggregatedSessionCount;
        this.aggregateAnalyzedAt = LocalDateTime.now();
    }
}