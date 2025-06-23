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
import java.util.List;
import java.util.Set;

public class TrackedDecorator implements DayViewDecorator {
    private final Drawable trackedBg;
    private final Set<CalendarDay> doneDays = new HashSet<>();

    public TrackedDecorator(Context ctx, List<LocalDate> doneDates) {
        trackedBg = ContextCompat.getDrawable(ctx, R.drawable.checked_days_bg);
        for (LocalDate d : doneDates) {
            doneDays.add(CalendarDay.from(d.getYear(), d.getMonthValue(), d.getDayOfMonth()));
        }
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return doneDays.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        // This will draw *on top* of the freqBg
        view.setSelectionDrawable(trackedBg);
    }
}
