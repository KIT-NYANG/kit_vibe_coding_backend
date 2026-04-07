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

    @Column(length = 500)
    private String thumbnailPath;

    @Column(nullable = false)
    private Integer durationSeconds;

    @Column(nullable = false)
    private LocalDateTime createdAt;

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
                .isDeleted(false)
                .build();
    }

    // 강의 영상도 soft del
    public void softDelete() {
        this.isDeleted = true;
    }
}