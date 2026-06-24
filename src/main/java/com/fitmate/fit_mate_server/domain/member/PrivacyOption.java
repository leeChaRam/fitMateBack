package com.fitmate.fit_mate_server.domain.body;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum PrivacyOption {
    PRIVATE(1, "🔒 나만 보기"),
    DELTA_ONLY(2, "📈 변화량만 공개"),
    PUBLIC(3, "👥 그룹 전체 공개");

    private final int dbValue; // DB에 저장될 숫자 (1, 2, 3)
    private final String title;

    PrivacyOption(int dbValue, String title) {
        this.dbValue = dbValue;
        this.title = title;
    }

    // 💡 1. Jackson이 JSON으로 직렬화/역직렬화할 때 dbValue(숫자)를 기준으로 삼도록 지정합니다.
    @JsonValue
    public int getDbValue() {
        return this.dbValue;
    }

    public String getTitle() {
        return this.title;
    }

    // 💡 2. Postman에서 숫자 3이 들어오면 이 메서드를 실행해 PUBLIC을 찾으라고 스프링에게 명시합니다.
    @JsonCreator
    public static PrivacyOption fromDbValue(Integer value) {
        if (value == null) return null;
        for (PrivacyOption option : PrivacyOption.values()) {
            if (option.getDbValue() == value) {
                return option;
            }
        }
        throw new IllegalArgumentException("올바르지 않은 공개 설정 값입니다: " + value);
    }

}
