package com.medstili.emopulse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.lottiefiles.dotlottie.core.model.Config;
import com.lottiefiles.dotlottie.core.util.DotLottieSource;
import com.lottiefiles.dotlottie.core.widget.DotLottieAnimation;
import com.dotlottie.dlplayer.Mode;
import com.medstili.emopulse.Auth.Authentication;
import com.medstili.emopulse.databinding.ActivityEmailConfirmationBinding;



public class EmailConfirmation extends AppCompatActivity {

    ActivityEmailConfirmationBinding binding ;
    private Handler handler = new Handler();
    private Runnable checkVerifiedRunnable;
    // Polling interval (in milliseconds)
    private static final long CHECK_INTERVAL_MS = 5000;
    // Delay after check-mark animation (in ms) before navigating to MainActivity
    private static final long POST_CHECKMARK_DELAY_MS = 2000;
    private Authentication authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


    binding = ActivityEmailConfirmationBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    authManager = Authentication.getInstance();



       String email = getIntent().getStringExtra("email");
        if (TextUtils.isEmpty(email)) {
            email = "(unknown)";
        }
        binding.emailConfirmationTitle.setText("A verification link has been sent to:\n" + email + "\n\nPlease click the link to verify your account.");

        binding.resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                senEmailVerification();
                startCounting();
            }
        });


        startCheckingEmailVerified();
    }


    /**
     * Sets up a Runnable that every CHECK_INTERVAL_MS reloads user and checks isEmailVerified().
     */

    private void startCheckingEmailVerified() {
        checkVerifiedRunnable = new Runnable() {
            @Override
            public void run() {
                authManager.checkEmailVerified(
                        new Authentication.VerificationCallback() {
                            @Override
                            public void onVerified() {
                                showCheckmarkAndFinish();
                            }
                            @Override
                            public void onNotVerified() {
                                // still not verified → try again later
                                handler.postDelayed(
                                        checkVerifiedRunnable, CHECK_INTERVAL_MS);
                            }
                        });
            }
        };
        /// First check after a short delay (e.g. immediately or after 1s)
        handler.postDelayed(checkVerifiedRunnable, 1000);
    }
    /**
     * Called once we detect isEmailVerified() == true.
     * Swaps the Lottie animation to CheckMark.lottie, plays it, and after 2s navigates on.
     */
    private void showCheckmarkAndFinish() {
        // 1) Prevent further polling
        handler.removeCallbacks(checkVerifiedRunnable);
        DotLottieAnimation dotView = binding.dotLottieAnimationView;

        // 3) Pause current animation if it’s still playing
        if (dotView.isPlaying()) {
            dotView.pause();
        }

        Config config = new Config.Builder()
                .autoplay(true)
                .speed(0.5f)
                .loop(true)
                 .source(new DotLottieSource.Asset("checkmark.lottie"))
                .useFrameInterpolation(true)
                .playMode(Mode.FORWARD)
                .build();

// Load the animation with the config
        dotView.load(config);


        // 3) Disable the “Resend” button (optional)
        binding.resendBtn.setEnabled(false);
        binding.resendBtn.setVisibility(View.GONE);
        binding.timer.setVisibility(View.GONE);
        binding.emailConfirmationTitle.setText("Your account has been verified!");

        // 4) After a short delay, navigate to MainActivity
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(EmailConfirmation.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, POST_CHECKMARK_DELAY_MS);
    }

    private void senEmailVerification(){
        authManager.resendVerificationEmail(
                new Authentication.ResendCallback() {
                    @Override public void onResent() {
                        Toast.makeText(EmailConfirmation.this,
                                "Verification email resent. Check your inbox.",
                                Toast.LENGTH_LONG).show();
                    }
                    @Override public void onFailure(String error) {
                        Toast.makeText(EmailConfirmation.this,
                                error, Toast.LENGTH_LONG).show();
                    }
                });
        binding.resendBtn.setEnabled(false); // Disable the button while counting
        binding.timer.setVisibility(View.VISIBLE);
    }

    private void startCounting(){
        binding.resendBtn.setEnabled(false);
        binding.timer.setVisibility(View.VISIBLE);

        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Update the timer TextView with the remaining time
                binding.timer.setText("Resend in: " + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                // Enable the resend button and hide the timer
                binding.resendBtn.setEnabled(true);
                binding.timer.setVisibility(View.GONE);
                binding.timer.setText(""); // Clear the timer text
            }
        }.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startCounting(); // Start the timer when the activity starts or becomes visible
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Always remove callbacks to avoid leaks
        handler.removeCallbacksAndMessages(null);
    }


}