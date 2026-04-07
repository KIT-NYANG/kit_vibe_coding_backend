package com.nyang.backend.lectureList.entity;

import com.nyang.backend.user.entity.Users;
import com.nyang.backend.lecture.entity.Lecture;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "lecture_list",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "lecture_id"})
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
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @Column(nullable = false)
    @Builder.Default
    private Integer watchTimeSeconds = 0; // 강의 시청 시간

    @Column(nullable = false)
    @Builder.Default
    private Integer progressPercent = 0; // 수강 진행도

    private LocalDateTime startedAt; // 수강 시작일

    private LocalDateTime completedAt; // 수강 완료일

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false; // 수강 삭제 여부

    // 진행도 업데이트 메서드 (진행도가 변경될 때의 규칙을 Service에 두면 비대해지기 때문에 상태 변경 책임을 엔티티가 가지게 함)
    public void updateProgress(Integer watchTimeSeconds, Integer progressPercent) {
        this.watchTimeSeconds = watchTimeSeconds;
        this.progressPercent = progressPercent;

        if (this.startedAt == null && watchTimeSeconds != null && watchTimeSeconds > 0) {
            this.startedAt = LocalDateTime.now();
        }

        if (progressPercent != null && progressPercent >= 100) {
            this.progressPercent = 100;
            if (this.completedAt == null) {
                this.completedAt = LocalDateTime.now(); // 한 번 완료되면 completedAt 유지
            }
        }
    }

    // 삭제는 soft하게 (수강 기록 보존)
    public void softDelete() {
        this.isDeleted = true;
    }
}
