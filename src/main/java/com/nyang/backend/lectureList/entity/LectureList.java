package com.nyang.backend.lectureList.entity;

import com.nyang.backend.lectureClass.entity.LectureClass;
import com.nyang.backend.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "lecture_list",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "lecture_class_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LectureList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lectureListId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_class_id", nullable = false)
    private LectureClass lectureClass;

    private LocalDateTime createdAt; // 수강 시작일

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false; // 수강 삭제 여부

    public static LectureList create(Users users, LectureClass lectureClass) {
        return LectureList.builder()
                .users(users)
                .lectureClass(lectureClass)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
    }

    // 삭제는 soft하게 (수강 기록 보존)
    public void softDelete() {
        this.isDeleted = true;
    }
}
