package com.fitmate.fit_mate_server.domain.body;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BodyInfoRequest {

    private Long memberId;      // 누구의 기록인지 (중요!)
    @NotNull(message = "측정일은 필수입니다.")
    private LocalDate measureDate; 
    @NotNull(message = "체중은 필수입니다.")
    private Double weight;

    private Double muscleMass;
    private Double fatMass;
    private String memo;

    // Flutter에서 문자열("PRIVATE" 등)로 던져주면 Sprign이 자동으로 Enum으로 파싱해줌
    private PrivacyOption weightPrivacy;
    private PrivacyOption musclePrivacy;
    private PrivacyOption fatPrivacy;
}
