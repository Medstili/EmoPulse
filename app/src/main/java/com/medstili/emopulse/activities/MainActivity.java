package com.medstili.emopulse.activities;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.medstili.emopulse.R;
import com.medstili.emopulse.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    public NavHostFragment navHostFragment;
    public NavController navController;
    public ViewGroup.MarginLayoutParams layoutParams;
    int totalHeight, appBarHeight, statusBarHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        getWindow().setSharedElementsUseOverlay(false);
        binding.appbar.setPadding(0, getStatusBarHeight(), 0, 0);
        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars());
        windowInsetsController.setAppearanceLightStatusBars(false);
        // Configure the behavior of the hidden system bars.
        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        statusBarHeight = getStatusBarHeight();
        appBarHeight = getAppBarHeight();
        totalHeight = statusBarHeight + appBarHeight;
        layoutParams = (ViewGroup.MarginLayoutParams) binding.navHostFragment.getLayoutParams();
//        layoutParams.topMargin = totalHeight;
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
        binding.chatButton.setOnClickListener(view -> {
                layoutParams.topMargin=totalHeight;
                binding.navHostFragment.setLayoutParams(layoutParams);
                navController.navigate(R.id.chatFragment);
                binding.appbar.setVisibility(View.VISIBLE);
                binding.toolbarContent.setVisibility(View.VISIBLE);
                hideBottomBarAndFab();
        });
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                    if (destination.getId() == R.id.dashboardFragment || destination.getId() == R.id.goalsFragment || destination.getId() == R.id.exercicesFragment||  destination.getId()==R.id.settingsFragment) {
//                        layoutParams.topMargin=0;
//                        binding.navHostFragment.setLayoutParams(layoutParams);
                        showBottomBarAndFab();
                        binding.appbar.setVisibility(View.GONE);
                        animateNavHostFragmentMargin(0);
                    }
                    else{
//                        layoutParams.topMargin = totalHeight;
                        animateNavHostFragmentMargin(totalHeight);
//                        binding.navHostFragment.setLayoutParams(layoutParams);
                        hideBottomBarAndFab();
                        binding.appbar.setVisibility(View.VISIBLE);
//                        binding.appbar.requestLayout();
                    };
                    if (destination.getId()== R.id.contactUsFragment) {
                        binding.toolbar.setTitle("Contact Us");
                    }
                    else if (destination.getId()== R.id.dataPrivacyFragment) {
                        binding.toolbar.setTitle("Data Privacy");
                    }
                    else if (destination.getId() == R.id.changePasswordFragment) {
                        binding.toolbar.setTitle("Change Password");
                    }
                    else if(destination.getId() == R.id.accountSettingsFragment) {
                        binding.toolbar.setTitle("Account Settings");
                    }
                    else if(destination.getId() == R.id.faqsFragment) {
                        binding.toolbar.setTitle("FAQs");
                    }
                    else if(destination.getId() == R.id.breathingFragment) {
                        binding.toolbar.setTitle(null);
                        binding.toolbarContent.setVisibility(View.GONE);
                    }
                    else if (destination.getId() == R.id.breathingExerciseFragment) {
                        binding.toolbar.setTitle(null);
                        binding.toolbarContent.setVisibility(View.GONE);
                    }
                    binding.toolbar.setNavigationOnClickListener(view -> {
                            navController.navigateUp();
                            binding.toolbarContent.setVisibility(View.GONE);
                        });
        });
        Glide.with(this).load(R.drawable.lellyavatar)  // The drawable or image URL
                                 .apply(RequestOptions.circleCropTransform())  // Apply circular transformation
                                    .into(binding.lellyAvatar);  // Set the image into the ImageView


    }

    public void hideBottomBarAndFab() {
        binding.bottomBar.setVisibility(View.GONE);
        binding.chatButton.setVisibility(View.GONE);

    }
    public void showBottomBarAndFab() {
        binding.bottomBar.setVisibility(View.VISIBLE);
        binding.chatButton.setVisibility(View.VISIBLE);
    }
    public void hideBottomBarWhileScrollingDown(@NonNull ScrollView scrollView){
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY){
                if (scrollY > oldScrollY) {
                    binding.bottomBar.animate().translationY(binding.bottomBar.getHeight());
                    binding.chatButton.animate().translationY(binding.bottomBar.getHeight());

//                    binding.appbar.setExpanded(false, true);
                }
                else {
                    binding.bottomBar.animate().translationY(0);
                    binding.chatButton.animate().translationY(0);
                    binding.appbar.animate().translationY(0).setDuration(300).start();

//                    binding.appbar.setExpanded(true, true);/

                }
            }
        });
    }
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    private int getAppBarHeight() {
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return 0;
    }
    private void animateAppBarPosition(float toY, int duration) {
        binding.appbar.animate()
                .translationY(toY) // Move the AppBar vertically
                .setDuration(duration) // Smooth animation duration in milliseconds
                .start(); // Start the animation
    }
    public void animateNavHostFragmentMargin(int targetMargin) {
        ValueAnimator animator = ValueAnimator.ofInt(layoutParams.topMargin, targetMargin);
        animator.setDuration(500); // Animation duration in milliseconds

        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            layoutParams.topMargin = animatedValue;
            binding.navHostFragment.setLayoutParams(layoutParams); // Apply updated margin
        });

        animator.start(); // Start the animation
    }
    @Override
//        handle the default phone back button too
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        LinearLayout toolbarContent = binding.toolbarContent;
        toolbarContent.setVisibility(View.GONE);
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack(); // Navigate to the previous fragment
            showBottomBarAndFab();
            binding.toolbarContent.setVisibility(View.GONE);
        }
        else {
            super.onBackPressed(); // Exit the activity
        }
    }
}