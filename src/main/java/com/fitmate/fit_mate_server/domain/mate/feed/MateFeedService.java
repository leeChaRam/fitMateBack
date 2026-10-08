package com.fitmate.fit_mate_server.domain.mate.feed;

import java.util.*;
import java.util.stream.Collectors;

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

    // 피드 아이템마다 반응 수 / 내 반응 / 댓글 수를  붙여 반환 (타입별 IN 쿼리로 일괄 조회 -> N+1 방지)
    private List<MateFeedItemResponse> attachReactionAndComments(Mate mate, Long loginMemberId, List<MateFeedItemResponse> items) {
        // 1) 글 타입별로 postId 모으기(BODY_INFO ->[1, 3, 5], WORKOUT -> [2, 4]
        Map<FeedPostType, List<Long>> postIdsByType = new HashMap<>();
        for (MateFeedItemResponse item : items) {
            FeedPostType type = item.getPostType();
            if (!postIdsByType.containsKey(type)) {          // 서랍이 없으면
                postIdsByType.put(type, new ArrayList<>());  // 새 서랍 만들고
            }
            postIdsByType.get(type).add(item.getPostId());   // 서랍에 id 넣기
        }

        // 2) 타입별로 반응/댓글 한 번에 조회 
        List<MateReaction> reactions = new ArrayList<>();
        List<MateComment> comments = new ArrayList<>();
        for(Map.Entry<FeedPostType, List<Long> entry : postIdsByType.entrySet()) {
            reactions.addAll(mateReactionRepository.findByMateAndPostTypeAndPostIdIn(mate, entry.getKey(), entry.getValue()));
            comments.addAll(mateCommentRepository.findByMateAndPostTypeAndPostIdIn(mate, entry.getKey(), entry.getValue()));
        }
        
        // 3) 글(PostKey)별로 바구니에 나눠 담기
        Map<PostKey, List<MateReaction>> reactionsByPost = reactions.stream()
                .collect(Collectors.groupingBy(c -> new PostKey(c.getPostType(), c.getPostId())));
        Map<PostKey, Long> commentCountByPost = comments.stream()
                .collect(Collectors.groupingBy(c -> new PostKey(c.getPostType(), c.getPostId()), Collectors.counting()));

        // 4) 쪽지마다 반응 수 / 내 반응 / 댓글 수 채워서 새로 만들기 
        List<MateFeedItemResponse> result = new ArrayList<>();
        for (MateFeedItemResponse item : items) {
            PostKey key = new PostKey(item.getPostType(), item.getPostId());

            Map<MateReactionEmoji, Long> reactionsCounts = new EnumMap<>(MateReactionEmoji.class);
            MateReactionEmoji myReaction = null;
            for (MateReaction reaction : reactionsByPost.getOrDefault(key, List.of())) {
                reactionsCounts.merge(reaction.getEmoji(), 1L, Long::sum);
                if(reaction.getMember().getId().equals(loginMemberId)) {
                    myReaction = reaction.getEmoji();
                }

            }
        }
    
    }

}
