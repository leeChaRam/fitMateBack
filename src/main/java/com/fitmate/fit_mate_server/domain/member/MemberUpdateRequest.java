package com.fitmate.fit_mate_server.domain.member;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberUpdateRequest {

    @Size(max = 20, message = "이름은 20자 이내로 입력해주세요.")
    private String name;

    @Size(max = 150, message = "자기소개는 150자 이내로 입력해주세요.")
    private String introduction;
}
