package com.nyang.backend.lecture.entity;

import com.nyang.backend.lectureClass.entity.LectureClass;
import com.nyang.backend.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lecture")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lectureId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Users teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_class_id")
    private LectureClass lectureClass;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, length = 500)
    private String videoPath;

    @Column(nullable = true, length = 500)
    private String tempVideoPath; //현재 스토리지를 사용하지 않아 절대 경로를 저장해야합니다. 스토리지 사용후 변경해야합니다.

    @Column(length = 500)
    private String thumbnailPath;

    @Column(nullable = false)
    private Integer durationSeconds;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SttStatus sttStatus;

    @Column(columnDefinition = "TEXT")
    private String transcriptFullText;

    private String transcriptLanguage;

    private LocalDateTime sttCompletedAt;

    @Column(length = 1000)
    private String sttErrorMessage;

    @Column(columnDefinition = "TEXT")
    private String summaryText;

    @Column(length = 1000)
    private String summaryKeywords;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    public static Lecture create(
            Users teacher,
            String title,
            String description,
            LectureClass lectureClass,
            Integer durationSeconds,
            String videoPath,
            String thumbnailPath
    ) {
        return Lecture.builder()
                .teacher(teacher)
                .title(title)
                .description(description)
                .lectureClass(lectureClass)
                .videoPath(videoPath)
                .thumbnailPath(thumbnailPath)
                .durationSeconds(durationSeconds)
                .createdAt(LocalDateTime.now())
                .sttStatus(SttStatus.PENDING)
                .isDeleted(false)
                .build();
    }
    public void markSttProcessing() {
        this.sttStatus = SttStatus.PROCESSING;
    }

    public void markSttCompleted(String fullText, String language,
                                 String summaryText, String summaryKeywords, LocalDateTime completedAt) {
        this.sttStatus = SttStatus.COMPLETED;
        this.transcriptFullText = fullText;
        this.transcriptLanguage = language;
        this.summaryText = summaryText;
        this.summaryKeywords = summaryKeywords;
        this.sttCompletedAt = completedAt;
        this.sttErrorMessage = null;
    }

    public void markSttFailed(String errorMessage) {
        this.sttStatus = SttStatus.FAILED;
        this.sttErrorMessage = errorMessage;
    }

    // 강의 영상도 soft del
    public void softDelete() {
        this.isDeleted = true;
    }
}