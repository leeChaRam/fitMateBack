package com.fitmate.fit_mate_server.domain.mate.feed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import com.fitmate.fit_mate_server.domain.workout.WorkoutIntensity;
import com.fitmate.fit_mate_server.domain.workout.WorkoutType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MateFeedItemResponse {
    private FeedPostType postType;
    private Long postId;

    private Long authorId;
    private String authorName;
    private String authorProfileImageUrl;

    private LocalDateTime createdAt; // 정렬 기준 (최신순)

    // 체성분 기록 전용 (postType == BODY_INFO 일 때만 값 있음, 공개범위에 따라 null일 수 있음)
    private LocalDate measureDate;
    private Double weightValue;
    private String weightDelta;
    private Double muscleValue;
    private String muscleDelta;
    private Double fatValue;
    private String fatDelta;

    // 운동 기록 전용 (postType == WORKOUT 일 때만 값 있음)
    private WorkoutType workoutType;
    private String customType;
    private Integer durationMinutes;
    private WorkoutIntensity intensity;

    // 공통: 반응/댓글
    private Map<MateReactionEmoji, Long> reactionCounts;
    private MateReactionEmoji myReaction; // 로그인한 사람이 이미 남긴 반응 (없으면 null)
    private long commentCount;
}
