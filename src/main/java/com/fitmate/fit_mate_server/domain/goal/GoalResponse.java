package com.fitmate.fit_mate_server.domain.goal;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GoalResponse {
    private Long id;
    private GoalMetric metric;
    private String unit;         // kg, % 등 프론트에서 바로 붙여쓰라고 같이 내려줌
    private Double targetValue;
    private LocalDate targetDate;

    public static GoalResponse from(Goal goal) {
        return GoalResponse.builder()
                .id(goal.getId())
                .metric(goal.getMetric())
                .unit(goal.getMetric().getUnit())
                .targetValue(goal.getTargetValue())
                .targetDate(goal.getTargetDate())
                .build();
    }
}
