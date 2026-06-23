package com.fitmate.fit_mate_server.domain.body;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitmate.fit_mate_server.domain.member.Member;
import com.fitmate.fit_mate_server.domain.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BodyInfoService {
    private final BodyInfoRepository bodyInfoRepository;
    private final MemberRepository memberRepository;

    public Long saveBodyInfo(BodyInfoRequest request) {
        // 1. 회원 존재 여부 확인
        Member member = memberRepository.findById(request.getMemberId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2. 빌더 패턴을 이용하여 기존 테이블 형식에 맞게 세팅
        BodyInfo bodyInfo = BodyInfo.builder()
                .member(member)
                .measureDate(request.getMeasureDate())
                .weight(request.getWeight())
                .muscleMass(request.getMuscleMass())
                .fatMass(request.getFatMass())
                .memo(request.getMemo())
                // 기본값 분기 처리 (선택 안했을 경우 나만보기 혹은 기본값 세팅)
                .weightPrivacy(request.getWeightPrivacy() != null ? request.getWeightPrivacy() : PrivacyOption.DELTA_ONLY)
                .musclePrivacy(request.getMusclePrivacy() != null ? request.getMusclePrivacy() : PrivacyOption.PRIVATE)
                .fatPrivacy(request.getFatPrivacy() != null ? request.getFatPrivacy() : PrivacyOption.PRIVATE)
                .build();
                        // 2. BodyInfo 엔티티 생성 및 회원 연결
        // BodyInfo bodyInfo = BodyInfo.builder()
        //         .member(member)
        //         .measureDate(request.getMeasureDate())
        //         .weight(request.getWeight())
        //         .muscleMass(request.getMuscleMass())
        //         .fatMass(request.getFatMass())
        //         .memo(request.getMemo())
        //         .createdAt(LocalDateTime.now())
        //         .build();

        return bodyInfoRepository.save(bodyInfo).getId();
    }

    public List<BodyInfo> getRecentBodyInfos(Long memberId) {
    return bodyInfoRepository.findTop10ByMemberIdOrderByMeasureDateDesc(memberId);
    }
    
}
