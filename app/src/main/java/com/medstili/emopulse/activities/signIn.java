package com.medstili.emopulse.activities;

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

import com.google.firebase.auth.FirebaseUser;
import com.medstili.emopulse.Components.CustomDialog;
import com.medstili.emopulse.R;
import com.medstili.emopulse.Auth.Authentication;


public class signIn extends AppCompatActivity {
// variables
    TextView signUpLink, forgotPasswordLink;
    MaterialButton googleLoginButton;
    MaterialButton loginBtn;
    EditText emailInput, passwordInput;
    Authentication authManager;
//    MainActivity mainActivity = new MainActivity();
//

    @Override
    public void onStart() {
        super.onStart();
        authManager = Authentication.getInstance();
        if (authManager.isUserSignedIn()) {
            MainActivity.navigateToActivity(this, MainActivity.class);
        }

    }
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
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

 //    finding views
            signUpLink = findViewById(R.id.signup_link);
            googleLoginButton = findViewById(R.id.google_login_button);
            loginBtn = findViewById(R.id.login_btn);
            emailInput=findViewById(R.id.email_input);
            passwordInput=findViewById(R.id.password_input);
            forgotPasswordLink = findViewById(R.id.forgot_password_link);



// making a portion of text clickable by using spannableString
        SpannableString spanAble = new SpannableString("Not Registered Yet!  Sign Up");
        String text = "Not Registered Yet! Sign Up";
        signUpLink.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));// For API 24 and above

        forgotPasswordLink.setOnClickListener(v -> {

            CustomDialog emailDialog = new CustomDialog(signIn.this);
            emailDialog.showEmailDialog(
                    "Please enter your email address to reset your password.",
                    "Send",
                    v1 -> {
                        emailInput.setHint("Enter your email");
                        String email = emailDialog.editTextEmail.getText().toString().trim();
                            if (!email.isEmpty()) {
                                authManager.sendPasswordResetEmail(email, new Authentication.ResetPasswordCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(signIn.this, "Password reset link sent to your email.", Toast.LENGTH_SHORT).show();
                                        emailDialog.dialog.dismiss();
                                    }
                                    @Override
                                    public void onFailure(String errorMessage) {
                                        Toast.makeText(signIn.this, errorMessage, Toast.LENGTH_LONG).show();
                                        emailDialog.dialog.dismiss();

                                    }
                                });
                            } else {
                                Toast.makeText(signIn.this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
                            }
                    },
                    "cancel",
                    v2-> emailDialog.dialog.dismiss()
                    );

            Toast.makeText(signIn.this, "Forgot Password Clicked", Toast.LENGTH_SHORT).show();



        });

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {

                MainActivity.navigateToActivity(signIn.this, signUp.class);
            }
        };
        spanAble.setSpan(clickableSpan, 20, 28, Spanned .SPAN_EXCLUSIVE_EXCLUSIVE);
        signUpLink.setText(spanAble);
        signUpLink.setMovementMethod(LinkMovementMethod.getInstance());
        googleLoginButton.setOnClickListener(view -> {
            WebViewBottomSheet webViewBottomSHeet=WebViewBottomSheet.newInstance(("https://accounts.google.com/ServiceLogin"));
            webViewBottomSHeet.show(getSupportFragmentManager(),"WebViewBottomSheet");
        });
        loginBtn.setOnClickListener(view -> {
            // handling the inputs
          if(!emailInput.getText().toString().isEmpty() && !passwordInput.getText().toString().isEmpty()){

              signInAuth(emailInput.getText().toString(), passwordInput.getText().toString());

          }else{

              Toast.makeText(signIn.this, "Please fill in the fields", Toast.LENGTH_SHORT).show();

          }

        });




    }

    private void signInAuth(String email, String password){
    authManager.signInWithEmail(email, password, new Authentication.SignInCallback() {
        @Override
        public void onSuccess(FirebaseUser user) {
            Toast.makeText(signIn.this, "Login successful!", Toast.LENGTH_SHORT).show();

            MainActivity.navigateToActivity( signIn.this, MainActivity.class);
        }
        @Override
        public void onFailure(String errorMessage) {
            Toast.makeText(signIn.this, errorMessage, Toast.LENGTH_LONG).show();
        }
    });
}
}