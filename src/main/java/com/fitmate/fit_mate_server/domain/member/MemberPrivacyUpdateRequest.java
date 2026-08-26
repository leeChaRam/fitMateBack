package com.fitmate.fit_mate_server.domain.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberPrivacyUpdateRequest {

    private PrivacyOption weightPrivacy;

    private PrivacyOption musclePrivacy;

    private PrivacyOption fatPrivacy;
}
