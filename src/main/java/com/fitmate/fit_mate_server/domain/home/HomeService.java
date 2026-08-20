package com.fitmate.fit_mate_server.domain.home;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fitmate.fit_mate_server.domain.body.BodyInfoService;
import com.fitmate.fit_mate_server.domain.goal.GoalService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HomeService {
    private final GoalService goalService;
    private final BodyInfoService bodyInfoService;
    // TODO: 피드 도메인 생기면 private final FeedService feedService; 추가

    public HomeResponse getHome(Long memberId) {
        Optional<LocalDate> lastRecordDate = bodyInfoService.getLastRecordDate(memberId);

        return HomeResponse.builder()
                .goals(goalService.getActiveGoals(memberId))
                .lastRecordDate(lastRecordDate.orElse(null))
                .daysSinceLastRecord(
                        lastRecordDate
                                .map(date -> (int) ChronoUnit.DAYS.between(date, LocalDate.now()))
                                .orElse(null)
                )
                // TODO: .feed(feedService.getRecentFeed(memberId))
                .build();
    }
}
