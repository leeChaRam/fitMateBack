package com.fitmate.fit_mate_server.domain.home;

import java.time.LocalDate;
import java.util.List;

import com.fitmate.fit_mate_server.domain.goal.GoalResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class HomeResponse {
    private List<GoalResponse> goals;

    private LocalDate lastRecordDate;     // 마지막 기록일, 기록 이력이 없으면 null
    private Integer daysSinceLastRecord;  // 오늘 기준 며칠 지났는지, 기록 이력이 없으면 null

    // TODO: 피드(Mate) 도메인 만들면 여기에 List<FeedResponse> feed 필드만 추가
}
