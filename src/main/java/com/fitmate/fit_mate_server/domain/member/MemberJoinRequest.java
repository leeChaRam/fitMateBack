package com.fitmate.fit_mate_server.domain.member;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

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

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
}
