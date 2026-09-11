package com.fitmate.fit_mate_server.global.jwt;

/**
 * "Authorization: Bearer {token}" 헤더에서 토큰 부분만 안전하게 꺼냅니다.
 * 접두사가 없으면 IllegalArgumentException을 던져 GlobalExceptionHandler가 400으로 응답하게 합니다.
 */
public final class BearerTokenExtractor {

    private static final String PREFIX = "Bearer ";

    private BearerTokenExtractor() {
    }

    public static String extract(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(PREFIX)) {
            throw new IllegalArgumentException("인증 헤더 형식이 올바르지 않습니다.");
        }
        return authorizationHeader.substring(PREFIX.length());
    }
}
