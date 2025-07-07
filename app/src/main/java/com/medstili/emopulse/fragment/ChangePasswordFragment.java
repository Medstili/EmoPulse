package com.medstili.emopulse.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.medstili.emopulse.Components.CustomDialog;
import com.medstili.emopulse.R;
import com.medstili.emopulse.Auth.Authentication;

import java.util.Objects;

public class ChangePasswordFragment extends Fragment {

    private TextInputEditText etNewPassword, etConfirmPassword;
    private Authentication authManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        authManager = Authentication.getInstance();
        etNewPassword    = view.findViewById(R.id.newPasswordInput);      // give your TextInputEditText IDs
        etConfirmPassword= view.findViewById(R.id.confirmPasswordInput);
        MaterialButton resetBtn = view.findViewById(R.id.resetBtn);

        resetBtn.setOnClickListener(v -> attemptPasswordChange());

        return view;
    }

    private void attemptPasswordChange() {
        String newPass     = Objects.requireNonNull(etNewPassword.getText()).toString().trim();
        String confirmPass = Objects.requireNonNull(etConfirmPassword.getText()).toString().trim();

        // 1) Basic validation:
        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            Snackbar.make(requireView(), "Please fill in both fields", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!newPass.equals(confirmPass)) {
            Snackbar.make(requireView(), "Passwords do not match", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (newPass.length() < 8) {
            Snackbar.make(requireView(), "Password must be at least 8 characters", Snackbar.LENGTH_SHORT).show();
            return;
        }

        authManager.updatePassword( newPass, new Authentication.UpdatePasswordCallBack() {

            @Override
            public void onSuccess(String SuccessMsg) {
                 Snackbar.make(requireView(), SuccessMsg, Snackbar.LENGTH_LONG).show();
                 etNewPassword.setText("");
                 etConfirmPassword.setText("");
                Navigation.findNavController(requireView()).popBackStack();
            }


            @Override
            public void onFailure(String ErrorMsg) {
                Snackbar.make(requireView(), ErrorMsg, Snackbar.LENGTH_LONG).show();
                etNewPassword.setText("");
                etConfirmPassword.setText("");
            }


            @Override
            public void onReAuthenticate() {

                // 2) Ask for current password so we can re-Auth if needed:
                CustomDialog pwdDialog = new CustomDialog(requireActivity());
                pwdDialog.showPasswordDialog(
                        "Enter your current password",
                        "Continue",
                        view -> {
                            String currentPass = pwdDialog.editTextPassword.getText().toString().trim();
                            if (currentPass.isEmpty()) {
                                Snackbar.make(requireView(), "Current password required", Snackbar.LENGTH_SHORT).show();
                                return;
                            }

                            FirebaseUser user = authManager.getCurrentUser();
                            if (user == null) {
                                Snackbar.make(requireView(), "User not found", Snackbar.LENGTH_SHORT).show();
                                return;
                            }
                            AuthCredential credential = authManager.getCredential(currentPass, null);
                            if (credential == null) {
                                Snackbar.make(requireView(), "Invalid current password", Snackbar.LENGTH_SHORT).show();
                                return;
                            }
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(reauthTask -> {
                                        if (reauthTask.isSuccessful()) {
                                            System.out.println("User re-authenticated successfully! Retrying password update...");
                                            // Now, retry the password update
                                            authManager.updatePassword(newPass, new Authentication.UpdatePasswordCallBack() {
                                                @Override
                                                public void onSuccess(String SuccessMsg) {
                                                    Snackbar.make(requireView(), SuccessMsg, Snackbar.LENGTH_LONG).show();
                                                    etNewPassword.setText("");
                                                    etConfirmPassword.setText("");
                                                    // Navigate back and remove the current fragment from the stack
                                                    Navigation.findNavController(requireView()).popBackStack();
                                                }

                                                @Override
                                                public void onFailure(String ErrorMsg) {
                                                    Snackbar.make(requireView(), ErrorMsg, Snackbar.LENGTH_LONG).show();
                                                    etNewPassword.setText("");
                                                    etConfirmPassword.setText("");
                                                }

                                                @Override
                                                public void onReAuthenticate() {

                                                }
                                            });
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
        

    }

}
