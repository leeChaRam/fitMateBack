package com.fitmate.fit_mate_server.domain.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.fitmate.fit_mate_server.domain.auth.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService;

    @PostMapping("/join")
    public ResponseEntity<String> join(@Valid @RequestBody MemberJoinRequest request){
        try{
            memberService.join(request);
            return ResponseEntity.ok("회원가입 성공!");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 토큰으로 인증된 회원의 정보 조회
    @GetMapping("/me")
    public MemberResponse me(@AuthenticationPrincipal Long memberId) {
        return memberService.getMe(memberId);
    }

    // 이름 / 자기소개(150자 이내) 수정
    @PutMapping("/me")
    public MemberResponse updateProfile(@AuthenticationPrincipal Long memberId,
                                         @Valid @RequestBody MemberUpdateRequest request) {
        return memberService.updateProfile(memberId, request);
    }

    // 체중/근육량/체지방률 공개 범위 수정 (일부 항목만 보내도 됨)
    @PutMapping("/me/privacy")
    public MemberResponse updatePrivacySettings(@AuthenticationPrincipal Long memberId,
                                                  @RequestBody MemberPrivacyUpdateRequest request) {
        return memberService.updatePrivacySettings(memberId, request);
    }

    // 프로필 사진 업로드/교체
    @PostMapping("/me/profile-image")
    public MemberResponse updateProfileImage(@AuthenticationPrincipal Long memberId,
                                              @RequestParam("image") MultipartFile image) {
        return memberService.updateProfileImage(memberId, image);
    }

    // 비밀번호 변경 (현재 비밀번호, 새 비밀번호, 새 비밀번호 확인)
    @PutMapping("/me/password")
    public ResponseEntity<String> updatePassword(@AuthenticationPrincipal Long memberId,
                                                  @Valid @RequestBody MemberPasswordUpdateRequest request) {
        memberService.changePassword(memberId, request);
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }

    // 회원 탈퇴: 비밀번호 확인 후 DB에 탈퇴 처리, 현재 토큰은 즉시 무효화
    @DeleteMapping("/me")
    public ResponseEntity<String> withdraw(@AuthenticationPrincipal Long memberId,
                                            @Valid @RequestBody MemberWithdrawRequest request,
                                            @RequestHeader("Authorization") String authorizationHeader) {
        memberService.withdraw(memberId, request);

        String token = authorizationHeader.substring("Bearer ".length());
        authService.logout(token);

        return ResponseEntity.ok("탈퇴 처리되었습니다.");
    }

}