package com.medstili.emopulse.GoalsCard;

import java.util.Date;

public class GoalsCard {
    private String title,description;
    private String goalId;
    private Date createdAt;
    public GoalsCard(String goalId, String title, String description) {
        this.goalId = goalId;
        this.title = title;
        this.description = description;
        this.createdAt = new Date();
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getGoalId() {
        return goalId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
}
