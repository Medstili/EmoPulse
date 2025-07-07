package com.medstili.emopulse.Calendar;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.medstili.emopulse.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Decorator for “Custom” frequency: marks only the days of week the user selected.
 * customDays strings should be like "MONDAY", "FRIDAY", etc. (matching DayOfWeek.name()).
 */
public class CustomFrequencyDecorator implements DayViewDecorator {
    private final Drawable bg;
    private final Set<CalendarDay> days = new HashSet<>();

    public CustomFrequencyDecorator(
            Context ctx,
            LocalDate startDate,
            List<String> customDays
    ) {
        bg = ContextCompat.getDrawable(ctx, R.drawable.freq_bg);

        Set<DayOfWeek> dowSet = new HashSet<>();
        for (String s : customDays) {
            try {
                dowSet.add(DayOfWeek.valueOf(s.toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid day of week: " + s);
            }
        }

        // Build the calendar days from startDate → endOfYear
        LocalDate endOfYear = LocalDate.of(startDate.getYear(), 12, 31);
        for (LocalDate d = startDate; !d.isAfter(endOfYear); d = d.plusDays(1)) {
            if (dowSet.contains(d.getDayOfWeek())) {
                days.add(CalendarDay.from(d.getYear(), d.getMonthValue(), d.getDayOfMonth()));
            }
        }
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return days.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(bg);
    }
}
