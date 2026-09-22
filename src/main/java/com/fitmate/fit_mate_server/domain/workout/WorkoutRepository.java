package com.fitmate.fit_mate_server.domain.workout;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitmate.fit_mate_server.domain.member.Member;

public class WorkoutRepository extends JpaRepository<Workout, Long> {

    // 서클 피드에서 멤버들의 운동 기록을 최신순으로 모아올 때 사용
    List<Workout> findByMemberInOrderByCreatedAtDesc(List<Member> members);
}
