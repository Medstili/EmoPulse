package com.medstili.emopulse.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.dotlottie.dlplayer.Mode;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.lottiefiles.dotlottie.core.model.Config;
import com.lottiefiles.dotlottie.core.util.DotLottieSource;

import com.lottiefiles.dotlottie.core.widget.DotLottieAnimation;
import com.medstili.emopulse.DataBase.DataBase;
import com.medstili.emopulse.R;
import com.medstili.emopulse.databinding.FragmentGroundingExerciseBinding;


public class GroundingExerciseFragment extends Fragment {
    FragmentGroundingExerciseBinding binding;
    public DotLottieAnimation dotLottieAnimation;
    private Animation fadeIn ;
    private int currentIndex =0;
    public String[] guideStepsList , guideStepsExampleList;
    private DataBase db = DataBase.getInstance();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding =  FragmentGroundingExerciseBinding.inflate(inflater, container, false);

        dotLottieAnimation = binding.dotLottieAnimationView;

        Transition transition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_element_transition);
        setSharedElementEnterTransition(transition);
        setSharedElementReturnTransition(transition);
        ViewCompat.setTransitionName(binding.groundingTitle, "sharingGroundingExTitle");

        fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        binding.groundingGuideSteps.startAnimation(fadeIn);

        guideStepsList = getResources().getStringArray(R.array.groundingGuideSteps);
        guideStepsExampleList = getResources().getStringArray(R.array.groundingGuideStepsExample);

        binding.groundingExStartBtn.setOnClickListener(v->{
           startExercise();
        });
        binding.groundingExNextBtn.setOnClickListener(v->{
            updateExercise();
        });
        binding.groundingExDoneBtn.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigateUp();
        });
        binding.groundingExDoItAgainBtn.setOnClickListener(v->{
            doItAgain();
        });

        return binding.getRoot();
    }
    public void changeAnimation(String animation , boolean loop){
        Config config = new Config.Builder()
                .autoplay(true)
                .speed(1f)
                .loop(loop)
                .source(new DotLottieSource.Asset(animation)) // asset from the asset folder .json or .lottie
                .useFrameInterpolation(true)
                .playMode(Mode.FORWARD)
                .build();
        dotLottieAnimation.load(config);
        dotLottieAnimation.startAnimation(fadeIn);

    }
    public void startExercise(){
        binding.groundingExStartBtn.setVisibility(View.GONE);
        binding.groundingExNextBtn.setVisibility(View.VISIBLE);
        binding.groundingGuideSteps.setText(guideStepsList[currentIndex]);
        binding.groundingGuideStepsExample.setVisibility(View.VISIBLE);
        binding.groundingGuideStepsExample.setText(guideStepsExampleList[currentIndex]);
        binding.groundingGuideStepsExample.startAnimation(fadeIn);
        binding.groundingExDoItAgainBtn.setVisibility(View.GONE);
        binding.groundingExDoneBtn.setVisibility(View.GONE);
        changeAnimation("blueeyes.lottie", true);
        currentIndex++;
    }
    public void doItAgain(){
        currentIndex=0;
        dotLottieAnimation.setVisibility(View.VISIBLE);
        binding.groundingExStartBtn.setVisibility(View.VISIBLE);
        binding.groundingExNextBtn.setVisibility(View.GONE);
        binding.groundingGuideStepsExample.setVisibility(View.GONE);
        binding.groundingGuideStepsExample.startAnimation(fadeIn);
        binding.groundingExDoItAgainBtn.setVisibility(View.GONE);
        binding.groundingExDoneBtn.setVisibility(View.GONE);
        binding.groundingGuideSteps.setText(getResources().getString(R.string.grounding_ex_description));
        changeAnimation("senseAnim.lottie", false);
    }
    public void updateExercise(){

        binding.groundingGuideSteps.setText(guideStepsList[currentIndex]);
        binding.groundingGuideSteps.startAnimation(fadeIn);
        if(currentIndex == guideStepsList.length-1){
            binding.groundingExNextBtn.setVisibility(View.GONE);
            binding.groundingGuideStepsExample.setVisibility(View.GONE);
            binding.groundingExDoItAgainBtn.setVisibility(View.VISIBLE);
            binding.groundingExDoneBtn.setVisibility(View.VISIBLE);
            binding.groundingExDoItAgainBtn.startAnimation(fadeIn);
            dotLottieAnimation.setVisibility(View.GONE);
            db.recordExerciseCompletion("Grounding", new DataBase.CompletionCallback() {
                @Override
                public void onSuccess(Integer newCount) {
                    Log.d("DB", "Exercise completed successfully. New count: " + newCount);
                    Snackbar.make( binding.getRoot()," Grounding Exercise completed successfully!", Snackbar.LENGTH_SHORT).show();
                    db.checkIfExerciseExistsInAnyGoal("Grounding",new DataBase.OnExerciseCheckListener(){

                                @Override
                                public void onExerciseFound(String exerciseName, String goalTitle) {
                                    Snackbar.make(binding.getRoot(), "your Goal was Updated", Snackbar.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onExerciseNotFound(String exerciseName) {

                                }

                                @Override
                                public void onError(String errorMessage) {

                                }
                            }
                    );
                }

                @Override
                public void onFailure(DatabaseError error) {
                    Log.e("DB", "Failed to record exercise completion: " + error.getMessage());
                }
            });

        }
        else{
            switch (currentIndex){
                case 0:
                    changeAnimation("blueeyes.lottie", true);
                    break;
                case 1 :
                    changeAnimation("bluetouch.lottie", true);
                    break;
                case 2 :
                    changeAnimation("blueear.lottie", true);
                    break;
                case 3:
                    changeAnimation("bluenose.json", true);
                    break;
                case 4:
                    changeAnimation("bluemouth.lottie", true);
                    break;
                default:
            }

            binding.groundingGuideStepsExample.setText(guideStepsExampleList[currentIndex]);
        }
        binding.groundingGuideStepsExample.startAnimation(fadeIn);
        Log.d("currentIndex", "currentIndex = "+ String.valueOf(currentIndex));
        currentIndex++;
    }

    /**
     *
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        dotLottieAnimation.setVisibility(View.GONE);
        dotLottieAnimation.clearAnimation();
        Log.d("GroundingExerciseFragment", "onDestroyView: Fragment destroyed and resources cleaned up.");
    }
}