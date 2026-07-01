package com.fitmate.fit_mate_server.domain.body;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitmate.fit_mate_server.domain.member.Member;
import com.fitmate.fit_mate_server.domain.member.MemberRepository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BodyInfoService {
    private final BodyInfoRepository bodyInfoRepository;
    private final MemberRepository memberRepository;

    // 💡 문자열과 상태 코드를 한 번에 묶어서 리턴할 가벼운 그릇
    @Getter
    @AllArgsConstructor
    private static class DeltaResult {
        private final String delta;
        private final Integer status;
    }

    public Long saveBodyInfo(BodyInfoRequest request) {
        // 1. 회원 존재 여부 확인
        Member member = memberRepository.findById(request.getMemberId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2. 빌더 패턴을 이용하여 기존 테이블 형식에 맞게 세팅
        BodyInfo bodyInfo = BodyInfo.builder()
                .member(member)
                .measureDate(request.getMeasureDate())
                .weight(request.getWeight())
                .height(request.getHeight())
                .muscleMass(request.getMuscleMass())
                .fatMass(request.getFatMass())
                .memo(request.getMemo())
                .build();

        return bodyInfoRepository.save(bodyInfo).getId();
    }

    public List<BodyInfo> getRecentBodyInfos(Long memberId) {
    return bodyInfoRepository.findTop10ByMemberIdOrderByMeasureDateDesc(memberId);
    }

    public DashboardResponse getDashboardData(Long memberId){
        // 1. 해당 유저의 모든 체성분 기록을 최신순으로 조회
        List<BodyInfo> history = bodyInfoRepository.findTop10ByMemberIdOrderByMeasureDateDesc(memberId);

        if (history.isEmpty()) {
            return new DashboardResponse(); // 데이터가 없으면 빈 객체 반환
        }

        // 2. 가장 최신 데이터와 그 이전 데이터 추출
        BodyInfo latest = history.get(0);
        BodyInfo previous = history.size() > 1 ? history.get(1) : null;

        // 3. 증감치(Delta) 계산 (소수점 첫째자리까지 반올림)
        DeltaResult weightResult = calculateDelta(latest.getWeight(), (previous != null) ? previous.getWeight() : null);
        DeltaResult muscleResult = calculateDelta(latest.getMuscleMass(), (previous != null) ? previous.getMuscleMass() : null);
        DeltaResult fatResult = calculateDelta(latest.getFatMass(), (previous != null) ? previous.getFatMass() : null);

        // 4. BMI 및 기초대사량(BMR) 계산 (수식 예시 - 필요시 유저 키/성별 데이터 연동)
        // 임시로 키 175cm 가정 하에 간단한 BMI 계산 예시 ($BMI = kg / m^2$)
        double heightInMeters = latest.getHeight() / 100.0;
        Double bmi = Math.round((latest.getWeight() / (heightInMeters * heightInMeters)) * 10.0) / 10.0;

        // 하드코딩 데이터를 대체할 기초대사량 기본 연산 (예시: 해리스-베네딕트 공식 변형 간이 정수값)
        Integer bmr = (int) (66.5 + (13.75 * latest.getWeight()) + (5.003 * latest.getHeight()) - (6.755 * 25));

        // 5. ⭐ 히스토리 리스트를 돌면서 각 항목의 직전 대비 증감치 계산
        List<DashboardResponse.BodyInfoHistoryDto> historyDtos = new ArrayList<>();
        
        for (int i = 0; i < history.size(); i++) {
            BodyInfo currentItem = history.get(i);
            // 현재 항목(i)의 직전 과거 데이터는 리스트의 다음 인덱스(i + 1)에 있습니다.
            BodyInfo olderItem = (i + 1 < history.size()) ? history.get(i + 1) : null;

            // 각 항목 기준의 증감치 계산
            DeltaResult hWeight = calculateDelta(currentItem.getWeight(), (olderItem != null) ? olderItem.getWeight() : null);
            DeltaResult hMuscle = calculateDelta(currentItem.getMuscleMass(), (olderItem != null) ? olderItem.getMuscleMass() : null);
            DeltaResult hFat = calculateDelta(currentItem.getFatMass(), (olderItem != null) ? olderItem.getFatMass() : null);

            DashboardResponse.BodyInfoHistoryDto dto = DashboardResponse.BodyInfoHistoryDto.builder()
                    .id(currentItem.getId())
                    .measureDate(currentItem.getMeasureDate().toString())
                    .weight(currentItem.getWeight())
                    .muscleMass(currentItem.getMuscleMass())
                    .fatMass(currentItem.getFatMass())
                    .weightDelta(hWeight.getDelta())
                    .weightStatus(hWeight.getStatus())
                    .muscleDelta(hMuscle.getDelta())
                    .muscleStatus(hMuscle.getStatus())
                    .fatDelta(hFat.getDelta())
                    .fatStatus(hFat.getStatus())
                    .build();
            
            historyDtos.add(dto);
        }

        // 6. 통합 DTO 조립하여 리턴
        return DashboardResponse.builder()
                .latestWeight(latest.getWeight())
                .latestMuscleMass(latest.getMuscleMass())
                .latestFatMass(latest.getFatMass())
                .measureDate(latest.getMeasureDate().toString())
                .weightDelta(weightResult.getDelta())
                .weightStatus(weightResult.getStatus())
                .muscleDelta(muscleResult.getDelta())
                .muscleStatus(muscleResult.getStatus())
                .fatDelta(fatResult.getDelta())
                .fatStatus(fatResult.getStatus())
                .bmi(bmi)
                .bmr(bmr)
                .historyList(historyDtos)
                .build();
    }
    /**
     * 최신값과 직전값을 비교하여 변동폭 문자열(▲ 0.3 / ▼ 0.8)을 만들어주는 헬퍼 메서드
     */
    private DeltaResult calculateDelta(Double current, Double previous) {
        // 비교 대상이 없으면 변동 없음(1) 처리
        if (current == null || previous == null) {
            return new DeltaResult("-", 1); 
        }
        
        double diff = current - previous;
        double roundedDiff = Math.round(Math.abs(diff) * 10.0) / 10.0;

        // 0.05 미만의 미미한 차이는 변동 없음(1) 처리
        if (Math.abs(diff) < 0.05) {
            return new DeltaResult("0.0", 1);
        }

        if (diff > 0) {
            return new DeltaResult("▲ " + roundedDiff, 2); // 업 (2)
        } else {
            return new DeltaResult("▼ " + roundedDiff, 0); // 다운 (0)
        }
    }
    
}
