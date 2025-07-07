package com.medstili.emopulse.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseError;
import com.medstili.emopulse.DataBase.DataBase;
import com.medstili.emopulse.GoalsCard.GoalsCard;
import com.medstili.emopulse.GoalsCard.GoalsCardAdapter;
import com.medstili.emopulse.R;
import com.medstili.emopulse.activities.MainActivity;
import com.medstili.emopulse.databinding.FragmentGoalsBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class GoalsFragment extends Fragment {
    private  FragmentGoalsBinding binding;
    private RecyclerView recyclerView;
    DataBase db;

    MainActivity mainActivity;
    private List<GoalsCard> goalsCardList ;
    private HashMap<String, Integer> goalIdToIndex = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentGoalsBinding.inflate(inflater, container, false);
        mainActivity = (MainActivity) getActivity();
        db = DataBase.getInstance();
        assert mainActivity != null;
        mainActivity.hideBottomBarWhileScrollingDownRecyclerView(binding.goalsRecyclerView);
        recyclerView = binding.goalsRecyclerView;
        TextView noGoalsText = binding.noGoalsText;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        goalsCardList = new ArrayList<>();
        GoalsCardAdapter goalsCardAdapter = new GoalsCardAdapter(goalsCardList, getContext());
        recyclerView.setAdapter(goalsCardAdapter);
        binding.addGoalBtn.setOnClickListener(v-> showAddGoalDialog(noGoalsText));

        // Show loading at start, hide "no goal was added" text
        binding.loadingCircle.setVisibility(View.VISIBLE);
        binding.noGoalsText.setVisibility(View.GONE);

        return binding.getRoot();
    }


    private void addGoalsCard(String id, String title, String description) {
        GoalsCard goalsCard = new GoalsCard(id, title, description);
        goalsCardList.add(goalsCard);
        goalIdToIndex.put(id, goalsCardList.size() - 1);
        Log.d("goals", goalsCard.getTitle());
        Objects.requireNonNull(recyclerView.getAdapter()).notifyItemInserted(goalsCardList.size() - 1);
        recyclerView.scrollToPosition(0);
    }

    private void updateGoalsCard(String id, String title, String description) {
        Integer idx = goalIdToIndex.get(id);
        if (idx != null && idx < goalsCardList.size()) {
            GoalsCard card = goalsCardList.get(idx);
            card.setTitle(title);
            card.setDescription(description);
            Objects.requireNonNull(recyclerView.getAdapter()).notifyItemChanged(idx);
        }
    }

    private void removeGoalsCard(String id) {
        Integer idx = goalIdToIndex.get(id);
        if (idx != null && idx < goalsCardList.size()) {
            goalsCardList.remove((int) idx);
            goalIdToIndex.remove(id);
            // Rebuild index map
            goalIdToIndex.clear();
            for (int i = 0; i < goalsCardList.size(); i++) {
                goalIdToIndex.put(goalsCardList.get(i).getGoalId(), i);
            }
            Objects.requireNonNull(recyclerView.getAdapter()).notifyItemRemoved(idx);
        }
    }
    private void showAddGoalDialog(TextView noGoalsText) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());

        View dialogView = getLayoutInflater()
                .inflate(R.layout.add_goal_dialog, null, false);
        builder.setView(dialogView);
        LinearLayout checkboxContainer = dialogView.findViewById(R.id.checkboxContainer);
        TextInputLayout freqLayout   = dialogView.findViewById(R.id.freqLayout);
        AutoCompleteTextView freqDrop = dialogView.findViewById(R.id.freqDropdown);
//        TextInputLayout exercisesLayout   = dialogView.findViewById(R.id.exercisesLayout);
        AutoCompleteTextView exercisesDrop = dialogView.findViewById(R.id.exercisesDropdown);
        ArrayAdapter<String> exercisesAdapter = new ArrayAdapter<>(
                requireContext(),
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.exercise_options)
                );
        exercisesDrop.setAdapter(exercisesAdapter);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(),
                        com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.frequency_options));
        freqDrop.setAdapter(adapter);
        builder.setNegativeButton("Cancel", (d, w) -> d.dismiss());
        builder.setPositiveButton("Add", null);
        AlertDialog dialog = builder.create();
        Drawable glassBg = ResourcesCompat.getDrawable(getResources(), R.drawable.add_goal_bg, null);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(glassBg);
        dialog.show();


        freqDrop.setOnItemClickListener((parent, view, position, id) -> {
            freqLayout.setError(null);
            String freq = adapter.getItem(position);
            boolean isCustom = "Custom".equals(freq);

            checkboxContainer.setVisibility(isCustom
                    ? View.VISIBLE
                    : View.GONE
            );

            if (!isCustom) {
                for (int dayId : new int[]{
                        R.id.checkBoxSun,
                        R.id.checkBoxMon,
                        R.id.checkBoxTue,
                        R.id.checkBoxWed,
                        R.id.checkBoxThu,
                        R.id.checkBoxFri,
                        R.id.checkBoxSat
                })
                {
                    ((CheckBox)dialogView.findViewById(dayId)).setChecked(false);
                }

            }
        });

        Button addBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        addBtn.setOnClickListener(v -> {
            if (!validateGoalDialogInputs(dialogView)) return;

            String title = Objects.requireNonNull(
                    ((TextInputEditText)dialogView.findViewById(R.id.goalTitleEditText))
                            .getText()).toString().trim();

            String description = Objects.requireNonNull(
                    ((TextInputEditText)dialogView.findViewById(R.id.goalDescriptionEditText))
                            .getText()).toString().trim();


            String selectedFreq = freqDrop.getText().toString().trim();
            String selectedExercise = exercisesDrop.getText().toString().trim();
            List<String> customDays = new ArrayList<>();

            if(selectedFreq.equals("Custom")){
                CheckBox sun = dialogView.findViewById(R.id.checkBoxSun);
                CheckBox mon = dialogView.findViewById(R.id.checkBoxMon);
                CheckBox tue = dialogView.findViewById(R.id.checkBoxTue);
                CheckBox wed = dialogView.findViewById(R.id.checkBoxWed);
                CheckBox thu = dialogView.findViewById(R.id.checkBoxThu);
                CheckBox fri = dialogView.findViewById(R.id.checkBoxFri);
                CheckBox sat = dialogView.findViewById(R.id.checkBoxSat);

                boolean allDaysChecked = sun.isChecked() &&
                        mon.isChecked() &&
                        tue.isChecked() &&
                        wed.isChecked() &&
                        thu.isChecked() &&
                        fri.isChecked() &&
                        sat.isChecked();

                if (allDaysChecked) {
                    selectedFreq = "Daily";
                }else{
                    if (sun.isChecked()) customDays.add("Sunday");
                    if (mon.isChecked()) customDays.add("Monday");
                    if (tue.isChecked()) customDays.add("Tuesday");
                    if (wed.isChecked()) customDays.add("Wednesday");
                    if (thu.isChecked()) customDays.add("Thursday");
                    if (fri.isChecked()) customDays.add("Friday");
                    if (sat.isChecked()) customDays.add("Saturday");
                }

            }



            db.addUserGoal(
                    title,
                    description,
                    selectedFreq,
                    selectedExercise,
                    customDays,
                    new DataBase.GoalCompletionCallback() {



                        @Override
                        public void onSuccess() {
                            Log.d("DB", "Goal added successfully");
                            Snackbar.make(
                                    requireView(),
                                    "Goal added successfully",
                                    Snackbar.LENGTH_SHORT
                            ).show();

                        }

                        @Override
                        public void onFailure(DatabaseError error) {
                            Log.e("DB", "Failed to add goal: " + error.getMessage());
                            Snackbar.make(
                                    requireView(),
                                    "Failed to add goal: " + error.getMessage(),
                                    Snackbar.LENGTH_LONG
                            ).show();

                        }
                    }
            );
            noGoalsText.setVisibility(View.GONE);
            dialog.dismiss();
        });

    }

    /**
     * Validates all the fields in the “Add Goal” dialog.
     * @param dialogView the inflated dialog root
     * @return true if everything is valid, false if we should stay open
     */
    private boolean validateGoalDialogInputs(@NonNull View dialogView) {
        TextInputLayout titleLayout       = dialogView.findViewById(R.id.goalTitleLayout);
        TextInputLayout descriptionLayout = dialogView.findViewById(R.id.goalDescriptionLayout);
        TextInputLayout freqLayout        = dialogView.findViewById(R.id.freqLayout);
        TextInputEditText titleEt         = dialogView.findViewById(R.id.goalTitleEditText);
        TextInputEditText descEt          = dialogView.findViewById(R.id.goalDescriptionEditText);
        AutoCompleteTextView freqDrop     = dialogView.findViewById(R.id.freqDropdown);
        TextInputLayout exercisesLayout   = dialogView.findViewById(R.id.exercisesLayout);
        AutoCompleteTextView exercisesDrop = dialogView.findViewById(R.id.exercisesDropdown);


        CheckBox sun   = dialogView.findViewById(R.id.checkBoxSun);
        CheckBox mon   = dialogView.findViewById(R.id.checkBoxMon);
        CheckBox tue   = dialogView.findViewById(R.id.checkBoxTue);
        CheckBox wed   = dialogView.findViewById(R.id.checkBoxWed);
        CheckBox thu   = dialogView.findViewById(R.id.checkBoxThu);
        CheckBox fri   = dialogView.findViewById(R.id.checkBoxFri);
        CheckBox sat   = dialogView.findViewById(R.id.checkBoxSat);

        boolean valid = true;

        String title       = Objects.requireNonNull(titleEt.getText()).toString().trim();
        String description = Objects.requireNonNull(descEt.getText()).toString().trim();
        String selectedFreq = freqDrop.getText().toString().trim();
        String selectedExercise = exercisesDrop.getText().toString().trim();


        // reset errors
        titleLayout.setError(null);
        descriptionLayout.setError(null);
        freqLayout.setError(null);

        if (title.isEmpty()) {
            titleLayout.setError("Title is required");
            valid = false;
        }
        if (description.isEmpty()) {
            descriptionLayout.setError("Description is required");
            valid = false;
        }
        if (selectedFreq.isEmpty()) {
            freqLayout.setError("Select a frequency");
            valid = false;
        }

        if (selectedExercise.isEmpty()) {
            exercisesLayout.setError("Select an exercise");
            valid = false;
        }

        // if custom, require at least one box
        boolean noBoxesChecked =
                !sun.isChecked() &&
                        !mon.isChecked() &&
                        !tue.isChecked() &&
                        !wed.isChecked() &&
                        !thu.isChecked() &&
                        !fri.isChecked() &&
                        !sat.isChecked();

        if ("Custom".equals(selectedFreq) && noBoxesChecked) {
            freqLayout.setError("Select at least one day");
            valid = false;
        }

        return valid;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.removeGoalsListener();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        goalsCardList.clear();
        goalIdToIndex.clear();
        binding.loadingCircle.setVisibility(View.VISIBLE);
        binding.noGoalsText.setVisibility(View.GONE);
        db.checkGoalsExist(hasGoals -> {
            binding.loadingCircle.setVisibility(View.GONE);
            binding.noGoalsText.setVisibility(hasGoals ? View.GONE : View.VISIBLE);
            db.loadUserGoals(new DataBase.LoadGoalsCallback() {
                @Override
                public void onGoalLoaded(String goalId, String title, String description, String frequency, String exercise, List<String> customDays) {
                    if (title == null && description == null) {
                        removeGoalsCard(goalId);
                    } else if (goalIdToIndex.containsKey(goalId)) {
                        updateGoalsCard(goalId, title, description);
                    } else {
                        addGoalsCard(goalId, title, description);
                        if (binding.loadingCircle.getVisibility() == View.VISIBLE) {
                            binding.loadingCircle.setVisibility(View.GONE);
                        }
                        if (binding.noGoalsText.getVisibility() == View.VISIBLE) {
                            binding.noGoalsText.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onFailure(DatabaseError error) {
                    Log.e("DB", "Failed to load goals: " + error.getMessage());
                    Snackbar.make(
                            requireView(),
                            "Failed to load goals: " + error.getMessage(),
                            Snackbar.LENGTH_LONG
                    ).show();

                    binding.loadingCircle.setVisibility(View.GONE);
                    if (goalsCardList.isEmpty()) {
                        binding.noGoalsText.setVisibility(View.VISIBLE);
                    } else {
                        binding.noGoalsText.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onComplete() {
                    Log.d("DB", "All goals loaded");
                    binding.loadingCircle.setVisibility(View.GONE);
                    if (goalsCardList.isEmpty()) {
                        binding.noGoalsText.setVisibility(View.VISIBLE);
                    } else {
                        binding.noGoalsText.setVisibility(View.GONE);
                    }
                }


            });

        });

    }
}



