package com.medstili.emopulse.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.lottiefiles.dotlottie.core.widget.DotLottieAnimation;
import com.medstili.emopulse.databinding.FragmentBreathingExerciseBinding;

public class BreathingExerciseFragment extends Fragment {
    FragmentBreathingExerciseBinding binding;
    private  DotLottieAnimation dotLottieAnimationView;
    private Handler breathingHandler;
    private int currentStep = 0; // 0 = Inhale, 1 = Hold, 2 = Exhale
    private final String[] steps = {"Inhale", "Hold", "Exhale"};
    private final long[] durations = {4000, 7000, 8000}; // Inhale, Hold, Exhale durations
    private int remainingTime; // Remaining time for the current step
//    private  int loopCounter = 0;
//    private  int maxLoop = 4;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBreathingExerciseBinding.inflate(inflater, container, false);
        dotLottieAnimationView = binding.dotLottieAnimationView;
        binding.startBtn.setOnClickListener(v->{
                startBreathingExercise();
        });


        return binding.getRoot();
    }

    public  void  startBreathingExercise() {
        currentStep=0;
//        loopCounter=0;

        remainingTime = (int)(durations[currentStep]/1000);
        dotLottieAnimationView.play();
        updateBreathingStep();
        binding.startBtnContainer.setVisibility(View.GONE);
        binding.counter.setVisibility(View.VISIBLE);
        binding.breathingGuideSteps.setVisibility(View.VISIBLE);
        dotLottieAnimationView.setLoop(true);

    }
    public void updateBreathingStep(){
        breathingHandler = new Handler();
        Runnable breathingCycle = new Runnable() {
            @Override
            public void run() {
//                if (loopCounter>=maxLoop){
//                        binding.btnContainer.setVisibility(View.VISIBLE);
//                        binding.counter.setVisibility(View.GONE);
//                        binding.breathingGuideSteps.setVisibility(View.GONE);
//                        dotLottieAnimationView.pause();
//                        return;
//                }
                 binding.breathingGuideSteps.setText(steps[currentStep]);
                 binding.counter.setText( String.valueOf(remainingTime));
                 if (remainingTime > 0) {
                     remainingTime--;
                     breathingHandler.postDelayed(this, 1000);
                 }else{
                     currentStep = (currentStep + 1) % steps.length;
                     remainingTime = (int)(durations[currentStep]/1000);
//
//                     if (currentStep==0 && loopCounter<maxLoop-1){
//                         loopCounter++;
//                         dotLottieAnimationView.pause();
//                         binding.breathingGuideSteps.setText("take a rest for 4s");
//                         dotLottieAnimationView.postDelayed(this,4000);
//
//                     }
                     breathingHandler.post(this);

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