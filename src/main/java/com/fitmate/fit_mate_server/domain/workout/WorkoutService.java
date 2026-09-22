package com.fitmate.fit_mate_server.domain.workout;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitmate.fit_mate_server.domain.member.Member;
import com.fitmate.fit_mate_server.domain.member.MemberRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final MemberRepository memberRepository;

    // 운동 기록 생성
    public WorkoutResponse createWorkout(Long memberId, WorkoutRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        validateCustomType(request);

        Workout workout = Workout.builder()
                .member(member)
                .workoutType(request.getWorkoutType())
                .customType(request.getWorkoutType() == WorkoutType.OTHER ? request.getCustomType() : null)
                .durationMinutes(request.getDurationMinutes())
                .intensity(request.getIntensity())
                .memo(request.getMemo())
                .recordDate(request.getRecordDate())
                .build();

        return WorkoutResponse.from(workoutRepository.save(workout));
    }

    // 운동 기록 수정: 본인만 가능
    public WorkoutResponse updateWorkout(Long memberId, Long workoutId, WorkoutRequest request) {
        Workout workout = getWorkoutOrThrow(workoutId);
        if (!workout.isOwnedBy(memberId)) {
            throw new IllegalArgumentException("본인의 운동 기록만 수정할 수 있습니다.");
        }
        validateCustomType(request);

        workout.update(
                request.getWorkoutType(),
                request.getWorkoutType() == WorkoutType.OTHER ? request.getCustomType() : null,
                request.getDurationMinutes(),
                request.getIntensity(),
                request.getMemo(),
                request.getRecordDate()
        );

        return WorkoutResponse.from(workout);
    }

    // 운동 기록 삭제 : 본인만 가능
    public void deleteWorkout(Long memberId, Long workoutId) {
        Workout workout = getWorkoutOrThrow(workoutId);
        if (!workout.isOwnedBy(memberId)) {
            throw new IllegalArgumentException("본인의 운동 기록만 삭제할 수 있습니다.");
        }
        workoutRepository.delete(workout);
    }


    private Workout getWorkoutOrThrow(Long workoutId) {
        return workoutRepository.findById(workoutId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 운동 기록입니다."));
    }

    // 운동 종류가 '기타'면 직접입력이 1~20자로 필수 (PRD 4.5)
    private void validateCustomType(WorkoutRequest request) {
        if (request.getWorkoutType() != WorkoutType.OTHER) return;

        String customType = request.getCustomType();
        if (customType == null || customType.isBlank() || customType.length() > 20) {
            throw new IllegalArgumentException("기타 운동 종류는 1자 이상 20자 이내로 입력해야 합니다.");
        }
    }    
    
}
