package com.fitmate.fit_mate_server.domain.goal;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class GoalSaveRequest {


    @NotNull
    @Valid
    private List<GoalRequest> goals; // 이번 화면에서 체크된 지표 전체
}
