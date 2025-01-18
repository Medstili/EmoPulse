package com.medstili.emopulse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.medstili.emopulse.R;


public class signUp extends AppCompatActivity {
    TextView logInLink ;
    MaterialButton googleLoginButton;
    MaterialButton nextBtn;
    EditText emailInput, passwordInput, confirmPasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


// finding the views
        logInLink = findViewById(R.id.login_link);
        googleLoginButton = findViewById(R.id.google_login_button);
        nextBtn= findViewById(R.id.next_btn);
        emailInput =findViewById(R.id.email_input);
        passwordInput =findViewById(R.id.password_input);
        confirmPasswordInput =findViewById(R.id.confirm_password_input);

// making a portion of text clickable by using spannableString
        SpannableString spanAble = new SpannableString("Already Have An Account ? Log In");
        String text = "Already Have An Account ? Log In";

        logInLink.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)); // For API 24 and above

        ClickableSpan clickAbleSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View v) {
                Intent intent = new Intent(signUp.this, signIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        };
//        make the span clickable
        spanAble.setSpan(clickAbleSpan, 26, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        logInLink.setText(spanAble);
        logInLink.setMovementMethod(LinkMovementMethod.getInstance());

        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebViewBottomSheet webViewBottomSHeet=WebViewBottomSheet.newINstance(("https://accounts.google.com/ServiceLogin"));
                webViewBottomSHeet.show(getSupportFragmentManager(),"WebViewBottomSheet");
            }
        });

//      next button event handler (going to EmailConfirmation )
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //        handling the inputes
                if (!emailInput.getText().toString().isEmpty() && !passwordInput.getText().toString().isEmpty() && !confirmPasswordInput.getText().toString().isEmpty()){
//                   check for passwords confirmation and go to EmailConfirmation activity if they match
                    if (passwordInput.getText().toString().equals(confirmPasswordInput.getText().toString())){;
                    Intent intent = new Intent(signUp.this, EmailConfirmation.class);
                    startActivity(intent);
                    finish();
                    }else {
                        Toast.makeText(signUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                }else {

                    Toast.makeText(signUp.this, "Please fill in the fileds", Toast.LENGTH_SHORT).show();
                }

            }
        });




    }
}