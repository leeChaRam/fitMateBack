package com.fitmate.fit_mate_server.domain.mate.feed;

import java.util.ArrayList;
import java.util.Comparator;
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
import com.fitmate.fit_mate_server.domain.workout.Workout;


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
    // 피드 글 하나를 구분하는 이름표 (체성분 5번과 운동 5번은 다른 글이므로 타입+id를 함께 사용)
    private record PostKey(FeedPostType postType, Long postId) {}

    // 회원의 지표 하나(체중/근육량/체지방량)에 대해, 공개범위에 따라 값/변화량을 걸러서 반환
    private MetricDisplay filterMetric(PrivacyOption privacy, Double currentValue, Double previousValue) {
        if (privacy == PrivacyOption.PRIVATE) {
            return new MetricDisplay(null, null);
        }
        String delta = calculateDeltaText(currentValue, previousValue);
        Double value = (privacy == PrivacyOption.PUBLIC) ? currentValue : null;
        return new MetricDisplay(value, delta);
    }

    private String calculateDeltaText(Double current, Double previous) {
        if (current == null || previous == null) return null;
        double diff = current - previous;
        double rounded = Math.round(Math.abs(diff) * 10.0) / 10.0;
        if (Math.abs(diff) < 0.05) return "0.0";
        return diff > 0 ? "▲ " + rounded : "▼ " + rounded;
    }

    // 서클 멤버들의 체성분 기록을 피드 아이템으로 변환
    private List<MateFeedItemResponse> buildBodyFeedItems(List<Member> members) {
        List<MateFeedItemResponse> items = new ArrayList<>();

        for (Member member : members) {
            List<BodyInfo> history = bodyInfoRepository.findTop10ByMemberIdOrderByMeasureDateDesc(member.getId());

            for (int i = 0; i< history.size(); i++) {
                BodyInfo current = history.get(i);
                BodyInfo previous = (i + 1 < history.size()) ? history.get(i+1) : null;

                MetricDisplay weight = filterMetric(member.getWeightPrivacy(), current.getWeight(),
                                                    previous != null ? previous.getWeight() : null);
                MetricDisplay muscle = filterMetric(member.getMusclePrivacy(), current.getMuscleMass(),
                        previous != null ? previous.getMuscleMass() : null);
                MetricDisplay fat = filterMetric(member.getFatPrivacy(), current.getFatMass(),
                        previous != null ? previous.getFatMass() : null);
            
                items.add(MateFeedItemResponse.builder()
                        .postType(FeedPostType.BODY_INFO)
                        .postId(current.getId())
                        .authorId(member.getId())
                        .authorName(member.getName())
                        .authorProfileImageUrl(member.getProfileImageUrl())
                        .createdAt(current.getCreatedAt())
                        .measureDate(current.getMeasureDate())
                        .weightValue(weight.value())
                        .weightDelta(weight.delta())
                        .muscleValue(muscle.value())
                        .muscleDelta(muscle.delta())
                        .fatValue(fat.value())
                        .fatDelta(fat.delta())
                        .build());
            }
        }
        return items;
    } 

    // 서클 멤버들의 운동 기록을 피드 아이템으로 변환(운동기록은 공개범위 대상 X -> 그대로 노출)
    private List<MateFeedItemResponse> buildWorkoutFeedItems(List<Member> members) {
        List<MateFeedItemResponse> items = new ArrayList<>();

        List<Workout> workouts = workoutRepository.findByMemberInOrderByCreatedAtDesc(members);

        for (Workout workout : workouts) {
            Member author = workout.getMember();
            
            items.add(MateFeedItemResponse.builder()
                    .postType(FeedPostType.WORKOUT)
                    .postId(workout.getId())
                    .authorId(author.getId())
                    .authorName(author.getName())
                    .authorProfileImageUrl(author.getProfileImageUrl())
                    .createdAt(workout.getCreatedAt())
                    .workoutType(workout.getWorkoutType())
                    .customType(workout.getCustomType())
                    .durationMinutes(workout.getDurationMinutes())
                    .intensity(workout.getIntensity())
                    .build());
        }

        return items;
    }

    // 체성분 + 운동 피드를 하나로 합쳐 최신순(createdAt 내림차순)으로 정렬
    private List<MateFeedItemResponse> mergeByLatest(List<MateFeedItemResponse> bodyItems, List<MateFeedItemResponse> workoutItems) {
        List<MateFeedItemResponse> merged = new ArrayList<>(bodyItems);
        merged.addAll(workoutItems);

        merged.sort(Comparator.comparing(MateFeedItemResponse::getCreatedAt,
                Comparator.nullsLast(Comparator.reverseOrder())));

        return merged;
    }

}
