// GoalDetails.java

package com.medstili.emopulse.fragment;

import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.SELECTION_MODE_NONE;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.medstili.emopulse.Calendar.CustomFrequencyDecorator;
import com.medstili.emopulse.Calendar.FrequencyDecorator;
import com.medstili.emopulse.Calendar.FrequencyRule;
import com.medstili.emopulse.Calendar.TrackedDecorator;
import com.medstili.emopulse.DataBase.DataBase;
import com.medstili.emopulse.databinding.FragmentGoalDetailsBinding;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class GoalDetails extends Fragment {

    private FragmentGoalDetailsBinding binding;
    private DataBase db ;
    private FrequencyRule freqRule;
    private String goalId;
    private List<LocalDate> done;
    private MaterialCalendarView cal;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGoalDetailsBinding.inflate(inflater, container, false);

        db = DataBase.getInstance();
        goalId = getArguments() != null ? getArguments().getString("goalId", "") : "";

        Bundle bundle = getArguments();
        if (bundle != null) {
            binding.goalDetailsTitle.setText(bundle.getString("title", ""));
            binding.goalDetailsDescription.setText(bundle.getString("description", ""));

        } else {
            Log.e("bundle", "No bundle data!");
        }
        cal = binding.calendarView;
        cal.setSelectionMode(SELECTION_MODE_NONE);
        done =new ArrayList<>();


        db.getGoalById( goalId,new DataBase.LoadGoalByIdCallback() {


            @Override
            public void onGoalLoaded(String goalId, String title, String description, String frequency, String exercise, List<String> customDays, Long created_At) {

                LocalDate start = Instant.ofEpochMilli(created_At)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                if ("Custom".equalsIgnoreCase(frequency) && customDays != null) {
                    binding.calendarView.addDecorator(
                            new CustomFrequencyDecorator(requireContext(), start, customDays)
                    );
                } else {
                    if (frequency != null) {
                        switch (frequency) {
                            case "Weekly":
                                freqRule = FrequencyRule.WEEKLY;
                                break;
                            case "Daily":
                                freqRule = FrequencyRule.DAILY;
                                break;
                            default:
                                Log.e("GoalDetails", "Unknown frequency: " + frequency);
                                Snackbar.make(binding.getRoot(), "Error", Snackbar.LENGTH_SHORT).show();
                        }
                        cal.addDecorator(
                                new FrequencyDecorator(requireContext(), start, freqRule)
                        );
                    }else{
                        Log.e("GoalDetails", "Frequency is null or empty");
                        Snackbar.make(binding.getRoot(), "Error: Frequency is not set", Snackbar.LENGTH_SHORT).show();
                    }

                }

                cal.addDecorator(
                        new TrackedDecorator(
                                requireActivity(),
                                done
                        )
                );

            }



            @Override
            public void onFailure(DatabaseError error) {

            }

            @Override
            public void onComplete() {

            }
        });

        cal.invalidateDecorators();



        return binding.getRoot();
    }
    @Override
    public void onStart() {
        super.onStart();
        db.loadGoalCheckDays(goalId, new DataBase.LoadGoalCheckDays() {
            @Override
            public void onSuccess(List<String> dateKeys) {
                for (String dateKey : dateKeys) {
                    try {
                        done.add(LocalDate.parse(dateKey));
                    } catch (DateTimeParseException e) {
                        Log.e("GoalDetails", "Invalid date in checkDays: " + dateKey, e);
                    }
                }
                cal.addDecorator(
                        new TrackedDecorator(requireContext(), done)
                );
                cal.invalidateDecorators();
            }

            @Override
            public void onFailure(DatabaseError error) {
                Log.e("GoalDetails", "Failed to load checkDays", error.toException());
                // Still add decorator with empty list
                binding.calendarView.addDecorator(
                        new TrackedDecorator(requireContext(), done)
                );
            }
        });

    }
}
