package com.fitmate.fit_mate_server.domain.mate;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mates")
public class MateController {

    private final MateService mateService;

    @PostMapping
    public MateResponse createMate(@AuthenticationPrincipal Long memberId, @Valid @RequestBody MateRequest request) {
        return mateService.createMate(memberId, request);
    }
    
    @GetMapping("/{id}")
    public MateResponse getMate(AuthenticationPrincipal Long memberId, @PathVariable Long id) {
        return mateService.getMate(memberId, id);
    }

    @PatchMapping("/{id}")
    public MateResponse updateMate(@AuthenticationPrincipal Long memberId, @PathVariable Long id,
                                    @Valid @RequestBody MateRequest request) {
        return mateService.updateMate(memberId, id, request);
    }

    @PostMapping("/{id}/invite-link")
    public MateInviteLinkResponse issueInviteLink(@AuthenticationPrincipal Long memberId, @PathVariable Long id) {
        return new MateInviteLinkResponse(mateService.issueInviteLink(memberId, id));
    }

    @GetMapping("/invite/{token}")
    public MatePreviewResponse previewByInviteToken(@AuthenticationPrincipal Long memberId, @PathVariable String token) {
        return mateService.previewByInviteToken(memberId, token);
    }

    @PostMapping("/join")
    public MateResponse join(@AuthenticationPrincipal Long memberId, @Valid @RequestBody MateJoinRequest request) {
        return mateService.joinByInviteToken(memberId, request.getInviteToken());
    }

    @GetMapping("/{id}/members")
    public List<MateMemberResponse> getMembers(@AuthenticationPrincipal Long memberId, @PathVariable Long id) {
        return mateService.getMembers(memberId, id);
    }

    @DeleteMapping("/{id}/members/{targetMemberId}")
    public ResponseEntity<String> kickMember(@AuthenticationPrincipal Long memberId, @PathVariable Long id,
                                              @PathVariable Long targetMemberId) {
        mateService.kickMember(memberId, id, targetMemberId);
        return ResponseEntity.ok("멤버가 강퇴되었습니다.");
    }
    
}
