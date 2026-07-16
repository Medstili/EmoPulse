package com.medstili.emopulse.DashboardGoalsSummary;

public class Goal {
    private String id;
    private String title;
    private int targetCount;
    private int completedCount;

    public Goal(String id, String title, int targetCount, int completedCount) {
        this.id = id;
        this.title = title;
        this.targetCount = targetCount;
        this.completedCount = completedCount;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public int getTargetCount() { return targetCount; }
    public int getCompletedCount() { return completedCount; }
    public int getProgress() {
        return targetCount > 0 ? Math.min((completedCount * 100) / targetCount, 100) : 0;
    }
}
