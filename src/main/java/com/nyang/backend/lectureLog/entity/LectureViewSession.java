package com.nyang.backend.lectureLog.entity;

import com.nyang.backend.lecture.entity.Lecture;
import com.nyang.backend.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "lecture_view_session",
        indexes = {
                @Index(name = "idx_lvs_lecture_user", columnList = "lecture_id, user_id"),
                @Index(name = "idx_lvs_session_id", columnList = "sessionId", unique = true)
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LectureViewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 어떤 사용자의 시청 세션인지
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private Users user;

    /**
     * 어떤 강의의 세션인지
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    /**
     * 프론트에서 생성해서 넘겨주는 sessionId
     */
    @Column(nullable = false, unique = true, length = 100)
    private String sessionId;

    /**
     * 이 세션이 시작된 시각
     */
    @Column(nullable = false)
    private LocalDateTime startedAt;

    /**
     * 이 세션이 종료된 시각
     */
    private LocalDateTime endedAt;

    /**
     * 마지막 시청 위치(초)
     */
    @Column(nullable = false)
    private Integer lastPositionSec;

    /**
     * 총 시청 시간(초)
     * HEARTBEAT 기반으로 누적
     */
    @Column(nullable = false)
    private Integer totalWatchTimeSec;

    /**
     * seek 횟수
     */
    @Column(nullable = false)
    private Integer seekCount;

    /**
     * pause 횟수
     */
    @Column(nullable = false)
    private Integer pauseCount;

    /**
     * heartbeat 횟수
     */
    @Column(nullable = false)
    private Integer heartbeatCount;

    /**
     * 완강 여부
     * ENDED면 true, PAGE_EXIT면 false일 가능성이 큼
     */
    @Column(nullable = false)
    private Boolean completed;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static LectureViewSession create(Users user, Lecture lecture, String sessionId, LocalDateTime startedAt) {
        LocalDateTime now = LocalDateTime.now();
        return LectureViewSession.builder()
                .user(user)
                .lecture(lecture)
                .sessionId(sessionId)
                .startedAt(startedAt)
                .lastPositionSec(0)
                .totalWatchTimeSec(0)
                .seekCount(0)
                .pauseCount(0)
                .heartbeatCount(0)
                .completed(false)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * 로그가 들어올 때 세션의 요약값을 갱신한다.
     */
    public void applyLog(LogEventType eventType, Integer currentTimeSec, Integer fromTimeSec, Integer toTimeSec, Double playbackRate) {
        this.updatedAt = LocalDateTime.now();

        switch (eventType) {
            case PLAY -> {
                if (currentTimeSec != null) {
                    this.lastPositionSec = currentTimeSec;
                }
            }
            case PAUSE -> {
                this.pauseCount += 1;
                if (currentTimeSec != null) {
                    this.lastPositionSec = currentTimeSec;
                }
            }
            case SEEK -> {
                this.seekCount += 1;
                if (toTimeSec != null) {
                    this.lastPositionSec = toTimeSec;
                } else if (currentTimeSec != null) {
                    this.lastPositionSec = currentTimeSec;
                }
            }
            case HEARTBEAT -> {
                this.heartbeatCount += 1;
                // 프론트가 10초마다 heartbeat를 보낸다고 가정
                this.totalWatchTimeSec += 10;
                if (currentTimeSec != null) {
                    this.lastPositionSec = currentTimeSec;
                }
            }
            case ENDED -> {
                this.completed = true;
                this.endedAt = LocalDateTime.now();
                if (currentTimeSec != null) {
                    this.lastPositionSec = currentTimeSec;
                }
            }
            case PAGE_EXIT -> {
                this.endedAt = LocalDateTime.now();
                if (currentTimeSec != null) {
                    this.lastPositionSec = currentTimeSec;
                }
            }
        }
    }

    public boolean isValidForAggregateAnalysis() {
        return Boolean.TRUE.equals(this.completed)
                || this.heartbeatCount >= 3
                || this.totalWatchTimeSec >= 30
                || this.lastPositionSec >= 60;
//                || this.heartbeatCount >= 0
//                || this.totalWatchTimeSec >= 0
//                || this.lastPositionSec >= 0;
    }
}