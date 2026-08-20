package com.fitmate.fit_mate_server.domain.body;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/body-info")
@RequiredArgsConstructor
public class BodyInfoController {
    private final BodyInfoService bodyInfoService;

    @PostMapping
    public ResponseEntity<String> saveBodyInfo(@AuthenticationPrincipal Long memberId, @Valid @RequestBody BodyInfoRequest request) {
        bodyInfoService.saveBodyInfo(memberId, request);
        System.out.println("break point 확인해보자 ");
        return ResponseEntity.status(HttpStatus.CREATED).body("체성분 기록 저장 완료");
    }

    @GetMapping("/recent")
    public List<BodyInfo> getRecentList(@AuthenticationPrincipal Long memberId) {
    return bodyInfoService.getRecentBodyInfos(memberId);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboardData(@AuthenticationPrincipal Long memberId) {
        DashboardResponse response = bodyInfoService.getDashboardData(memberId);
        return ResponseEntity.ok(response);
    }
    
}
