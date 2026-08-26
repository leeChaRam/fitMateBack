package com.fitmate.fit_mate_server.domain.goal;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitmate.fit_mate_server.domain.member.Member;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByMemberAndActiveTrue(Member member);
    Optional<Goal> findByMemberAndMetric(Member member, GoalMetric metric);

    // 회원 탈퇴 유예기간 만료 후 배치에서 목표를 완전 파기할 때 사용
    void deleteByMemberId(Long memberId);
}
