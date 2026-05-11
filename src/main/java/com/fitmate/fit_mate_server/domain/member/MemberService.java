package com.fitmate.fit_mate_server.domain.member;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    
    private final MemberRepository memberRepository;

    public Long join(MemberJoinRequest request){
        // 1. 중복 이메일 검증
        memberRepository.findByEmail(request.getEmail()).ifPresent(m ->{
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        });

        // 2. 저장
        Member newMember = Member.builder()
            .email(request.getEmail())
            .password(request.getPassword())
            .name(request.getName())
            .build();

        // 3. 저장 및 ID 반환 
        Member savedMember = memberRepository.save(newMember);
        return savedMember.getId();
    }
}
