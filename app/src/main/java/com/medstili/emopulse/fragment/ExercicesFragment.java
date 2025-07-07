package com.medstili.emopulse.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.FragmentNavigator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.medstili.emopulse.activities.MainActivity;
import com.medstili.emopulse.R;
import com.medstili.emopulse.databinding.FragmentExercicesBinding;



public class ExercicesFragment extends Fragment {
    public MainActivity mainActivity;
    private FragmentExercicesBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExercicesBinding.inflate(inflater, container, false);
        mainActivity = (MainActivity) getActivity();

        assert mainActivity != null;
        mainActivity.hideBottomBarWhileScrollingDown(binding.exercisesScrollView);
        ViewCompat.setOnApplyWindowInsetsListener(binding.titlecontainer, (v, insets) -> {
            Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());

            // Apply padding to the top for the status bar
            v.setPadding(0, statusBarInsets.top, 0, 0);

            return insets; // Return insets to keep consuming them
        });
        binding.breathingCard.setOnClickListener(v->{
            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                    .addSharedElement(binding.cardBreathingImg, "sharing_breathing_img")
                    .addSharedElement(binding.cardBreathingTitle, "sharing_breathing_title")
                    .build();
            mainActivity.navController.navigate(R.id.action_exercicesFragment_to_breathingFragment, null,null ,extras);
        });
        binding.groundingCard.setOnClickListener(v->{
            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                    .addSharedElement(binding.cardGroundingTitle, "sharingGroundingExTitle")
                    .build();
            mainActivity.navController.navigate(R.id.action_exercicesFragment_to_groundingExerciseFragment, null,null ,extras);
        });
        binding.bodyScanCard.setOnClickListener(v->{
            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                    .addSharedElement(binding.cardBodyScanTitle, "sharingBodyScanExTitle")
                    .addSharedElement(binding.cardBodyScanImg, "sharingBodyScanExImg")
                    .build();
            mainActivity.navController.navigate(R.id.action_exercicesFragment_to_bodyScanExerciseFragment, null,null ,extras);
        });


        return binding.getRoot();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}