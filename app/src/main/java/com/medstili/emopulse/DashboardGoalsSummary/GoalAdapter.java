package com.medstili.emopulse.DashboardGoalsSummary;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.medstili.emopulse.R;

import java.util.List;

public class GoalAdapter extends BaseAdapter {
    private Context context;
    private List<Goal> goals;

    public GoalAdapter(Context context, List<Goal> goals) {
        this.context = context;
        this.goals = goals;
    }

    @Override
    public int getCount() { return goals.size(); }

    @Override
    public Goal getItem(int position) { return goals.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.goal_item, parent, false);
        }

        Goal goal = getItem(position);
        TextView titleView = convertView.findViewById(R.id.goalTitle);
        LinearProgressIndicator progressIndicator = convertView.findViewById(R.id.goalProgress);
        TextView goalPercentage = convertView.findViewById(R.id.goalPercentage);

        titleView.setText(goal.getTitle());
        progressIndicator.setProgress(goal.getProgress());
        progressIndicator.setTrackColor(Color.parseColor("#32FFFFFF"));
        progressIndicator.setIndicatorColor(ContextCompat.getColor(context, R.color.cyan));
        String percentageText = goal.getProgress() + "%";
        goalPercentage.setText(percentageText);


        return convertView;
    }

    public void updateGoal(Goal updatedGoal) {
        for (int i = 0; i < goals.size(); i++) {
            if (goals.get(i).getId().equals(updatedGoal.getId())) {
                goals.set(i, updatedGoal);
                notifyDataSetChanged();
                return;
            }
        }
        goals.add(updatedGoal);
        notifyDataSetChanged();
    }

    public void removeGoal(String goalId) {
        goals.removeIf(g -> g.getId().equals(goalId));
        notifyDataSetChanged();
    }
}

