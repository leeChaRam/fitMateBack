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

    // 목표설정 화면에서 "체크된 지표 전체"를 한 번에 넘겨 받아 통째로 동기화 
    public void saveGoals(Long memberId, List<GoalRequest> requests) {
        Memeber member = memberRepository.findById(memberId)
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

    }

    // 홈 화면: active=true만 조회하면 끝, 별도 "최신" 로직 불필요
    public List<Goal> getActiveGoals(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return goalRepository.findByMemberAndActiveTrue(member);
    }

    
    
}
