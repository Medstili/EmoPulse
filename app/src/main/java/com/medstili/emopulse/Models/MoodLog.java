package com.medstili.emopulse.Models;

public class MoodLog {
    public long timestamp;
    public String moodLabel;
    public double moodScore;
    public String context;

    public MoodLog() {} // Required for Firebase
}