package com.fitmate.fit_mate_server.domain.workout;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class WorkoutResponse {
    private Long id;
    private WorkoutType workoutType;
    private String customType;
    private Integer durationMinutes;
    private WorkoutIntensity intensity;
    private String memo;
    private LocalDate recordDate;
    private LocalDateTime createdAt;

    public static WorkoutResponse from(Workout workout) {
        return WorkoutResponse.builder()
                .id(workout.getId())
                .workoutType(workout.getWorkoutType())
                .customType(workout.getCustomType())
                .durationMinutes(workout.getDurationMinutes())
                .intensity(workout.getIntensity())
                .memo(workout.getMemo())
                .recordDate(workout.getRecordDate())
                .createdAt(workout.getCreatedAt())
                .build();
    }
}
