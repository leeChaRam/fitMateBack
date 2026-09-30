package com.fitmate.fit_mate_server.domain.mate;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MateJoinRequest {
    @NotBlank(message = "초대 토큰은 필수입니다.")
    private String inviteToken;
}
