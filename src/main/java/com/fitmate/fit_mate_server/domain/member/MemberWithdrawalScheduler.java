package com.fitmate.fit_mate_server.domain.member;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fitmate.fit_mate_server.domain.body.BodyInfoRepository;
import com.fitmate.fit_mate_server.domain.goal.GoalRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * 탈퇴 후 30일(유예기간)이 지난 회원의 개인정보를 익명화하고,
 * 체성분 기록(BodyInfo)/목표(Goal)는 완전히 삭제합니다.
 * Member row 자체는 남기되(정산/분쟁 대응 등 무결성 목적) 개인정보만 제거합니다.
 */
@Component
@RequiredArgsConstructor
public class MemberWithdrawalScheduler {

    private static final int GRACE_PERIOD_DAYS = 30;

    private final MemberRepository memberRepository;
    private final BodyInfoRepository bodyInfoRepository;
    private final GoalRepository goalRepository;

    @Scheduled(cron = "0 0 4 * * *") // 매일 새벽 4시
    @Transactional
    public void anonymizeExpiredWithdrawals() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(GRACE_PERIOD_DAYS);
        List<Member> targets = memberRepository
                .findByStatusAndWithdrawnAtBeforeAndAnonymizedAtIsNull(MemberStatus.WITHDRAWN, cutoff);

        for (Member member : targets) {
            bodyInfoRepository.deleteByMemberId(member.getId());
            goalRepository.deleteByMemberId(member.getId());
            member.anonymize();
        }
    }
}
