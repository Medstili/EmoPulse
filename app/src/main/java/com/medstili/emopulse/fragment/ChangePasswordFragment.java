package com.medstili.emopulse.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.medstili.emopulse.Components.CustomDialog;
import com.medstili.emopulse.R;


public class ChangePasswordFragment extends Fragment {

    MaterialButton resetBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        resetBtn= view.findViewById(R.id.resetBtn);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialog customDialog = new CustomDialog(requireActivity());
                customDialog.showDialog(
                        "Reset Password",
                        "Are You Sure You Want To Reset Your Password ?",
                        "Reset",
                        v->{
                            Toast.makeText(requireActivity(), "Password Reset Successfully", Toast.LENGTH_SHORT).show();
                        },
                        "Cancel",
                       null
                );
            }
        });
        return view;
    }
}