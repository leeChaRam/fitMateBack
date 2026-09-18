package com.fitmate.fit_mate_server.domain.mate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MatePreviewResponse {
    private Long id;
    private String name;
    private String coverImageUrl;
    private long memberCount;
    private int maxMembers;
    private boolean full; // 정원 초과 여부
    private boolean alreadyMember; // 이미 가입된 서클인지

    private MateGoalMetric goalMetric;
    private MateGoalAggregation goalAggregation;
    private MateGoalPeriod goalPeriod;
    private Double goalTargetValue;

    public static MatePreviewResponse from(Mate mate, long memberCount, int maxMembers, boolean alreadyMember) {
        return MatePreviewResponse.builder()
                .id(mate.getId())
                .name(mate.getName())
                .coverImageUrl(mate.getCoverImageUrl())
                .memberCount(memberCount)
                .maxMembers(maxMembers)
                .full(memberCount >= maxMembers)
                .alreadyMember(alreadyMember)
                .goalMetric(mate.getGoalMetric())
                .goalAggregation(mate.getGoalAggregation())
                .goalPeriod(mate.getGoalPeriod())
                .goalTargetValue(mate.getGoalTargetValue())
                .build();
    }
    
}
 