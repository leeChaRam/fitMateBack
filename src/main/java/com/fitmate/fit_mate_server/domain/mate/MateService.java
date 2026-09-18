package com.fitmate.fit_mate_server.domain.mate;

import java.util.UUID;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitmate.fit_mate_server.domain.member.Member;
import com.fitmate.fit_mate_server.domain.member.MemberRepository;
import com.fitmate.fit_mate_server.global.upload.ImageUploadService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MateService {

    public static final int MAX_MEMBERS = 10;

    private final MateRepository mateRepository;
    private final MateMemberRepository mateMemberRepository;
    private final MemberRepository memberRepository;
    private final ImageUploadService imageUploadService;

    // 서클 생성 만든 사람이 자동으로 방장이 되어 첫 멤버로 등록됨
    public MateResponse createMate(Long memberId, MateRequest request) {
        Member owner = memberRepository.findById(memberId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        
        validateGoal(request);

        Mate mate = Mate.builder()
                .name(request.getName())
                .owner(owner)
                .inviteToken(UUID.randomUUID().toString())
                .goalMetric(request.getGoalMetric())
                .goalAggregation(request.getGoalAggregation())
                .goalPeriod(request.getGoalPeriod())
                .goalTargetValue(request.getGoalTargetValue())
                .build();
        mateRepository.save(mate);

        MateMember ownerMembership = MateMember.builder()
                .mate(mate)
                .member(owner)
                .role(MateMemberRole.OWNER)
                .build();
        mateMemberRepository.save(ownerMembership);

        return MateResponse.from(mate, 1L, MAX_MEMBERS, MateMemberRole.OWNER);
    }

    // 목표 지표를 선택했다면 집계방식, 기간, 수치가 전부 있어야함 
    private void validateGoal(MateRequest request) {
        if (request.getGoalMetric() == null) return;

        if (request.getGoalAggregation() == null || request.getGoalPeriod() == null || request.getGoalTargetValue() == null) {
            throw new IllegalArgumentException("목표 지표를 선택했다면 집계 방식·기간 단위·목표 수치를 모두 입력해야 합니다.");
        }
        if (request.getGoalMetric() == MateGoalMetric.WORKOUT_COUNT
                && request.getGoalTargetValue() != Math.floor(request.getGoalTargetValue())) {
            throw new IllegalArgumentException("운동 횟수 목표는 정수로 입력해야 합니다.");
        }
    }

    // 초대 링크 발급/재발급: 방장만 가능, 재발급 시 기존 링크는 즉시 무효화
    public String issueInviteLink(Long memberId, Long mateId) {
        Mate mate = getMateOrThrow(mateId);

        if (!mate.isOwnedBy(memberId)) {
            throw new IllegalArgumentException("방장만 초대 링크를 발급할 수 있습니다.");
        }
        mate.regenerateInviteToken(UUID.randomUUID().toString());
        return mate.getInviteToken();
    }

    // 초대 링크 클릭 시 보여줄 미리보기
    public MatePreviewResponse previewByInviteToken(Long viewerId, String inviteToken) {
        Mate mate = mateRepository.findByInviteToken(inviteToken)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 링크입니다."));

        long memberCount = mateMemberRepository.countByMate(mate);
        boolean alreadyMember = mateMemberRepository.existsByMateAndMemberId(mate, viewerId);

        return MatePreviewResponse.from(mate, memberCount, MAX_MEMBERS, alreadyMember);
    }

    private Mate getMateOrThrow(Long mateId) {
        return mateRepository.findById(mateId)
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사람입니다."));
    }

    private MateResponse toResponse(Mate mate, MateMemberRole myRole) {
        long memberCount = mateMemberRepository.countByMate(mate);
        return MateResponse.from(mate, memberCount, MAX_MEMBERS, myRole);
    }

    // 초대 링크로 참여 : 이미 가입돼 있으면 에러 없이 현재 상태 그대로 반환 (중복 참여 방지)
    public MateResponse joinByInviteToken(Long memberId, String inviteToken) {
        Mate mate = mateRepository.findByInviteToken(inviteToken)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 링크입니다."));
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Optional<MateMember> existing = mateMemberRepository.findByMateAndMember(mate, member);
        if (existing.isPresent()) {
            return toResponse(mate, existing.get().getRole());
        }

        long memberCount = mateMemberRepository.countByMate(mate);
        if (memberCount >= MAX_MEMBERS) {
            throw new IllegalArgumentException("정원이 초과되어 참여할 수 없습니다.");
        }

        MateMember newMembership = MateMember.builder()
            .mate(mate)
            .member(member)
            .role(MateMemberRole.MEMBER)
            .build();

        mateMemberRepository.save(newMembership);

        return toResponse(mate, MateMemberRole.MEMBER);

    }


    // 서클 상세 조회 : 멤버만 조회 가능 
    public MateResponse getMate(Long viewerId, Long mateId) {
        Mate mate = getMateOrThrow(mateId);
        MateMember membership = mateMemberRepository.findByMateAndMemberId(mate, viewerId)
            .orElseThrow(() -> new IllegalArgumentException("서클 멤버만 조회할 수 있습니다."));
        return toResponse(mate, membership.getRole());
    }

    // 서클 정보/목표 수정: 방장만 가능, goalMetric에 null이면 목표 삭제
    public MateResponse updateMate(Long memberId, Long mateId, MateRequest request) {
        Mate mate = getMateOrThrow(mateId);
        if (!mate.isOwnedBy(memberId)) {
            throw new IllegalArgumentException("방장만 서클 정보를 수정할 수 있습니다.");
        }
        validateGoal(request);

        mate.updateInfo(request.getName(), null, null); // 커버 이미지는 별도 엔드포인트에서 변경
        mate.updateGoal(request.getGoalMetric(), request.getGoalAggregation(), request.getGoalPeriod(), request.getGoalTargetValue());

        return toResponse(mate, MateMemberRole.OWNER);
    }

    // 멤버 강퇴: 방장만 가능, 방장 자신은 강퇴 대상이 될 수없음
    public void kickMember(Long memberId, Long mateId, Long targetMemberId) {
        Mate mate = getMateOrThrow(mateId);
        if (!mate.isOwnedBy(memberId)) {
            throw new IllegalArgumentException("방장만 멤버를 강퇴할 수 있습니다.");
        }
        if (mate.isOwnedBy(targetMemberId)) {
            throw new IllegalArgumentException("방장은 강퇴할 수 없습니다.");
        }
        MateMember target = mateMemberRepository.findByMateAndMemberId(mate, targetMemberId)
            .orElseThrow(() -> new IllegalArgumentException("서클 멤버가 아닙니다."));
        
        mateMemberRepository.delete(target);
    }

    // 서클 삭제: 방장만 가능, 연관 데이터 즉시 삭제 (복구 불가)
    public void deleteMate(Long memberId, Long mateId) {
        Mate mate = getMateOrThrow(mateId);
        if (!mate.isOwnedBy(memberId)) {
            throw new IllegalArgumentException("방장만 서클을 삭제할 수 있습니다.");
        }

        if (mate.getCoverImagePublicId() != null) {
            imageUploadService.delete(mate.getCoverImagePublicId());
        }

        // TODO: 피드/댓글/반응 도메인 구현 후 여기서 함께 삭제 처리
        mateMemberRepository.deleteByMate(mate);
        mateRepository.delete(mate);
    }
    
}