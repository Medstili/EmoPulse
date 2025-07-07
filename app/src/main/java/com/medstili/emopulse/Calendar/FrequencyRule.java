package com.medstili.emopulse.Calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;

public enum  FrequencyRule {
    DAILY, WEEKLY;
    public boolean matches(LocalDate d) {
        return switch (this) {
            case DAILY -> true;
            case WEEKLY -> d.getDayOfWeek() == DayOfWeek.MONDAY;
            default -> false;
        };
    }
}
