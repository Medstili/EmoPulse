package com.medstili.emopulse.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.medstili.emopulse.Components.CustomDialog;
import com.medstili.emopulse.R;
import com.medstili.emopulse.activities.MainActivity;
import com.medstili.emopulse.databinding.FragmentAccountSettingsBinding;
import com.medstili.emopulse.activities.signUp;


public class AccountSettingsFragment extends Fragment {
    FragmentAccountSettingsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}