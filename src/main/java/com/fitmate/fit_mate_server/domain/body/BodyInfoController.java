package com.fitmate.fit_mate_server.domain.body;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fitmate.fit_mate_server.domain.member.Member;
import com.fitmate.fit_mate_server.domain.member.MemberRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/body-info")
@RequiredArgsConstructor
public class BodyInfoController {
    private final BodyInfoService bodyInfoService;
    private final MemberRepository memberRepository; 

    @PostMapping
    public ResponseEntity<String> saveBodyInfo(@Valid @RequestBody BodyInfoRequest request) {
        // 1. JSON에 실려온 memberId로 DB에서 실제 유저 정보를 조회
        Member currentMember = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        bodyInfoService.saveBodyInfo(request);
        System.out.println("break point 확인해보자 ");
        return ResponseEntity.status(HttpStatus.CREATED).body("체성분 기록 저장 완료");
    }

    @GetMapping("/recent")
    @CrossOrigin(origins = "*") // 👈 모든 도메인에서의 웹 호출을 허용하겠다는 설정!
    public List<BodyInfo> getRecentList(@RequestParam("memberId") Long memberId) {
    return bodyInfoService.getRecentBodyInfos(memberId);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboardData(@RequestParam Long memberId) {
        DashboardResponse response = bodyInfoService.getDashboardData(memberId);
        return ResponseEntity.ok(response);
    }
    
}
