package com.medstili.emopulse.activities;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.medstili.emopulse.R;
import com.medstili.emopulse.auth.Authentication;
import com.medstili.emopulse.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    public NavHostFragment navHostFragment;
    public NavController navController;
    public ViewGroup.MarginLayoutParams layoutParams;
    int totalHeight, appBarHeight, statusBarHeight;
    Authentication authManager;

    @Override
    protected void onStart() {
        super.onStart();
        authManager = Authentication.getInstance();
        if ( !authManager.isUserSignedIn()) {
            navigateToActivity(MainActivity.this, signIn.class);
        }

        authManager.checkEmailVerified(
                new Authentication.VerificationCallback() {
                    @Override
                    public void onVerified() {
                        // Already verified → nothing to do
                        Log.i("email", "email verified");
                    }
                    @Override
                    public void onNotVerified() {
                        Log.i("email", "email not verified");

                        authManager.signOut();
                        navigateToActivity(MainActivity.this, signIn.class);
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
                    if (destination.getId() == R.id.dashboardFragment || destination.getId() == R.id.goalsNavigation || destination.getId() == R.id.exercicesFragment||  destination.getId()==R.id.settingsFragment) {
                        showBottomBarAndFab();
                        binding.appbar.setVisibility(View.GONE);
                        animateNavHostFragmentMargin(0);
                    }
                    else{
                        animateNavHostFragmentMargin(totalHeight);
                        hideBottomBarAndFab();
                        binding.appbar.setVisibility(View.VISIBLE);
                    };
                    if (destination.getId()== R.id.contactUsFragment) {
                        binding.toolbar.setTitle("Contact Us");
                    }

                    else if (destination.getId() == R.id.changePasswordFragment) {
                        binding.toolbar.setTitle("Change Password");
                    }

                    else if(destination.getId() == R.id.faqsFragment) {
                        binding.toolbar.setTitle("FAQs");
                    }
                    else if (destination.getId() == R.id.goalDetailsFragment){
                        binding.toolbar.setTitle("Goal Details");
                    }
//                    else if (destination.getId() == R.id.bodyScanExerciseFragment){
//                        binding.toolbar.setTitle(" Body Scan");
//                    }
                    binding.toolbar.setNavigationOnClickListener(view -> {
                            navController.navigateUp();
                            binding.toolbarContent.setVisibility(View.GONE);
                        });
        });
        Glide.with(this).load(R.drawable.lellyavatar)  // The drawable or image URL
                                 .apply(RequestOptions.circleCropTransform())  // Apply circular transformation
                                    .into(binding.lellyAvatar);  // Set the image into the ImageView


    }
    public void hideBottomBarWhileScrollingDownRecyclerView(@NonNull RecyclerView recyclerView){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    binding.bottomBar.animate().translationY(binding.bottomBar.getHeight());
                    binding.chatButton.animate().translationY(binding.bottomBar.getHeight());
                } else if (dy < 0) {

                     // Scrolling Up

                    binding.bottomBar.animate().translationY(0);
                    binding.chatButton.animate().translationY(0);                }
            }
        });
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
//                    binding.appbar.animate().translationY(0).setDuration(300).start();

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
    public void animateNavHostFragmentMargin(int targetMargin) {
        ValueAnimator animator = ValueAnimator.ofInt(layoutParams.topMargin, targetMargin);
        animator.setDuration(500); // Animation duration in milliseconds

        animator.addUpdateListener(animation -> {
            layoutParams.topMargin = (int) animation.getAnimatedValue();
            binding.navHostFragment.setLayoutParams(layoutParams); // Apply updated margin
        });

        animator.start(); // Start the animation
    }
    private void navigateToActivity(Activity fromActivity, Class<? extends Activity> toActivityClass) {
        Intent intent = new Intent(fromActivity, toActivityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        fromActivity.finish();
    }

    /**
     *  handle the default phone back button too
     */
    @Override
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