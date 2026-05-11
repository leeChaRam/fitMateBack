package com.fitmate.fit_mate_server.domain.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberJoinRequest {
    private String email;
    private String password;
    private String name;
}
