package com.fitmate.fit_mate_server.domain.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberResponse {
    private Long id;
    private String email;
    private String name;
    private String introduction;
    private String profileImageUrl;
    private Double height;
    private PrivacyOption weightPrivacy;
    private PrivacyOption musclePrivacy;
    private PrivacyOption fatPrivacy;
}