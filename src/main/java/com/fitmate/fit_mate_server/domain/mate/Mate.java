package com.fitmate.fit_mate_server.domain.mate;

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
public class Mate {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    private String coverImageUrl;
    private String coverImagePublicId; // Cloudinary 삭제용 식별자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private Member owner;

    @Column(unique = true, nullable = false)
    private String inviteToken;

    @Enumerated(EnumType.STRING)
    private MateGoalMetric goalMetric; // null이면 목표 미설정

    @Enumerated(EnumType.STRING)
    private MateGoalAggregation goalAggregation;

    @Enumerated(EnumType.STRING)
    private MateGoalPeriod goalPeriod;

    private Double goalTargetValue;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Mate(String name, String coverImageUrl, String coverImagePublicId, Member owner, String inviteToken,
                MateGoalMetric goalMetric, MateGoalAggregation goalAggregation, MateGoalPeriod goalPeriod, Double goalTargetValue) {
        this.name = name;
        this.coverImageUrl = coverImageUrl;
        this.coverImagePublicId = coverImagePublicId;
        this.owner = owner;
        this.inviteToken = inviteToken;
        this.goalMetric = goalMetric;
        this.goalAggregation = goalAggregation;
        this.goalPeriod = goalPeriod;
        this.goalTargetValue = goalTargetValue;
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
    
    public void updateInfo(String name, String coverImageUrl, String coverImagePublicId) {
        if (name != null) this.name = name;
        if (coverImageUrl != null) {
            this.coverImageUrl = coverImageUrl;
            this.coverImagePublicId = coverImagePublicId;
        }
    }

    // metric이 null이면 목표를 통째로 삭제(미설정 상태로 전환)
    public void updateGoal(MateGoalMetric metric, MateGoalAggregation aggregation, MateGoalPeriod period, Double targetValue) {
        this.goalMetric = metric;
        this.goalAggregation = metric != null ? aggregation : null;
        this.goalPeriod = metric != null ? period : null;
        this.goalTargetValue = metric != null ? targetValue : null;
    }

    public void regenerateInviteToken(String newToken) {
        this.inviteToken = newToken;
    }

    public boolean isOwnedBy(Long memberId) {
        return this.owner.getId().equals(memberId);
    }
    
}
