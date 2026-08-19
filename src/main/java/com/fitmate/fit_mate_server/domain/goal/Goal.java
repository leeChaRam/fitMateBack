package com.fitmate.fit_mate_server.domain.goal;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fitmate.fit_mate_server.domain.member.Member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "goal",
    uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "metric"})
)
public class Goal {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalMetric metric; // WEIGHT, MUSCLE_MASS, BODY_FAT_MASS, BODY_FAT_PERCENT

    @Column(nullable = false)
    private Double targetValue;

    private LocalDate targetDate; // 목표 기한 (선택)

    @Column(nullable = false)
    private boolean active = true; // 비활성화 = row 삭제 대신 플래그로 (히스토리 보존)

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Goal(Member member, GoalMetric metric, Double targetValue, LocalDate targetDate) {
        this.member = member;
        this.metric = metric;
        this.targetValue = targetValue;
        this.targetDate = targetDate;
        this.active = true;
    }

    public void update(Double targetValue, LocalDate targetDate) {
    this.targetValue = targetValue;
    this.targetDate = targetDate;
    this.active = true; // 수정 = 다시 활성 상태
}

    public void deactivate() {
        this.active = false;
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
