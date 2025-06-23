package com.medstili.emopulse.fragment;



import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lottiefiles.dotlottie.core.widget.DotLottieAnimation;
import com.medstili.emopulse.R;
import com.medstili.emopulse.databinding.FragmentBreathingExerciseBinding;

public class BreathingExerciseFragment extends Fragment {
    FragmentBreathingExerciseBinding binding;
    private  DotLottieAnimation dotLottieAnimationView;
    private Handler breathingHandler;
    private int currentStep = 0; // 0 = Inhale, 1 = Hold, 2 = Exhale
    private final String[] steps = {"Inhale", "Hold", "Exhale"};
    private final long[] durations = {4000, 7000, 8000}; // Inhale, Hold, Exhale durations
    private int remainingTime; // Remaining time for the current step
    private  int loopCount = 0; // Counter for the number of loops
    private  int maxLoops = 3; // Maximum number of loops
    private boolean isRestTime = false;
    private  int restTime= 4;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBreathingExerciseBinding.inflate(inflater, container, false);
        dotLottieAnimationView = binding.dotLottieAnimationView;
        binding.startBtn.setOnClickListener(v -> {
            startBreathingExercise();
        });
        binding.doneBtn.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
//            navController.navigateUp();
            navController.popBackStack(R.id.exercicesFragment,false);

        });
        binding.doItAgainBtn.setOnClickListener(v -> {
            startBreathingExercise();
            binding.btnContainer.setVisibility(View.GONE);
        });
        return binding.getRoot();
    }
    public  void  startBreathingExercise() {
        currentStep=0;
        loopCount=0;
        remainingTime = (int)(durations[currentStep]/1000);
        dotLottieAnimationView.play();
        binding.startBtnContainer.setVisibility(View.GONE);
        binding.counter.setVisibility(View.VISIBLE);
        binding.breathingGuideSteps.setVisibility(View.VISIBLE);
        updateBreathingStep();
    }
    public void updateBreathingStep(){
        breathingHandler = new Handler();
        Runnable breathingCycle = new Runnable() {
            @Override
            public void run() {
                if (loopCount >= maxLoops) {
                    // Stop after completing all loops
                    binding.counter.setVisibility(View.GONE);
                    binding.breathingGuideSteps.setText("Well Done");
                    binding.btnContainer.setVisibility(View.VISIBLE);
                    binding.startBtnContainer.setVisibility(View.GONE);
                    dotLottieAnimationView.pause();
                    return;
                }
                if (isRestTime){
                    binding.breathingGuideSteps.setText("Take A Rest");
                    binding.counter.setText(String.valueOf(restTime));
                    dotLottieAnimationView.pause();
                    if(restTime>0){
                        restTime--;
                        breathingHandler.postDelayed(this,1000);
                    }
                    else{
                        isRestTime =false;
                        currentStep = 0;
                        remainingTime = (int) (durations[currentStep] / 1000);
                        dotLottieAnimationView.play();
                        breathingHandler.post(this);
                        Log.d("currentStep",String.valueOf(currentStep));
                    }
                }
                else{

                    binding.breathingGuideSteps.setText(steps[currentStep]);
                    binding.counter.setText(String.valueOf(remainingTime));

                    if (remainingTime >= 1) {
                        remainingTime--;
                        breathingHandler.postDelayed(this, 1000);
                    }
                    else{
                        currentStep = (currentStep +1) % steps.length;
                        remainingTime = (int) (durations[currentStep] / 1000); // Reset for Inhale

                        if (currentStep == 0) {
                            isRestTime =true;
                            restTime=4;
                            loopCount++;
                            binding.breathingGuideSteps.setText("Take A Rest");
                            binding.counter.setText(String.valueOf(restTime));
                            breathingHandler.post(this);

                        }
                        else{

                            remainingTime = (int) (durations[currentStep] / 1000);
                            breathingHandler.post(this);
                        }
                    }
                }
             }
        };
        breathingHandler.post(breathingCycle);
    }
    public void onDestroyView() {
        super.onDestroyView();
        if (breathingHandler != null) {
            breathingHandler.removeCallbacksAndMessages(null); // Stop the handler
        }
    }
}