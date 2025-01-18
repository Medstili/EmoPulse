package com.medstili.emopulse.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.medstili.emopulse.Components.CustomDialog;
import com.medstili.emopulse.R;

public class ContactUsFragment extends Fragment {
    MaterialButton submit;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        submit= view.findViewById(R.id.contactSubmitBtn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                customDialog.rightBtn.setVisibility(View.GONE);
                customDialog.dialogImg.setVisibility(View.VISIBLE);
                customDialog.leftBtn.setBackground(getResources().getDrawable(R.drawable.dialog_cancel_btn_bg));
                customDialog.leftBtn.setTextColor(getResources().getColor(R.color.white));
            }
        });

        return view;
    }
}