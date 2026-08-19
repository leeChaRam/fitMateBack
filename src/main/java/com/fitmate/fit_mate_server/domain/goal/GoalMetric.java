package com.fitmate.fit_mate_server.domain.goal;

public enum GoalMetric {
    WEIGHT("kg"),
    MUSCLE_MASS("kg"),
    BODY_FAT_PERCENT("%"),
    BODY_FAT_MASS("kg");

    private final String unit;

    GoalMetric(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }
    
}
