package com.nyang.backend.lectureClass.entity;

import com.nyang.backend.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lecture_class")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LectureClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lectureClassId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Users teacher;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(length = 1000)
    private String description;

    @Column(length = 500)
    private String thumbnailPath;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    public static LectureClass create(
            Users teacher,
            String title,
            String category,
            String description,
            String thumbnailPath
    ) {
        return LectureClass.builder()
                .teacher(teacher)
                .title(title)
                .category(category)
                .description(description)
                .thumbnailPath(thumbnailPath)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
    }

    public void softDelete() {
        this.isDeleted = true;
    }
}