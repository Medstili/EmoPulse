package com.medstili.emopulse.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;


import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DatabaseError;
import com.medstili.emopulse.DashboardGoalsSummary.Goal;
import com.medstili.emopulse.DashboardGoalsSummary.GoalAdapter;
import com.medstili.emopulse.DataBase.DataBase;
import com.medstili.emopulse.Models.MoodLog;
import com.medstili.emopulse.Utils.MoodMath;
import com.medstili.emopulse.activities.MainActivity;
import com.medstili.emopulse.R;
import com.medstili.emopulse.databinding.FragmentDashboardBinding;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    private GoalAdapter goalAdapter;
    private List<Goal> goalsList = new ArrayList<>();
    MainActivity mainActivity;

    DataBase db;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        mainActivity = (MainActivity)  getActivity();
        assert mainActivity != null;
        mainActivity.hideBottomBarWhileScrollingDown(binding.dashboardScrollView);
        ViewCompat.setOnApplyWindowInsetsListener(binding.container, (v, insets) -> {
            Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            // Apply padding to the top for the status bar
            v.setPadding(0, statusBarInsets.top, 0, 0);
            return insets; // Return insets to keep consuming them
        });
        db = DataBase.getInstance();
        PieChart pieChart = binding.pieChart;
//       pie chart
        pieChart(pieChart);

        binding.breathingCard.setOnClickListener(v-> mainActivity.navController.navigate(R.id.action_dashboardFragment_to_exercisesNavigation2, null,null));
        binding.groundingCard.setOnClickListener(v-> mainActivity.navController.navigate(R.id.action_dashboardFragment_to_exercisesNavigation2, null,null));
        binding.bodyScanCard.setOnClickListener(v-> mainActivity.navController.navigate(R.id.action_dashboardFragment_to_exercisesNavigation2, null,null));


        return binding.getRoot();
    }

    private void barChart(BarChart barChart){
        // Create data entries
        List<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(6f, .5f));
        barEntries.add(new BarEntry(5f, 1f));
        barEntries.add(new BarEntry(1f, 4f));
        barEntries.add(new BarEntry(7f, 7f));
        barEntries.add(new BarEntry(2f, 3f));
        barEntries.add(new BarEntry(4f, 5f));
        barEntries.add(new BarEntry(3f, 3.5f));

        // Create a dataset and customize appearance
        BarDataSet barDataSet = new BarDataSet(barEntries, null);
        barDataSet.setColor(getResources().getColor(R.color.cyan));  // Set the bar color
        barDataSet.setValueTextColor(Color.WHITE);  // Set value text color
        barDataSet.setValueTextSize(12f);  // Set value text size
        barDataSet.setDrawValues(true);
        // Create BarData object
        BarData barData = new BarData(barDataSet);

        // Set the data to the chart
        barChart.setData(barData);

        // Customize X-Axis
        XAxis bar_xAxis = barChart.getXAxis();
        bar_xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        bar_xAxis.setTextColor(Color.parseColor("#ffffff"));
        bar_xAxis.setDrawGridLines(false);
        bar_xAxis.setGranularity(1f);


        // Customize Y-Axis
        YAxis bar_leftAxis = barChart.getAxisLeft();
        bar_leftAxis.setTextColor(Color.parseColor("#ffffff"));
        bar_leftAxis.setDrawGridLines(false);
        bar_leftAxis.setGranularity(1f);
        bar_leftAxis.setAxisMaximum(0f);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);  // Disable the right Y-axis

        // Disable description text (optional)
        barChart.getDescription().setEnabled(false);

        // Refresh the chart to display the data
        barChart.invalidate();
    }
    private void pieChart(PieChart pieChart){

        // 1. Create data entries
        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(40f, "Category 1")); // 40%
        pieEntries.add(new PieEntry(30f, "Category 2")); // 30%
        pieEntries.add(new PieEntry(10f, "Category 4")); // 10%

        // 2. Create PieDataSet with data
        int blue = getResources().getColor(R.color.cyan);
        int pink = getResources().getColor(R.color.pink);
        int pitchOrange = getResources().getColor(R.color.pitchOrange);

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Categories"); // Label
        pieDataSet.setColors( blue, pink, pitchOrange); // Colors
        pieDataSet.setValueTextSize(12f); // Text size
        pieDataSet.setValueTextColor(Color.WHITE); // Text color

        // 3. Create PieData
        PieData pieData = new PieData(pieDataSet);

        // 4. Set data to PieChart
        pieChart.setData(pieData);
        pieChart.invalidate(); // Refresh chart

        // 5. Customize PieChart appearance
        pieChart.setUsePercentValues(true); // Use percentage display
        pieChart.setEntryLabelColor(Color.BLACK); // Label color
        pieChart.setEntryLabelTextSize(12f); // Label text size

        // Remove chart description (optional)
        Description description = new Description();
        description.setText(""); // No description
        pieChart.setDescription(description);

        // Add hole in the center (optional)
        pieChart.setDrawHoleEnabled(true); // Enable hole
        pieChart.setHoleRadius(40f); // Hole size
        pieChart.setHoleColor(android.R.color.transparent);
        pieChart.setCenterTextSize(14f); // Center text size
        pieChart.setHighlightPerTapEnabled(true); // Enable highlight effect
        pieChart.setDrawEntryLabels(false); // Hide labels on slices
        pieChart.animateXY(1000, 1000); // Animate X and Y axes

    }
    private void setupMoodChart(LineChart lineChart, List<Entry> entries) {
        if (entries.isEmpty()) {
            lineChart.clear();
            return;
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "Mood Progress");
        lineDataSet.setDrawFilled(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setCircleColor(Color.parseColor("#E401FF"));
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setValueTextColor(Color.parseColor("#ffffff"));
        lineDataSet.setValueTextSize(0f);
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        lineDataSet.setColor(Color.parseColor("#E401FF"));
        lineDataSet.setLineWidth(3f);

        Drawable drawable = ContextCompat.getDrawable(requireActivity(), R.drawable.line_chart_gradient_fill);
        lineDataSet.setFillDrawable(drawable);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
//        lineChart.setPinchZoom(false);


        // --- X-Axis ---
        XAxis xAxis = lineChart.getXAxis();
        xAxis.removeAllLimitLines();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisLineColor(Color.parseColor("#44FFFFFF"));
        xAxis.setLabelRotationAngle(-45);
        // Consistent formatter - always show HH:mm
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            private final SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
            private final Calendar cal = Calendar.getInstance();

            @Override
            public String getFormattedValue(float value) {
                // Round to nearest hour
                cal.setTimeInMillis((long) value);
                    int minute = cal.get(Calendar.MINUTE);
                    if (minute >= 30) {
                        cal.add(Calendar.HOUR_OF_DAY, 1);
                    }
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);


                    return  timeFormat.format(cal.getTime()) ;
            }
        });

        // Also set these to enforce hourly labels:
        xAxis.setGranularity(  3600000f ); // 1 hour
        xAxis.setLabelCount(12, false); // Suggested count, not forced
        xAxis.resetAxisMinimum();
        xAxis.resetAxisMaximum();
        xAxis.setGranularityEnabled(true);
        xAxis.setAvoidFirstLastClipping(false); // Allow edge labels
        xAxis.setSpaceMin(0.5f); // Add padding
        xAxis.setSpaceMax(0.5f);

        // Add day separator lines at midnight boundaries
        Calendar cal = Calendar.getInstance();
        long prevDayStart = -1;
        for (Entry entry : entries) {
                cal.setTimeInMillis((long) entry.getX());
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                long dayStart = cal.getTimeInMillis();

                if (prevDayStart != -1 && dayStart != prevDayStart) {
                    // Add limit line at midnight
                    LimitLine dayDivider = new LimitLine(dayStart);
                    dayDivider.setLineColor(Color.parseColor("#66FFFFFF"));
                    dayDivider.setLineWidth(1f);
                    dayDivider.enableDashedLine(10f, 10f, 0f);
                    dayDivider.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                    dayDivider.setTextColor(Color.WHITE);
                    dayDivider.setTextSize(10f);
                    dayDivider.setLabel(new SimpleDateFormat("MMM dd", Locale.getDefault()).format(new Date(dayStart)));
                    xAxis.addLimitLine(dayDivider);
                }
                prevDayStart = dayStart;
        }
        // --- Y-Axis ---
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setTextColor(Color.WHITE);
        yAxis.setDrawGridLines(false);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(1f);
        yAxis.setLabelCount(5, true);
        yAxis.setSpaceTop(25f); // Add 20% padding at top
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value >= 0.8f) return "Happy";
                if (value >= 0.6f) return "Good";
                if (value >= 0.4f) return "Neutral";
                if (value >= 0.2f) return "Anxious";
                return "Sad/Angry";
            }
        });

        lineChart.setExtraTopOffset(10f);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);

        lineChart.setVisibleXRangeMaximum(8 * 3600000f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            lineChart.moveViewToX(entries.getLast().getX());
        }

        lineChart.animateX(1000);
        lineChart.invalidate();
    }
    private void setupBarChart(BarChart barChart, List<BarEntry> entries, List<String> labels) {
        if (entries.isEmpty()) {
            barChart.clear();
            return;
        }

        BarDataSet barDataSet = new BarDataSet(entries, null);
        barDataSet.setColor(getResources().getColor(R.color.cyan));
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setValueTextSize(12f);
        barDataSet.setDrawValues(false);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.parseColor("#ffffff"));
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < labels.size()) {
                    return labels.get(index);
                }
                return "";
            }
        });

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextColor(Color.parseColor("#ffffff"));
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularity(1f);
        leftAxis.setAxisMinimum(0f);

        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.invalidate();
    }
    private void loadGoalsSummaries() {
        goalAdapter = new GoalAdapter(requireContext(), goalsList);
        binding.goalsListView.setAdapter(goalAdapter);

        db.loadGoalsSummaries(new DataBase.loadGoalsSummariesCallback() {
            @Override
            public void onGoalLoaded(String goalId, String title, int targetCount, int completedCount, boolean done) {
                requireActivity().runOnUiThread(() -> {
                    if (done) {
                        goalAdapter.removeGoal(goalId);
                    } else {
                        goalAdapter.updateGoal(new Goal(goalId, title, targetCount, completedCount));
                    }
                });
            }

            @Override
            public void onComplete() {
                Log.d("loadGoalsSummaries", "All goals loaded");
            }

            @Override
            public void onFailure(DatabaseError error) {
                Log.e("loadGoalsSummaries", "Failed to load goals: " + error.getMessage());
            }
        });
    }
    private void loadMoodLogs(LineChart lineChart) {
        db.loadUserMoodData(new DataBase.loadUserMoodLogsCallback() {

            @Override
            public void onSuccess(List<MoodLog> moodLogs) {
                // 1. Process and Average the data
                List<Entry> entries = MoodMath.processData(moodLogs);

                // 2. Update the Chart
                setupMoodChart(lineChart, entries);
                Log.d("loadMoodLogs", "Mood logs loaded successfully");

            }


            @Override
            public void onFailure(DatabaseError error) {
                Log.e("loadMoodLogs", "Failed to load mood logs: " + error.getMessage());
            }
        });
    }
    private void loadCompletedExercises() {
        db.loadCompletedExercises(new DataBase.LoadExercisesCallback() {


            @Override
            public void onSuccess(String exerciseName, int count) {
                switch (exerciseName){

                    case "Breathing":
                        binding.breathingScore.setText(String.valueOf(count));
                        break;
                    case "Grounding":
                        binding.groundingScore.setText(String.valueOf(count));
                        break;
                    case "Body Scan":
                        binding.bodyScanScore.setText(String.valueOf(count));
                        break;
                    case "ButterFly":
                        binding.butterflyScore.setText(String.valueOf(count));
                        break;
                    case "Progressive Muscle Relaxation":
                        binding.pmrScore.setText(String.valueOf(count));
                        break;
                    case "Guided Imagery":
                        binding.guidedImageryScore.setText(String.valueOf(count));
                        break;
                    case "Phrase Repetition":
                        binding.phraseRepetitionScore.setText(String.valueOf(count));
                        break;
                    default:
                        Log.e("DB", "Unknown exercise name: " + exerciseName);
                        break;


                }
            }

            @Override
            public void onFailure(DatabaseError error) {
                Log.e("loadCompletedExercises", "Load failed: " + error.getMessage());
            }
            @Override
            public void onComplete(){
                Log.d("loadCompletedExercises", "Load completed");
            }
        });
    }
    private void loadInteractivityData(){
        db.loadInteractivityRawData(new DataBase.LoadInteractivityRawCallback() {
            @Override
            public void onSuccess(Map<String, Integer> dateToCount) {
                List<BarEntry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>();
                SimpleDateFormat dbFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                SimpleDateFormat displayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                Calendar cal = Calendar.getInstance();

                for (int i = 6; i >= 0; i--) {
                    cal.setTime(new Date());
                    cal.add(Calendar.DAY_OF_YEAR, -i);
                    String dateKey = dbFormat.format(cal.getTime());
                    String label = displayFormat.format(cal.getTime());

                    int messageCount = dateToCount.getOrDefault(dateKey, 0);
                    Log.d("loadInteractivityData", "Date: " + dateKey + ", Count: " + messageCount);
                    entries.add(new BarEntry(6 - i, messageCount));
                    labels.add(label);
                }

                setupBarChart(binding.barChart, entries, labels);
                Log.d("loadInteractivityData", "Interactivity data loaded successfully");
            }
            public void onComplete() {
                Log.d("loadInteractivityData", "Load completed");
            }
            @Override
            public void onFailure(DatabaseError error) {
                Log.e("loadInteractivityData", "Load failed: " + error.getMessage());
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.stopListeningForExercises();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadGoalsSummaries();
        loadCompletedExercises();
        loadInteractivityData();
        loadMoodLogs(binding.lineChart);




    }

}

