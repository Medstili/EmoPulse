// GoalDetails.java

package com.medstili.emopulse.fragment;

import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.SELECTION_MODE_NONE;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.medstili.emopulse.Calendar.FrequencyDecorator;
import com.medstili.emopulse.Calendar.FrequencyRule;
import com.medstili.emopulse.Calendar.TrackedDecorator;
import com.medstili.emopulse.databinding.FragmentGoalDetailsBinding;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class GoalDetails extends Fragment {

    private FragmentGoalDetailsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGoalDetailsBinding.inflate(inflater, container, false);


        Intent intent = requireActivity().getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            binding.goalDetailsTitle.setText(bundle.getString("title", ""));
            binding.goalDetailsDescription.setText(bundle.getString("description", ""));
        } else {
            Log.e("bundle", "No bundle data!");
        }

        MaterialCalendarView cal = binding.calendarView;
        cal.setSelectionMode(SELECTION_MODE_NONE);
        LocalDate createdAt    = LocalDate.of(2024, 12,  1);
        FrequencyRule freqRule = FrequencyRule.FRIDAY;


        List<LocalDate> done = Arrays.asList(
                LocalDate.of(2024, 12,  13),
                LocalDate.of(2024, 12, 20)
        );


        cal.addDecorator(
                new FrequencyDecorator(requireContext(), createdAt, freqRule)
        );

        cal.addDecorator(
                new TrackedDecorator(
                        requireActivity(),
                        done
                )
        );

        cal.invalidateDecorators();


        return binding.getRoot();
    }
}
