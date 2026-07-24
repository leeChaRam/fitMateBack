package com.fitmate.fit_mate_server.domain.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth") // 모든 인증 관련 주소는 /api/auth로 시작
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
 
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    public String logout() {
        // 여기에 로그아웃 로직 연결
        return "로그아웃 성공! tedㅇdfdㄹㅇㄹf";
    }
}

