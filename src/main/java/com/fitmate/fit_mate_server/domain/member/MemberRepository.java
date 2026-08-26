package com.fitmate.fit_mate_server.domain.member;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>{
    Optional<Member> findByEmail(String email);

    // 유예기간이 지났지만 아직 익명화되지 않은 탈퇴 회원 조회 (배치용)
    List<Member> findByStatusAndWithdrawnAtBeforeAndAnonymizedAtIsNull(MemberStatus status, LocalDateTime cutoff);
}
