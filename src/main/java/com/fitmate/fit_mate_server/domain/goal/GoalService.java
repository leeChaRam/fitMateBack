package com.fitmate.fit_mate_server.domain.goal;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitmate.fit_mate_server.domain.member.Member;
import com.fitmate.fit_mate_server.domain.member.MemberRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional
public class GoalService {
    private final GoalRepository goalRepository;
    private final MemberRepository memberRepository;

    // 목표설정 화면에서 "체크된 지표 전체"를 받아 회원의 목표 상태를 통째로 동기화하고,
    // 동기화 후 활성 목표 전체를 리턴한다 (생성/갱신/비활성화가 한 번에 일어남)
    public List<GoalResponse> syncGoals(Long memberId, List<GoalRequest> requests) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Set<GoalMetric> requestMetrics = requests.stream()
                                         .map(GoalRequest::getMetric)
                                         .collect(Collectors.toSet());
        
        // 1. 이번에 체크 해제된(=요청에 없는) 기존 활성 목표는 비활성화
        goalRepository.findByMemberAndActiveTrue(member).stream()
                .filter(goal -> !requestMetrics.contains(goal.getMetric()))
                .forEach(Goal::deactivate);

        // 2. 요청에 포함된 지표는 upsert (있으면 갱신, 없으면 새로 생성)
        for (GoalRequest req : requests) {
            Goal goal = goalRepository.findByMemberAndMetric(member, req.getMetric())
                    .orElseGet(() -> Goal.builder()
                            .member(member)
                            .metric(req.getMetric())
                            .build());
            goal.update(req.getTargetValue(), req.getTargetDate()); // 내부에서 active=true 처리
            goalRepository.save(goal);
        }

        // 3. 최종 활성 목표 전체를 다시 조회해서 리턴 (프론트가 바로 화면에 반영할 수 있게)
        return toResponses(goalRepository.findByMemberAndActiveTrue(member));

    }

    // 홈 화면: active=true만 조회하면 끝, 별도 "최신" 로직 불필요
    public List<GoalResponse> getActiveGoals(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return toResponses(goalRepository.findByMemberAndActiveTrue(member));
    }

    private List<GoalResponse> toResponses(List<Goal> goals) {
        return goals.stream()
                .map(GoalResponse::from)
                .collect(Collectors.toList());
    }

    
    
}
