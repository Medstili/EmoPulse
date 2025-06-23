package com.medstili.emopulse.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.medstili.emopulse.R;
import com.medstili.emopulse.User.User;
import com.medstili.emopulse.auth.Authentication;

import java.util.Objects;


public class signUp extends AppCompatActivity {
    TextView logInLink ;
    MaterialButton googleLoginButton;
    MaterialButton nextBtn;
    EditText emailInput, passwordInput, confirmPasswordInput;
    Authentication authManager;

    @Override
    public void onStart() {
        super.onStart();
        if (authManager.isUserSignedIn()) {
            navigateToActivity(this, MainActivity.class, null);
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
        authManager = Authentication.getInstance();


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

        nextBtn.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           if (!fieldsVerification(emailInput.getText().toString(), passwordInput.getText().toString())) {
                                               emailInput.setError("Field can't be empty");
                                               passwordInput.setError("Field can't be empty");
                                               confirmPasswordInput.setError("Field can't be empty");
                                               Toast.makeText(signUp.this, "Please fill in the fields", Toast.LENGTH_SHORT).show();
                                           } else if (!emailVerification(emailInput.getText().toString())) {
                                               emailInput.setError("Enter a valid email");
                                               Toast.makeText(signUp.this, "Enter a valid email", Toast.LENGTH_SHORT).show();
                                           } else if (!passwordVerification(passwordInput.getText().toString())) {
                                               passwordInput.setError("Password must be at least 8 characters");
                                               Toast.makeText(signUp.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                                           } else if (!passwordConfirmationVerification(passwordInput.getText().toString(), confirmPasswordInput.getText().toString())) {
                                               confirmPasswordInput.setError("password must match");
                                               Toast.makeText(signUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                                           } else {
//                    try {
//                        addUser();
//                        mAuth.createUserWithEmailAndPassword(emailInput.getText().toString(), passwordInput.getText().toString())
//                                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<AuthResult> task) {
//                                        if (task.isSuccessful()) {
//                                            // FirebaseUser user = mAuth.getCurrentUser(); // User object is available if needed
//                                            navigateToActivity(signUp.this, EmailConfirmation.class); // Example usage
//                                            Log.d("SignUp Success:", "createUserWithEmail:success");
//
//                                        } else {
//                                            Log.w("SignUp Error:", "createUserWithEmail:failure", task.getException());
//                                            Toast.makeText(signUp.this, "Authentication failed.",
//                                                    Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                });

                                               signUpAuth(emailInput.getText().toString(), passwordInput.getText().toString());
                                           }
                                       }
        });






    }

    private  boolean passwordConfirmationVerification(String password, String confirmPassword){
        return password.equals(confirmPassword);
    }
    private  boolean emailVerification(String email){
        return !email.trim().isEmpty() && email.contains("@") ;
    }
    private boolean passwordVerification(String password){
        return !password.trim().isEmpty() && password.length() >= 8;
    }
    private boolean fieldsVerification(String email, String password){
        return !email.isEmpty() && !password.isEmpty();
    }
    private void navigateToActivity(Activity fromActivity, Class<? extends Activity> toActivityClass, String email) { // made email optional by checking if it's null
        Intent intent = new Intent(fromActivity, toActivityClass);
        if (email != null){
            intent.putExtra("email", email);
        }
        startActivity(intent);
        fromActivity.finish();
    }
    private void signUpAuth(String email, String password){
        authManager.signUpWithEmail(email, password, new Authentication.SignUpCallback() {
            @Override
            public void onSuccess() {
                // verification email sent — navigate to EmailConfirmation:
                runOnUiThread(() -> {
                    Toast.makeText(signUp.this,
                            "Verification email sent. Check your inbox.",
                            Toast.LENGTH_LONG).show();
                    navigateToActivity(signUp.this, EmailConfirmation.class, email);
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(signUp.this, errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

}