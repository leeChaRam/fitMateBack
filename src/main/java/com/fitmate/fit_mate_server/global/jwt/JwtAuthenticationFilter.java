package com.fitmate.fit_mate_server.global.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * "Authorization: Bearer {token}" 헤더를 확인해서
 * 토큰이 유효하면 SecurityContext에 로그인 상태를 등록합니다.
 * 이후 컨트롤러에서 @AuthenticationPrincipal Long memberId 로 꺼내 쓸 수 있습니다.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader(HEADER);

        if (header != null && header.startsWith(PREFIX)) {
            String token = header.substring(PREFIX.length());

            if (jwtTokenProvider.validateToken(token)) {
                Long memberId = jwtTokenProvider.getMemberId(token);

                // principal 자리에 memberId(Long)를 그대로 담아둡니다.
                // 컨트롤러에서 @AuthenticationPrincipal Long memberId로 꺼내 쓸 수 있어요.
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(memberId, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            // 토큰이 유효하지 않으면 그냥 인증 안 된 상태로 통과시킵니다.
            // (permitAll이 아닌 API라면 이후 SecurityConfig 설정에 의해 401로 막힙니다)
        }

        filterChain.doFilter(request, response);
    }
}