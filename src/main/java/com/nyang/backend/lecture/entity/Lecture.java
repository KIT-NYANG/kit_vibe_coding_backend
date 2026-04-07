package com.nyang.backend.lecture.entity;

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

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, length = 500)
    private String videoPath;

    @Column(length = 500)
    private String thumbnailPath;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static Lecture create(Users teacher, String category, String title,
                                 String description, String videoPath, String thumbnailPath) {
        return Lecture.builder()
                .teacher(teacher)
                .category(category)
                .title(title)
                .description(description)
                .videoPath(videoPath)
                .thumbnailPath(thumbnailPath)
                .createdAt(LocalDateTime.now())
                .build();
    }
}