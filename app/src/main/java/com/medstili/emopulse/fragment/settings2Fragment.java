package com.medstili.emopulse.fragment;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.LinearLayout;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.medstili.emopulse.Components.CustomDialog;
import com.medstili.emopulse.activities.MainActivity;
import com.medstili.emopulse.R;
import com.medstili.emopulse.activities.signUp;
import com.medstili.emopulse.auth.Authentication;
import com.medstili.emopulse.databinding.FragmentSettings2Binding;
import com.medstili.emopulse.activities.signIn;

import java.util.Locale;
import java.util.Objects;

public class settings2Fragment extends Fragment {

    FragmentSettings2Binding binding;
    Authentication authManager;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        binding = FragmentSettings2Binding.inflate(inflater, container, false);
        mainActivity.hideBottomBarWhileScrollingDown(binding.settingsScrollView);
//        binding.accountSettingsBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
//                navController.navigate(R.id.action_settingsFragment_to_accountSettingsNavigation);
//            }
//        });

//        binding.dataPrivacyBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
//                navController.navigate(R.id.action_settingsFragment_to_dataPrivacyFragment);
//            }
//        });

        authManager = Authentication.getInstance();
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
//                            mAuth.signOut();
                            authManager.signOut();
                            Toast.makeText(requireActivity(), "Logged Out Successfully", Toast.LENGTH_SHORT).show();
                            goToTheNext();
                        },
                        "Cancel",
                        null);


            }
        });
        binding.deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialog customDialog = new CustomDialog(requireActivity());
                customDialog.showDialog(
                        "Delete Account",
                        "Are You Sure You Want To Delete Your Account ?",
                        "Delete",
                        v->{
                            authManager.deleteAccount(requireActivity(),
                                    new Authentication.DeleteAccountCallBack() {
                                        @Override
                                        public void onSuccess() {
                                            Toast.makeText(requireActivity(),"Account Deleted Successfully", Toast.LENGTH_SHORT).show();

                                        }

                                        @Override
                                        public void onFailure() {
                                            Toast.makeText(requireActivity(),"Error Deleting  Account", Toast.LENGTH_SHORT).show();

                                        }

                                        @Override
                                        public void onReAuthenticate() {

                                        }
                                    }
                            );
                        },
                        "Cancel",
                        null
                );
                customDialog.title.setTextColor(ContextCompat.getColor(requireActivity(), R.color.danger));
            }
        });
        binding.changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_settingsFragment_to_changePasswordFragment);
            }
        });
//        binding.deleteDataBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CustomDialog customDialog = new CustomDialog(requireActivity());
//                customDialog.showDialog(
//                        "Delete Data",
//                        "After Deleting The Data you Will Loose All Your Analytics Data and And Not Being Able To See Your old Messages  ",
//                        "Delete",
//                        v->{
//                            Toast.makeText(requireActivity(), "Data Deleted Successfully", Toast.LENGTH_SHORT).show();
//                            customDialog.dialog.dismiss();
//                        },
//                        "Cancel",
//                        null
//                );
//            }
//        });
        binding.deleteAccountBtn.setOnClickListener(v -> {
            CustomDialog confirm = new CustomDialog(requireActivity());
            confirm.showDialog(
                    "Delete Account",
                    "Are you sure you want to delete your account?",
                    "Delete",
                    view -> {
                        // First attempt to delete; will callback onReAuthenticate if needed
                        authManager.deleteAccount(requireActivity(), new Authentication.DeleteAccountCallBack() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(requireActivity(),
                                        "Account deleted successfully", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(requireActivity(),
                                        "Error deleting account", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onReAuthenticate() {
                                // We need to ask the user for their password again
                                CustomDialog pwdDialog = new CustomDialog(requireActivity());
                                pwdDialog.showPasswordDialog(
                                        "Re-enter your password to continue",
                                        "Confirm",
                                        pwdView -> {
                                            String currentPassword = pwdDialog.editTextPassword.getText().toString().trim();
                                            if (TextUtils.isEmpty(currentPassword)) {
                                                Toast.makeText(requireActivity(),
                                                        "Password cannot be empty", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            // Reauthenticate
                                            FirebaseUser user = authManager.getCurrentUser();
                                            if (user == null) return;
                                            AuthCredential cred = authManager.getCredential(currentPassword);
                                            user.reauthenticate(cred)
                                                    .addOnCompleteListener(reauthTask -> {
                                                        if (reauthTask.isSuccessful()) {
                                                            authManager.deleteAccount(requireActivity(), new Authentication.DeleteAccountCallBack() {
                                                                @Override public void onSuccess() {
                                                                    Toast.makeText(requireActivity(),
                                                                            "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                                                }
                                                                @Override public void onFailure() {
                                                                    Toast.makeText(requireActivity(),
                                                                            "Error deleting account after reauth", Toast.LENGTH_SHORT).show();
                                                                }
                                                                @Override public void onReAuthenticate() {
                                                                }
                                                            });
                                                        } else {
                                                            Toast.makeText(requireActivity(),
                                                                    "Re-authentication failed: " + Objects.requireNonNull(reauthTask.getException()).getMessage(),
                                                                    Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                        },
                                        "Cancel",
                                        cancelView -> {
                                            pwdDialog.dialog.dismiss();
                                        }
                                );
                            }
                        });
                    },
                    "Cancel",
                    cancelView -> {
                        confirm.dialog.dismiss();
                    }
            );
            confirm.title.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.danger)
            );
        });

        binding.exportDataBtn.setOnClickListener(new View.OnClickListener() {
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
        ViewCompat.setOnApplyWindowInsetsListener(binding.settingstitlecontainer, (v, insets) -> {
            Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());

            // Apply padding to the top for the status bar
            v.setPadding(0, statusBarInsets.top, 0, 0);

            return insets; // Return insets to keep consuming them
        });
        LinearLayout languageContainer = binding.languageContainer;

        binding.englishButton.setOnClickListener(v->{
            showLanguageChangeDialog("en","English");
        });
        binding.frenshButton.setOnClickListener(v->{
            showLanguageChangeDialog("fr","French");
        });
        binding.spanishButton.setOnClickListener(v->{
            showLanguageChangeDialog("es","Spanish");
        });
        binding.arabeButton.setOnClickListener(v-> {
            showLanguageChangeDialog("ar","Arabe");
        });

        return binding.getRoot();
    }

    private void changeLanguage(String lang) {
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("My_Lang", lang);
        editor.apply();

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        requireActivity().getBaseContext().getResources().updateConfiguration(config, requireActivity().getBaseContext().getResources().getDisplayMetrics());

        // Restart activity to apply changes
        Intent intent = new Intent(requireActivity(), requireActivity().getClass());
        startActivity(intent);
        requireActivity().finish();
    }

    private void showLanguageChangeDialog(String languageCode, String languageName) {
        CustomDialog customDialog = new CustomDialog(requireActivity());
        customDialog.showDialog(
                "Change Language",
                "Are you sure you want to change the app language to " + languageName + "?",
                "Yes", v1 -> { changeLanguage(languageCode); customDialog.dialog.dismiss(); },
                "No", v1 -> { customDialog.dialog.dismiss(); }
        );

        customDialog.message.setVisibility(View.VISIBLE);
    }

    private void goToTheNext(){
        Intent intent = new Intent(requireActivity(), signIn.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}