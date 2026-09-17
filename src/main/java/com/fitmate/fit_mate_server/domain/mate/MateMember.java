package com.fitmate.fit_mate_server.domain.mate;

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
    name = "mate_member",
    uniqueConstraints = @UniqueConstraint(columnNames = {"mate_id", "member_id"})
)
public class MateMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mate_id")
    @JsonIgnore
    private Mate mate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MateMemberRole role;

    private LocalDateTime joinedAt;

    @Builder
    public MateMember(Mate mate, Member member, MateMemberRole role) {
        this.mate = mate;
        this.member = member;
        this.role = role;
    }

    @PrePersist
    public void prePersist() {
        this.joinedAt = LocalDateTime.now();
    }
}