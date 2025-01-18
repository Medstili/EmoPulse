package com.medstili.emopulse.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.medstili.emopulse.Components.CustomDialog;
import com.medstili.emopulse.activities.MainActivity;
import com.medstili.emopulse.R;
import com.medstili.emopulse.databinding.FragmentSettings2Binding;
import com.medstili.emopulse.activities.signIn;

public class settings2Fragment extends Fragment {

    FragmentSettings2Binding binding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        binding = FragmentSettings2Binding.inflate(inflater, container, false);
        mainActivity.hideBottomBarWhileScrollingDown(binding.settingsScrollView);
        binding.accountSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_settingsFragment_to_accountSettingsNavigation);
            }
        });
        binding.dataPrivacyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_settingsFragment_to_dataPrivacyFragment);
            }
        });
        binding.faqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_settingsFragment_to_faqsFragment);
            }
        });
        binding.contactUsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_settingsFragment_to_contactUsFragment);
            }
        });
//        handling the dialog popup
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                logDialog.show();
                CustomDialog customDialog = new CustomDialog(requireActivity());
                customDialog.showDialog(
                        "Log Out",
                        "Are You Sure You Want To Log Out ?",
                        "Log Out",
                        v -> {
                            Intent intent = new Intent(requireActivity(), signIn.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            requireActivity().finish();
                        },
                        "Cancel",
                        null);


            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(binding.settingstitlecontainer, (v, insets) -> {
            Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());

            // Apply padding to the top for the status bar
            v.setPadding(0, statusBarInsets.top, 0, 0);

            return insets; // Return insets to keep consuming them
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}