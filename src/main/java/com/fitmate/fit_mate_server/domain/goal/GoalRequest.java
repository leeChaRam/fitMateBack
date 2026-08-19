package com.fitmate.fit_mate_server.domain.goal;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class GoalRequest {

    @NotNull(message = "목표 지표는 필수입니다.")
    private GoalMetric metric; // WEIGHT, MUSCLE_MASS, BODY_FAT_MASS, BODY_FAT_PERCENT

    @NotNull(message = "목표 수치는 필수입니다.")
    @Positive(message = "목표 수치는 0보다 커야 합니다.")
    private Double targetValue;

    private LocalDate targetDate; // 목표 기한 (선택)
}
