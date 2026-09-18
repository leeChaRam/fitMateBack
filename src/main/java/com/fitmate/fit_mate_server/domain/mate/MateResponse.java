package com.fitmate.fit_mate_server.domain.mate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MateResponse {
    private Long id;
    private String name;
    private String coverImageUrl;
    private Long ownerId;
    private long memberCount;
    private int maxMembers;
    private MateMemberRole myRole;

    private MateGoalMetric goalMetric;
    private MateGoalAggregation goalAggregation;
    private MateGoalPeriod goalPeriod;
    private Double goalTargetValue;

    public static MateResponse from(Mate mate, long memberCount, int maxMembers, MateMemberRole myRole) {
        return MateResponse.builder()
                .id(mate.getId())
                .name(mate.getName())
                .coverImageUrl(mate.getCoverImageUrl())
                .ownerId(mate.getOwner().getId())
                .memberCount(memberCount)
                .maxMembers(maxMembers)
                .myRole(myRole)
                .goalMetric(mate.getGoalMetric())
                .goalAggregation(mate.getGoalAggregation())
                .goalPeriod(mate.getGoalPeriod())
                .goalTargetValue(mate.getGoalTargetValue())
                .build();
    }

}
