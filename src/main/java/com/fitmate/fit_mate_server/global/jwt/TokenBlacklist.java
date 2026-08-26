package com.fitmate.fit_mate_server.global.jwt;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 로그아웃된(무효화된) 토큰을 만료 시각까지만 메모리에 보관합니다.
 * JWT는 stateless라 서버가 별도로 막지 않으면 만료 전까지 계속 유효하므로,
 * 로그아웃 시 여기에 등록해두고 JwtAuthenticationFilter에서 함께 검사합니다.
 */
@Component
public class TokenBlacklist {

    private final Map<String, Date> blacklist = new ConcurrentHashMap<>();

    public void blacklist(String token, Date expiry) {
        blacklist.put(token, expiry);
    }

    public boolean isBlacklisted(String token) {
        return blacklist.containsKey(token);
    }

    // 만료된 토큰은 더 이상 검증을 통과할 수 없으므로 주기적으로 비워서 메모리 누수를 막음
    @Scheduled(fixedRate = 3600000)
    public void removeExpiredTokens() {
        Date now = new Date();
        blacklist.values().removeIf(expiry -> expiry.before(now));
    }
}
