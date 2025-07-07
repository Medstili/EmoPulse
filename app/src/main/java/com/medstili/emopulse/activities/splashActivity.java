package com.medstili.emopulse.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.medstili.emopulse.R;
import com.medstili.emopulse.Auth.Authentication;

import java.util.Locale;

@SuppressLint("CustomSplashScreen")
public class splashActivity extends AppCompatActivity {
    Authentication authManager ;

    /**
     *
     */
    @Override
    protected void onStart() {
        super.onStart();
        authManager.initGoogleSignIn(this);
        if (authManager.isUserSignedIn()) {
            authManager.checkEmailVerified(
                    new Authentication.VerificationCallback() {
                        @Override
                        public void onVerified() {
                            Intent intent = new Intent(splashActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onNotVerified() {
                            Intent intent = new Intent(splashActivity.this, signUp.class);
                            startActivity(intent);
                            finish();
                        }
                    }
            );
        }
        else{
            Intent intent = new Intent(splashActivity.this, signIn.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();


//        Authentication.getInstance().initGoogleSignIn(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | // Hides navigation bar
                        View.SYSTEM_UI_FLAG_FULLSCREEN | // Hides status bar
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Keeps it immersive
        );
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        authManager = Authentication.getInstance();

        int apiLevel = android.os.Build.VERSION.SDK_INT;
        Log.d("API_LEVEL", "Current API level: " + apiLevel);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        new Handler().postDelayed(() -> {
//            if (authManager.isUserSignedIn()) {
//                authManager.checkEmailVerified(
//                        new Authentication.VerificationCallback() {
//                            @Override
//                            public void onVerified() {
//                                Intent intent = new Intent(splashActivity.this, MainActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//
//                            @Override
//                            public void onNotVerified() {
//                                Intent intent = new Intent(splashActivity.this, signUp.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                        }
//                        );
//            }
//            else{
//                Intent intent = new Intent(splashActivity.this, signIn.class);
//                startActivity(intent);
//                finish();
//            }
//
//
//        }, 2000);
    }

    private void loadLanguage() {
        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        if (sharedPref == null) {
            Log.e("splashActivity", "SharedPreferences is null");
            return;
        }
        String language = sharedPref.getString("My_Lang", "");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}