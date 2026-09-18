package com.fitmate.fit_mate_server.domain.mate;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitmate.fit_mate_server.domain.member.Member;

public interface MateMemberRepository extends JpaRepository<MateMember, Long> {
    Optional<MateMember> findByMateAndMember(Mate mate, Member member);
    boolean existsByMateAndMember(Mate mate, Member member);
    List<MateMember> findByMate(Mate mate);
    List<MateMember> findByMember(Member member); // 이 사람이 속한 Mate 목록 (전환 드롭다운, 피드 조회 등에 재사용)
    long countByMate(Mate mate); //정원(10명) 체크
    boolean existsByMateAndMemberId(Mate mate, Long memberId); // 이미 가입되어 있는 건지 확인
    void deleteByMateAndMember(Mate mate, Member member); // 강퇴
    Optional<MateMember> findByMateAndMemberId(Mate mate, Long memberId); // 역할까지 필요할 때
}
