package com.fitmate.fit_mate_server.domain.body;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BodyInfoRepository extends JpaRepository<BodyInfo, Long> {

    // 특정 회원의 체성분 기록을 측정일(measureDate) 기준 최신순으로 10개만 가져옴
    List<BodyInfo> findTop10ByMemberIdOrderByMeasureDateDesc(Long memberId);

    // 홈 화면 "마지막 기록일" 계산용 - 최신 기록 1건만 조회 (기록이 없으면 empty)
    Optional<BodyInfo> findTopByMemberIdOrderByMeasureDateDesc(Long memberId);
}
