package com.medstili.emopulse.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseError;
import com.medstili.emopulse.Components.CustomDialog;
import com.medstili.emopulse.DataBase.DataBase;
import com.medstili.emopulse.R;

import java.util.Objects;

public class ContactUsFragment extends Fragment {
    MaterialButton submit;
    DataBase db ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        submit= view.findViewById(R.id.contactSubmitBtn);
        db = DataBase.getInstance();
        submit.setOnClickListener(view1 -> {
            String message = Objects.requireNonNull(((TextInputEditText) view.findViewById(R.id.contactMessage)).getText()).toString();
            String email = Objects.requireNonNull(((TextInputEditText) view.findViewById(R.id.contactEmail)).getText()).toString();
            if (!sendMessageVerification(message, email)) return;
            db.contactUsMessage(email, message, new DataBase.contactUsCallback() {
                @Override
                public void onSuccess() {
                    ((TextInputEditText) view.findViewById(R.id.contactMessage)).setText("");
                    ((TextInputEditText) view.findViewById(R.id.contactEmail)).setText("");
                    CustomDialog customDialog = new CustomDialog(requireActivity());

                    customDialog.showDialog(
                            "title",
                            "Message Sent Successfully",
                            "",
                            null,
                            "OK",
                            null
                    );
                    customDialog.title.setVisibility(View.GONE);
                    customDialog.message.setVisibility(View.VISIBLE);
                    customDialog.rightBtn.setVisibility(View.GONE);
                    customDialog.dialogImg.setVisibility(View.VISIBLE);
                    customDialog.leftBtn.setBackground(getResources().getDrawable(R.drawable.dialog_cancel_btn_bg));
                    customDialog.leftBtn.setTextColor(getResources().getColor(R.color.white));
                }

                @Override
                public void onFailure(DatabaseError error) {
                    Log.e("DB", "Failed to send message: " + error.getMessage());
                    Snackbar.make(requireView(), "Failed to send message: " + error.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            });



        });

        return view;
    }

    public  boolean  sendMessageVerification(String msg, String email) {

        if (TextUtils.isEmpty(msg) &&  TextUtils.isEmpty(email)) {
            Snackbar.make(requireView(), "PLease fill the Fields", Snackbar.LENGTH_SHORT).show();
            ((TextInputEditText) requireView().findViewById(R.id.contactMessage)).setError("Message cannot be empty");
            ((TextInputEditText) requireView().findViewById(R.id.contactEmail)).setError("Email cannot be empty");
            return false;
        }
        else if (TextUtils.isEmpty(email)){
            Snackbar.make( requireView(), "Email cannot be empty", Snackbar.LENGTH_SHORT).show();
            ((TextInputEditText) requireView().findViewById(R.id.contactEmail)).setError("Email cannot be empty");
            return false;
        }
        else if (msg.isEmpty()) {
            Snackbar.make(requireView(), "Message cannot be empty", Snackbar.LENGTH_SHORT).show();
            ((TextInputEditText) requireView().findViewById(R.id.contactMessage)).setError("Message cannot be empty");
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snackbar.make(requireView(), "Please enter a valid email address", Snackbar.LENGTH_SHORT).show();
            ((TextInputEditText) requireView().findViewById(R.id.contactEmail)).setError("Invalid email format");
            return false;
        }


        return true;
    }
}