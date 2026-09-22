package com.fitmate.fit_mate_server.domain.workout;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkoutRequest {

    @NotNull(message = "운동 종류는 필수입니다.")
    private WorkoutType workoutType;

    private String customType; // workoutType == OTHER 일 때만 필수(Service에서 검증)

    @NotNull(message = "운동 시간은 필수입니다.")
    @Min(value = 1, messgae ="운동 시간은 1분 이상이어야 합니다.")
    @Max(value = 600, message = "운동 시간은 600분을 초과할 수 없습니다.")
    private Integer durationMinutes;

    private WorkoutIntensity intensity; // 선택

    private String memo; //선택 

    @NotNull(message = "기록 날짜는 필수입니다.")
    @PastOrPresent(message = "기록 날짜는 오늘 또는 과거만 선택할  수 있습니다.")
    private LocalDate recordDate;
    
}
