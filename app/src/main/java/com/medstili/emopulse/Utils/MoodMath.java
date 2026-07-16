package com.medstili.emopulse.Utils;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.medstili.emopulse.Models.MoodLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MoodMath {
    public static float getWellbeingScore(String label, double intensity) {
        float score = (float) intensity;
        return switch (label.toLowerCase().trim()) {
            case "happy", "excited", "surprised","good" -> 0.5f + (score / 2f); // 0.5 to 1.0
            case "anxious", "sad", "angry", "bad","depressed" -> 0.5f - (score / 2f); // 0.0 to 0.5
            default -> 0.5f; // Neutral
        };
    }

    public static List<Entry> processData(List<MoodLog> logs) {
        Map<String, List<Float>> groups = new HashMap<>();
        String pattern = "yyyyMMddHH" ;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());

        for (MoodLog log : logs) {
            String key = sdf.format(new Date(log.timestamp));
            if (!groups.containsKey(key)) groups.put(key, new ArrayList<>());
            Objects.requireNonNull(groups.get(key)).add(getWellbeingScore(log.moodLabel, log.moodScore));
        }

        List<Entry> entries = new ArrayList<>();
        for (String key : groups.keySet()) {
            float sum = 0;
            for (float val : Objects.requireNonNull(groups.get(key))) sum += val;
            float avg = sum / Objects.requireNonNull(groups.get(key)).size();
            try {

                long time = Objects.requireNonNull(sdf.parse(key)).getTime();
                entries.add(new Entry(time, avg));
            } catch (Exception e) { e.printStackTrace(); }
        }
        entries.sort((e1, e2) -> Float.compare(e1.getX(), e2.getX()));
        return entries;
    }
}
