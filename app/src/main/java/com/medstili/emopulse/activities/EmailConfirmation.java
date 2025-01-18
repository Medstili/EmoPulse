package com.medstili.emopulse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.medstili.emopulse.R;
import com.medstili.emopulse.databinding.ActivityEmailConfirmationBinding;


public class EmailConfirmation extends AppCompatActivity {

    ActivityEmailConfirmationBinding binding ;
    boolean isOtpComplete = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


    binding = ActivityEmailConfirmationBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

        EditText[] otpInputs = {
                findViewById(R.id.otp_digit_1),
                findViewById(R.id.otp_digit_2),
                findViewById(R.id.otp_digit_3),
                findViewById(R.id.otp_digit_4)
        };

        for (int i = 0; i < otpInputs.length; i++) {
            final int index = i;
            otpInputs[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().isEmpty() && index < otpInputs.length - 1) {
                        otpInputs[index + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        binding.btnSubmitOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOtpComplete= true;
//            check if teh boxes is not empty
                for (EditText input : otpInputs) {
                    if (input.getText().toString().trim().isEmpty()) {
                        isOtpComplete = false;
                        break;
                    }
                };

                if (isOtpComplete){
                    Intent intent = new Intent(EmailConfirmation.this, Done.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(EmailConfirmation.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}