package com.medstili.emopulse.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
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
import com.google.firebase.database.DatabaseError;
import com.medstili.emopulse.DataBase.DataBase;
import com.medstili.emopulse.activities.MainActivity;
import com.medstili.emopulse.R;
import com.medstili.emopulse.databinding.FragmentDashboardBinding;


import java.util.ArrayList;
import java.util.List;


public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
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
//        recyclerView = binding.recyclerView;
        LineChart lineChart = binding.lineChart;
        BarChart barChart = binding.barChart;
        PieChart pieChart = binding.pieChart;
        db = DataBase.getInstance();

        lineChart(lineChart);
//      bar chart
        barChart(barChart);
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
        barDataSet.setDrawValues(false);
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
        bar_leftAxis.setDrawGridLines(true);
        bar_leftAxis.setGranularity(1f);
        bar_leftAxis.setDrawGridLines(false);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);  // Disable the right Y-axis

        // Disable description text (optional)
        barChart.getDescription().setEnabled(false);

        // Refresh the chart to display the data
        barChart.invalidate();
    }

    private void lineChart(LineChart lineChart){

        // Create sample data points
        ArrayList<Entry> entries = new ArrayList<>();
        // Example of adding more entries dynamically
        for (int i = 0; i < 50; i++) { // Add 50 points
            entries.add(new Entry(i, (float) Math.sin(i) * 10)); // Example: sine wave
        }
        // Create LineDataSet
        LineDataSet lineDataSet = new LineDataSet(entries, "Gradient Example");
        lineDataSet.setDrawFilled(true);  // Enable fill below the line
        lineDataSet.setDrawCircles(false); // No circles on data points
        lineDataSet.setValueTextColor(Color.parseColor("#ffffff"));  // Change the color of the value labels
        lineDataSet.setValueTextSize(10f);  // Optional: Adjust the size of the value labels
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Smooth curve
        lineDataSet.setColor(Color.parseColor("#E401FF"));  // Line color
        lineDataSet.setLineWidth(2f);      // Line thickness
        // Apply Gradient Fill
        Drawable drawable = ContextCompat.getDrawable(requireActivity(), R.drawable.line_chart_gradient_fill);
        lineDataSet.setFillDrawable(drawable); // Set the gradient drawable
        // Set Data
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
// Enable touch gestures
        lineChart.setTouchEnabled(true);
// Enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
// Enable horizontal scrolling
        lineChart.setPinchZoom(true); // Allows zooming in/out with two fingers
        lineChart.setDragXEnabled(true); // Enable horizontal drag
// Optional: Disable vertical scrolling
        lineChart.setDragYEnabled(true); // Keeps vertical axis fixed
// Set visible range for x-axis (initial view)
        lineChart.setVisibleXRangeMaximum(7f); // Show only 5 data points initially
// Allow scrolling to the right if data exceeds visible range
        lineChart.moveViewToX(0); // Start from the beginning
        lineChart.setVisibleXRangeMaximum(10f); // Show only 10 points at a time
        lineChart.setVisibleXRangeMinimum(5f);  // Minimum view size
        lineChart.moveViewToX(entries.size() - 1); // Move to the last point dynamically
        // Customize Chart
        lineChart.getDescription().setEnabled(false); // No description
        lineChart.getXAxis().setDrawGridLines(false); // Hide X-axis grid lines
        lineChart.getAxisLeft().setDrawGridLines(false); // Hide Y-axis grid lines
        lineChart.getAxisRight().setEnabled(false);   // Hide right Y-axis
        lineChart.getLegend().setEnabled(false);      // Hide legend
        lineChart.setDrawGridBackground(false);       // No grid background
        lineChart.animateX(1000); // Animation

        XAxis xAxis = lineChart.getXAxis();
        YAxis yAxis = lineChart.getAxisLeft();
        xAxis.setGranularity(1f); // Minimum interval between values
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(-45); // Rotate labels for readability
        xAxis.setAxisLineColor(Color.parseColor("#ffffff")); // Set X-axis line color;
        xAxis.setTextColor(Color.parseColor("#ffffff"));
        yAxis.setAxisLineColor(Color.parseColor("#ffffff"));
        yAxis.setTextColor(Color.parseColor("#ffffff"));

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.stopListeningForExercises();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        db.loadCompletedExercises(new DataBase.LoadExercisesCallback() {
            // Use a map to collect all loaded exercises
//            Map<String, Integer> completedExercises = new HashMap<>();

            @Override
            public void onSuccess(String exerciseName, int count) {
//                completedExercises.put(exerciseName, count);
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
                // Optionally handle error
            }
            @Override
            public void onComplete(){}
        });
    }
}