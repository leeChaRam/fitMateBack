package com.fitmate.fit_mate_server.domain.mate;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MateMemberResponse {
    private Long memberId;
    private String name;
    private String profileImageUrl;
    private MateMemberRole role;
    private LocalDateTime joinedAt;

    public static MateMemberResponse from(MateMember mateMember) {
        return MateMemberResponse.builder()
                .memberId(mateMember.getMember().getId())
                .name(mateMember.getMember().getName())
                .profileImageUrl(mateMember.getMember().getProfileImageUrl())
                .role(mateMember.getRole())
                .joinedAt(mateMember.getJoinedAt())
                .build();
    }
    
}
