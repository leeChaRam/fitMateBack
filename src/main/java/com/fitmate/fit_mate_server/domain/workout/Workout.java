package com.fitmate.fit_mate_server.domain.workout;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fitmate.fit_mate_server.domain.member.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Workout {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkoutType workoutType;

    private String customType; // workoutType == OTHER 일 때만 사용

    @Column(nullable = false)
    private Integer durationMinutes; // 분 단위

    @Enumerated(EnumType.STRING)
    private WorkoutIntensity intensity; // 선택 입력

    @Column(columnDefinition = "TEXT")
    private String memo; // 선택 입력

    @Column(nullable = false)
    private LocalDate recordDate; // 기록 날짜 (오늘 또는 과거만 허용, 검증은 Service에서)

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Workout(Member member, WorkoutType workoutType, String customType, Integer durationMinutes,
                    WorkoutIntensity intensity, String memo, LocalDate recordDate) {
        this.member = member;
        this.workoutType = workoutType;
        this.customType = customType;
        this.durationMinutes = durationMinutes;
        this.intensity = intensity;
        this.memo = memo;
        this.recordDate = recordDate;
    }

    public void update(WorkoutType workoutType, String customType, Integer durationMinutes,
                        WorkoutIntensity intensity, String memo, LocalDate recordDate) {
        this.workoutType = workoutType;
        this.customType = customType;
        this.durationMinutes = durationMinutes;
        this.intensity = intensity;
        this.memo = memo;
        this.recordDate = recordDate;
    }

    public boolean isOwnedBy(Long memberId) {
        return this.member.getId().equals(memberId);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
