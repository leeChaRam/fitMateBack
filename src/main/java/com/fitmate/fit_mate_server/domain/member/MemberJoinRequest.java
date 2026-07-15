package com.fitmate.fit_mate_server.domain.member;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberJoinRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private String name;
    
    @NotNull(message = "키는 필수입니다.")
    @Positive(message = "키는 0보다 커야 합니다.")
    private Double height;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
}
