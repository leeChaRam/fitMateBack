package com.fitmate.fit_mate_server.domain.body;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PrivacyOption {
    PRIVATE(1, "🔒 나만 보기"),
    DELTA_ONLY(2, "📈 변화량만 공개"),
    PUBLIC(3, "👥 그룹 전체 공개");

    private final int dbValue; // DB에 저장될 숫자 (1, 2, 3)
    private final String title;

     // 이코드 오류로 get 추가 실행시 괜찮으면 지워보기
    public int getDbValue() {
        return this.dbValue;
    }

    public String getTitle() {
        return this.title;
    }

    // 숫자를 가지고 Enum을 찾아내는 역방향 메서드
    public static PrivacyOption fromDbValue(int value) {
        for (PrivacyOption option : PrivacyOption.values()) {
            if (option.getDbValue() == value) {
                return option;
            }
        }
        throw new IllegalArgumentException("올바르지 않은 공개 설정 값입니다: " + value);
    }

}
