package com.fitmate.fit_mate_server.domain.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // 모든 인증 관련 주소는 /api/auth로 시작
public class AuthController {

    @PostMapping("/login")
    public String login() {
        // 여기에 로그인 로직 연결
        // git contribute check!!!
        
        return "로그인 성공!";
    }

    @PostMapping("/logout")
    public String logout() {
        // 여기에 로그아웃 로직 연결
        return "로그아웃 성공! tedㅇdfdㄹㅇㄹf";
    }
}

