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

public class signIn extends AppCompatActivity {
// variables
    TextView signUpLink;
    MaterialButton googleLoginButton;
    MaterialButton loginBtn;
    EditText emailInput, passwordInput;
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

// making a portion of text clickable by using spannableString
        SpannableString spannAble = new SpannableString("Not Registered Yet!  Sign Up");
        String text = "Not Registered Yet! Sign Up";
        signUpLink.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));// For API 24 and above

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(signIn.this, signUp.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        };
        spannAble.setSpan(clickableSpan, 20, 28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        signUpLink.setText(spannAble);
        signUpLink.setMovementMethod(LinkMovementMethod.getInstance());

// google web view bottom sheet
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebViewBottomSheet webViewBottomSHeet=WebViewBottomSheet.newINstance(("https://accounts.google.com/ServiceLogin"));
                webViewBottomSHeet.show(getSupportFragmentManager(),"WebViewBottomSheet");
            }
        });
// login button event handler (going to MainActivity )
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handling the inputs
              if(!emailInput.getText().toString().isEmpty() && !passwordInput.getText().toString().isEmpty()){
                  Intent intent = new Intent(signIn.this, MainActivity.class);
                  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                  startActivity(intent);
                  finish();

              }else{

                  Toast.makeText(signIn.this, "Please fill in the fileds", Toast.LENGTH_SHORT).show();

              }

            }
        });




    }
    
}