package com.fitmate.fit_mate_server.domain.auth;

/**
 * LoginRequest: 로그인 요청 데이터를 담는 객체
 * 
 * [주요 역할]
 * 클라이언트(프론트엔드)에서 보내온 JSON 데이터를 
 * 자바 객체로 변환하여 담아두는 용도입니다.
 */
public class LoginRequest {

    private String userId;
    private String password;

    // 기본 생성자 (Jackson 라이브러리가 JSON을 객체로 바꿀 때 필요합니다)
    public LoginRequest() {}

    // 데이터를 꺼내기 위한 Getter
    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    // 데이터를 넣기 위한 Setter (필요한 경우)
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}