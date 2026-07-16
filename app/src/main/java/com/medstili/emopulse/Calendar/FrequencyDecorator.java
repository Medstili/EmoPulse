package com.medstili.emopulse.Calendar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import com.medstili.emopulse.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class FrequencyDecorator implements DayViewDecorator {
    private final Drawable freqBg;
    private final Set<CalendarDay> days = new HashSet<>();
    public FrequencyDecorator(Context ctx, LocalDate createdAt, LocalDate endDate, FrequencyRule rule) {
        freqBg = ContextCompat.getDrawable(ctx, R.drawable.freq_bg);
        LocalDate adjustedEndDate = endDate.plusDays(1);
        for (LocalDate d = createdAt; d.isBefore(adjustedEndDate); d = d.plusDays(1)) {
            if (rule.matches(d)) {
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
        view.setBackgroundDrawable(freqBg);

    }
}
