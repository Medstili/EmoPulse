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


public class DataPrivacyFragment extends Fragment {
        MaterialButton deleteBtn, exportBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data_privacy, container, false);

        deleteBtn= view.findViewById(R.id.deleteDataBtn);
        exportBtn= view.findViewById(R.id.ExportBtn);

//        handling buttons listener
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialog customDialog = new CustomDialog(requireActivity());
                customDialog.showDialog(
                        "Delete Data",
                        "After Deleting The Data you Will Loose All Your Analytics Data and And Not Being Able To See Your old Messages  ",
                        "Delete",
                        v->{
                            Toast.makeText(requireActivity(), "Data Deleted Successfully", Toast.LENGTH_SHORT).show();
                            customDialog.dialog.dismiss();
                        },
                        "Cancel",
                        null
                         );
            }
        });
        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialog customDialog = new CustomDialog(requireActivity());
                customDialog.showDialog(
                        "Export Data",
                        "You Will Export Data As Pdf File",
                        "Export",
                        v->{
                            Toast.makeText(requireActivity(), "Data Exported Successfully", Toast.LENGTH_SHORT).show();
                            customDialog.dialog.dismiss();
                        },
                        "Cancel",
                        v->{
                            customDialog.dialog.dismiss();
                        }
                );
            }
        });
        return view;
    }


}