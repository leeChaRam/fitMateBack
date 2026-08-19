package com.fitmate.fit_mate_server.domain.goal;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/goal")
public class GoalController {
    

    private final GoalService goalService;


    @PostMapping
    public ResponseEntity<List<GoalResponse>> syncGoals(@AuthenticationPrincipal Long memberId, @Valid @RequestBody GoalSaveRequest request) {
        return ResponseEntity.ok(goalService.syncGoals(memberId, request.getGoals()));
    }

    @GetMapping
    public ResponseEntity<List<GoalResponse>> getActiveGoals(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(goalService.getActiveGoals(memberId));
    }
}
