 package com.medstili.emopulse.fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.medstili.emopulse.R;
import com.medstili.emopulse.activities.MainActivity;
import com.medstili.emopulse.databinding.FragmentBreathingBinding;

 public class BreathingFragment extends Fragment {

    FragmentBreathingBinding binding;
    int counter;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBreathingBinding.inflate(inflater, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        int[]  guideParts = {R.string.guide_part_1, R.string.guide_part_2, R.string.guide_part_3, R.string.guide_part_4, R.string.guide_part_5, R.string.guide_part_6, R.string.guide_part_7};
        counter = 0;
        binding.breathingGuideDescription.setText(guideParts[0]);
        // Load the fade-in animation
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        // Start the fade-in animation
        binding.breathingGuideDescription.startAnimation(fadeIn);
        binding.breathingNextBtn.setOnClickListener(v->{
            // Load the fade-out animation
            Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
            // Set an animation listener to change the text after the fade-out
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // Optional: Actions to perform when the animation starts
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Change the text of the TextView
                    counter = counter + 1;
                    binding.breathingGuideDescription.setText(guideParts[counter]);

                    // Start the fade-in animation
                    binding.breathingGuideDescription.startAnimation(fadeIn);

                    if (counter == guideParts.length - 1){
                        binding.breathingNextBtn.setVisibility(View.GONE);
                        binding.breathingGoBtn.setVisibility(View.VISIBLE);
                        counter=0;
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // Optional: Actions to perform when the animation repeats
                }
            });
            // Start the fade-out animation
            binding.breathingGuideDescription.startAnimation(fadeOut);
            Log.d("counter", String.valueOf(counter));
        });
        binding.breathingGoBtn.setOnClickListener(v->{
            assert mainActivity != null;
            mainActivity.navController.navigate(R.id.action_breathingFragment_to_breathingExerciseFragment);
            Log.d("go btn", "go btn is clicked");
        });

        Transition transition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_element_transition);
        setSharedElementEnterTransition(transition);
        setSharedElementReturnTransition(transition);
        ViewCompat.setTransitionName(binding.breathingImg, "sharing_breathing_img");
        ViewCompat.setTransitionName(binding.breathingTitle, "sharing_breathing_title");
        return binding. getRoot();
    }
}