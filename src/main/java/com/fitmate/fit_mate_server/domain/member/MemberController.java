package com.fitmate.fit_mate_server.domain.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

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

    // 프로필 사진 업로드/교체
    @PostMapping("/me/profile-image")
    public MemberResponse updateProfileImage(@AuthenticationPrincipal Long memberId,
                                              @RequestParam("image") MultipartFile image) {
        return memberService.updateProfileImage(memberId, image);
    }

}