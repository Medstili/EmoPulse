package com.medstili.emopulse.Calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;

public enum FrequencyRule {
    EVERY_DAY, WEEKLY,
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    public boolean matches(LocalDate d) {
        switch(this) {
            case EVERY_DAY:  return true;
            case WEEKLY:
            case MONDAY:
                return d.getDayOfWeek() == DayOfWeek.MONDAY;
            case TUESDAY:    return d.getDayOfWeek() == DayOfWeek.TUESDAY;
            case WEDNESDAY:  return d.getDayOfWeek() == DayOfWeek.WEDNESDAY;
            case THURSDAY:   return d.getDayOfWeek() == DayOfWeek.THURSDAY;
            case FRIDAY:     return d.getDayOfWeek() == DayOfWeek.FRIDAY;
            case SATURDAY:   return d.getDayOfWeek() == DayOfWeek.SATURDAY;
            case SUNDAY:     return d.getDayOfWeek() == DayOfWeek.SUNDAY;

            default:         return false;
        }
    }
}
