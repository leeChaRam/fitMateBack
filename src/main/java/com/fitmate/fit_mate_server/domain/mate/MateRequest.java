package com.fitmate.fit_mate_server.domain.mate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MateRequest {

    @NotBlank(message = "서클 이름은 필수입니다.")
    @Size(min = 1, max = 20, message = "서클 이름은 1자 이상 20자 이내로 입력해주세요.")
    private String name;

    private MateGoalMetric goalMetric;       // null이면 목표 미설정
    private MateGoalAggregation goalAggregation;
    private MateGoalPeriod goalPeriod;

    @Positive(message = "목포 수치는 0보다 커야 합니다.")
    private Double goalTargetValue;
    
}
