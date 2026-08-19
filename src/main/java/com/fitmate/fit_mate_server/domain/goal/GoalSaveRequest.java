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

    @NotNull(message = "회원 ID는 필수입니다.")
    private Long memberId;

    @NotEmpty(message = "목표를 1개 이상 선택해주세요.")
    @Valid
    private List<GoalRequest> goals; // 이번 화면에서 체크된 지표 전체
}
