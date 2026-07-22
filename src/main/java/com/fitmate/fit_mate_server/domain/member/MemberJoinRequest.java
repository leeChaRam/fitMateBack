package com.fitmate.fit_mate_server.domain.member;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberJoinRequest {
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    //정책: 영문, 숫자, 특수문자를 모두 포함한 8~20자

    @NotBlank(message="비밀번호는 필수입니다.")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$",
        message = "비밀번호는 영문, 숫자, 특수문자를 모두 포함한 8~20자여야 합니다."
    )
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String checkPassword;

    @NotBlank(message="이름은 필수입니다.")
    private String name;
    
    @NotBlank(message="생년월일은은 필수입니다.")
    @PastOrPresent(message="생년월일은 현재 날짜보다 미래일 수 없습니다.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @NotNull(message = "키는 필수입니다.")
    @Positive(message = "키는 0보다 커야 합니다.")
    private Double height;

    
}
