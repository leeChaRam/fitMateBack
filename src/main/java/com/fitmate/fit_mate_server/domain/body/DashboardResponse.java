package com.fitmate.fit_mate_server.domain.body;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    //최근 측정 요약 데이터
    private Double latestWeight;
    private Double latestMuscleMass;
    private Double latestFatMass;
    private String measureDate;

    // 변동폭 (이전 측정 대비 증감치)
    private String weightDelta;
    private String muscleDelta;
    private String fatDelta;

    // 백엔드에서 미리 계산해서 넘겨줄 지표
    private Double bmi;
    private Integer bmr; // 기초대사량

    // 하단 리스트 및 차트용 전체 히스토리
    private List<BodyInfoHistoryDto> historyList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BodyInfoHistoryDto {
        private Long id;
        private String measureDate;
        private Double weight;
        private Double muscleMass;
        private Double fatMass;

        // 💡 각 과거 기록 시점에서의 직전 대비 증감치 필드 추가
        private String weightDelta;
        private String muscleDelta;
        private String fatDelta;
    }
}