package com.nyang.backend.lectureLog.entity;

import com.nyang.backend.lecture.entity.Lecture;
import com.nyang.backend.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lecture_view_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LectureViewLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 어떤 사용자의 로그인지
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private Users user;

    /**
     * 어떤 강의의 로그인지
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    /**
     * 프론트에서 생성해서 넘겨주는 sessionId
     * 같은 강의 시청 흐름을 묶는 기준
     */
    @Column(nullable = false, length = 100)
    private String sessionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LogEventType eventType;

    /**
     * 이벤트 발생 시점의 영상 재생 위치(초)
     * 예: PAUSE, PLAY, HEARTBEAT 시 현재 위치
     */
    private Integer currentTimeSec;

    /**
     * SEEK 전 위치
     */
    private Integer fromTimeSec;

    /**
     * SEEK 후 위치
     */
    private Integer toTimeSec;

    /**
     * 재생 속도 (1.0, 1.25, 1.5 등)
     */
    private Double playbackRate;

    /**
     * 프론트에서 이벤트가 발생한 실제 시간
     */
    @Column(nullable = false)
    private LocalDateTime occurredAt;

    /**
     * 서버 저장 시간
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}