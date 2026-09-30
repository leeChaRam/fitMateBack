package com.fitmate.fit_mate_server.domain.mate.feed;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitmate.fit_mate_server.domain.body.BodyInfo;
import com.fitmate.fit_mate_server.domain.body.BodyInfoRepository;
import com.fitmate.fit_mate_server.domain.mate.Mate;
import com.fitmate.fit_mate_server.domain.mate.MateMemberRepository;
import com.fitmate.fit_mate_server.domain.mate.MateRepository;
import com.fitmate.fit_mate_server.domain.member.Member;
import com.fitmate.fit_mate_server.domain.member.PrivacyOption;
import com.fitmate.fit_mate_server.domain.workout.WorkoutRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MateFeedService {

    private final MateRepository mateRepository;
    private final MateMemberRepository mateMemberRepository;
    private final BodyInfoRepository bodyInfoRepository;
    private final WorkoutRepository workoutRepository;
    private final MateReactionRepository mateReactionRepository;
    private final MateCommentRepository mateCommentRepository;

    // 공개범위 판단 결과를 담는 작은 그릇
    private record MetricDisplay(Double value, String delta) {}

    // 회원의 지표 하나(체중/근육량/체지방량)에 대해, 공개범위에 따라 값/변화량을 걸러서 반환
    private MetricDisplay filterMetric(PrivacyOption privacy, Double currentValue, Double previousValue) {
        if (privacy == PrivacyOption.PRIVATE) {
            return new MetricDisplay(null, null);
        }
        String delta = calculateDeltaText(currentValue, previousValue);
        Double value = (privacy == PrivacyOption.PUBLIC) ? currentValue : null;
        return new MetricDisplay(value, delta);
    }

}
