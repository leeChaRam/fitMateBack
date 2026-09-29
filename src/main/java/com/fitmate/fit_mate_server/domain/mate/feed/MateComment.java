package com.fitmate.fit_mate_server.domain.mate.feed;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fitmate.fit_mate_server.domain.mate.Mate;
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
public class MateComment {

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
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member; // 작성자

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;

    @Builder
    public MateComment(Mate mate, FeedPostType postType, Long postId, Member member, String content) {
        this.mate = mate;
        this.postType = postType;
        this.postId = postId;
        this.member = member;
        this.content = content;
    }

    public boolean isWrittenBy(Long memberId) {
        return this.member.getId().equals(memberId);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
