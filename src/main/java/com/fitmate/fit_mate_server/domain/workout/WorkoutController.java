package com.fitmate.fit_mate_server.domain.workout;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    @PostMapping
    public WorkoutResponse createWorkout(@AuthenticationPrincipal Long memberId,
                                @Valid @RequestBody WorkoutRequest request) {
        return workoutService.createWorkout(memberId, request);
    }
    
    @PatchMapping("/{id}")
    public WorkoutResponse updateWorkout(@AuthenticationPrincipal Long memberId, @PathVariable Long id,
                                          @Valid @RequestBody WorkoutRequest request) {
        return workoutService.updateWorkout(memberId, id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteWorkout(@AuthenticationPrincipal Long memberId, @PathVariable Long id) {
        workoutService.deleteWorkout(memberId, id);
    }

    
}
