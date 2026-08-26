package com.fitmate.fit_mate_server.domain.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberWithdrawRequest {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
