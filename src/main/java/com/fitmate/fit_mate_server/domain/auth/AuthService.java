package com.fitmate.fit_mate_server.domain.auth;

import org.springframework.stereotype.Service;

/**
 * AuthService: 인증 관련 비즈니스 로직을 처리하는 클래스
 * 
 * [주요 역할]
 * 1. 로그인 시 아이디/비밀번호 검증
 * 2. 회원가입 시 중복 유저 확인 및 데이터 저장 요청
 * 3. 토큰 발행 또는 세션 관리 로직 처리
 */
@Service // 이 클래스가 서비스 레이어임을 스프링에게 알려줍니다.
public class AuthService {

    /**
     * 로그인 로직
     * @param userId 사용자가 입력한 아이디
     * @param password 사용자가 입력한 비밀번호
     * @return 로그인 성공 여부 (지금은 임시로 boolean 반환)
     */
    public boolean login(String userId, String password) {
        // 1. 실제로는 여기서 DB를 조회합니다. (예: userRepository.findByUserId(userId))
        // 2. 지금은 테스트를 위해 아이디가 "admin", 비번이 "1234"이면 성공이라고 가정합시다.
        
        System.out.println("서비스 로직 실행 - 아이디: " + userId);

        if ("admin".equals(userId) && "1234".equals(password)) {
            System.out.println("인증 성공: 관리자 계정입니다.");
            return true;
        }

        System.out.println("인증 실패: 아이디 또는 비밀번호가 일치하지 않습니다.");
        return false;
    }

    /**
     * 로그아웃 로직
     * @param userId 로그아웃하려는 사용자 아이디
     */
    public void logout(String userId) {
        // 실제로는 세션을 무효화하거나, 리프레시 토큰을 DB에서 삭제하는 로직이 들어갑니다.
        System.out.println("서비스 로직 실행 - 유저 로그아웃 처리: " + userId);
    }
}