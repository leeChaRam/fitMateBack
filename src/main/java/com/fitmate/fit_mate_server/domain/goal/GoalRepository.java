package com.fitmate.fit_mate_server.domain.goal;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitmate.fit_mate_server.domain.member.Member;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByMemberAndActiveTrue(Member member);
    Optional<Goal> findByMemberAndMetric(Member member, GoalMetric metric);
}
