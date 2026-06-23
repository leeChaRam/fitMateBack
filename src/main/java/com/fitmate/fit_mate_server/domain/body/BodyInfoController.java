package com.fitmate.fit_mate_server.domain.body;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/body-info")
@RequiredArgsConstructor
public class BodyInfoController {
    private final BodyInfoService bodyInfoService;

    @PostMapping
    public ResponseEntity<String> saveBodyInfo(@Valid @RequestBody BodyInfoRequest request) {
        Long id = bodyInfoService.saveBodyInfo(request);
        System.out.println("break point 확인해보자 ");
        return ResponseEntity.status(HttpStatus.CREATED).body("체성분 기록 저장 완료");
    }

    @GetMapping("/list/recent")
    public List<BodyInfo> getRecentList(@RequestParam(name = "memberId") Long memberId) {
    return bodyInfoService.getRecentBodyInfos(memberId);
    }

    // @GetMapping("/dashborad")
    // public List getDashborad
    
}
