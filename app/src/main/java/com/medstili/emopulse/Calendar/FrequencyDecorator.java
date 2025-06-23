package com.medstili.emopulse.Calendar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

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

    public FrequencyDecorator(Context ctx, LocalDate createdAt, FrequencyRule rule) {
        freqBg = ContextCompat.getDrawable(ctx, R.drawable.freq_bg);
        LocalDate endOfYear = LocalDate.of(LocalDate.now().getYear(), 12, 31);


        for (LocalDate d = createdAt; !d.isAfter(endOfYear); d = d.plusDays(1)) {
            if (rule.matches(d)) {
                days.add(CalendarDay.from(d.getYear(), d.getMonthValue(), d.getDayOfMonth()));

                Log.d("FrequencyDecorator", "Added " + d);
                Log.d("FrequencyDecorator", "Days size: " + days.size());
            }
        }
        Log.d("FrequencyDecorator", "Days: " + days);
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
