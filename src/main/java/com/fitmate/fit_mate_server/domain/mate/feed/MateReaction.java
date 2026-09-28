package com.fitmate.fit_mate_server.domain.mate.feed;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fitmate.fit_mate_server.domain.mate.Mate;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "mate_reaction",
    uniqueConstraints = @UniqueConstraint(columnNames = {"mate_id", "post_type", "post_id", "member_id"})
)
public class MateReaction {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mate_id")
    @JsonIgnore
    private Mate mate;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false)
    private FeedPostType postType;

    @Column(name = "post_id", nullable = false)
    private Long postId; // BodyInfo.id 또는 Workout.id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MateReactionEmoji emoji;

    private LocalDateTime createdAt;

    @Builder
    public MateReaction(Mate mate, FeedPostType postType, Long postId, Member member, MateReactionEmoji emoji) {
        this.mate = mate;
        this.postType = postType;
        this.postId = postId;
        this.member = member;
        this.emoji = emoji;
    }

    public void updateEmoji(MateReactionEmoji emoji) {
        this.emoji = emoji;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
