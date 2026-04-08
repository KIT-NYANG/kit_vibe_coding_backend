package com.nyang.backend.lecture.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "lecture_transcript_segment",
        indexes = {
                @Index(name = "idx_lecture_transcript_segment_lecture_id", columnList = "lecture_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureTranscriptSegment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @Column(nullable = false)
    private Integer segmentIndex;

    @Column(nullable = false)
    private Long startMs;

    @Column(nullable = false)
    private Long endMs;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Builder
    public LectureTranscriptSegment(
            Lecture lecture,
            Integer segmentIndex,
            Long startMs,
            Long endMs,
            String text
    ) {
        this.lecture = lecture;
        this.segmentIndex = segmentIndex;
        this.startMs = startMs;
        this.endMs = endMs;
        this.text = text;
    }
}