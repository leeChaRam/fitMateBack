package com.fitmate.fit_mate_server.domain.home;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class HomeController {
    private final HomeService homeService;

    @GetMapping
    public ResponseEntity<HomeResponse> getHome(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(homeService.getHome(memberId));
    }
}
