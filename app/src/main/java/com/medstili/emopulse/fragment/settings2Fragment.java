package com.medstili.emopulse.fragment;



import android.app.DownloadManager;
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

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;

import com.google.firebase.auth.FirebaseUser;
import com.medstili.emopulse.Components.CustomDialog;
import com.medstili.emopulse.activities.MainActivity;
import com.medstili.emopulse.R;
import com.medstili.emopulse.Auth.Authentication;
import com.medstili.emopulse.databinding.FragmentSettings2Binding;
import com.medstili.emopulse.activities.signIn;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.net.Uri;
import android.os.Environment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
public class settings2Fragment extends Fragment {

    FragmentSettings2Binding binding;
    Authentication authManager;
    private static final String TAG = "ExportDataExecutor";
    private static final String FUNCTION_URL = "https://us-central1-emopulse-6a39a.cloudfunctions.net/exportData";
//    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1001;
    // Create an ExecutorService to manage a pool of threads
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private FirebaseStorage firebaseStorage; // <-- INITIALIZE FIREBASE STORAGE FIELD
    // A Handler to post results back to the Main (UI) Thread
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        binding = FragmentSettings2Binding.inflate(inflater, container, false);
        mainActivity.hideBottomBarWhileScrollingDown(binding.settingsScrollView);
        authManager = Authentication.getInstance();
        FirebaseUser currentUser = authManager.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance(); // <-- INITIALIZE FIREBASE STORAGE HERE
        binding.faqBtn.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_settingsFragment_to_faqsFragment);
        });
        binding.contactUsBtn.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_settingsFragment_to_contactUsFragment);
        });
        binding.logoutBtn.setOnClickListener(view -> {
            CustomDialog customDialog = new CustomDialog(requireActivity());
            customDialog.showDialog(
                    "Log Out",
                    "Are You Sure You Want To Log Out ?",
                    "Log Out",
                    v -> {
                        authManager.signOut();
                        Snackbar.make(requireView(), "Logged Out Successfully", Snackbar.LENGTH_SHORT).show();
                        goToTheNext();
                    },
                    "Cancel",
                    null);


        });
        binding.changePasswordBtn.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_settingsFragment_to_changePasswordFragment);
        });
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
                                Snackbar.make(requireView(),
                                        "Account deleted successfully", Snackbar.LENGTH_SHORT).show();
                                        goToTheNext();
                            }

                            @Override
                            public void onFailure() {
                                Snackbar.make(requireView(),
                                        "Error deleting account", Snackbar.LENGTH_SHORT).show();
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
                                                Snackbar.make(requireView(),
                                                        "Password cannot be empty", Snackbar.LENGTH_SHORT).show();
                                                return;
                                            }
                                            // Re-Authenticate
                                            FirebaseUser user = authManager.getCurrentUser();
                                            if (user == null) return;
                                            AuthCredential cred = authManager.getCredential(currentPassword, null);
                                            user.reauthenticate(cred)
                                                    .addOnCompleteListener(reauthTask -> {
                                                        if (reauthTask.isSuccessful()) {
                                                            authManager.deleteAccount(requireActivity(), new Authentication.DeleteAccountCallBack() {
                                                                @Override public void onSuccess() {
                                                                    Snackbar.make(requireView(),
                                                                            "Account deleted successfully", Snackbar.LENGTH_SHORT).show();
                                                                            goToTheNext();

                                                                }
                                                                @Override public void onFailure() {
                                                                    Snackbar.make(requireView(),
                                                                            "Error deleting account after re-auth", Snackbar.LENGTH_SHORT).show();
                                                                }
                                                                @Override public void onReAuthenticate() {
                                                                }
                                                            });
                                                        } else {
                                                            Snackbar.make(requireView(),
                                                                    "Re-authentication failed: " + Objects.requireNonNull(reauthTask.getException()).getMessage(),
                                                                    Snackbar.LENGTH_LONG).show();
                                                        }
                                                    });
                                        },
                                        "Cancel",
                                        cancelView -> pwdDialog.dialog.dismiss()
                                );
                            }
                        });
                    },
                    "Cancel",
                    cancelView -> confirm.dialog.dismiss()
            );
            confirm.title.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.danger)
            );
        });
        binding.exportDataBtn.setOnClickListener(view -> {
            CustomDialog customDialog = new CustomDialog(requireActivity());
            customDialog.showDialog(
                    "Export Data",
                    "Your data will be exported as a JSON file and downloaded to your phone. It may take a moment.", // Updated message
                    "Export",
                    v->{
                        if (currentUser != null) {
                            String uid = currentUser.getUid(); // Access UID directly
                            Log.d(TAG, "Attempting to export data for UID: " + uid);
                            Snackbar.make(requireView(), "Exporting data...", Snackbar.LENGTH_SHORT).show(); // User feedback
                            callExportDataFunctionWithExecutor(uid); // Check permissions before calling
                        } else {
                            Log.w(TAG, "No user signed in to export data.");
                            Snackbar.make(requireView(), "Please sign in to export data.", Snackbar.LENGTH_SHORT).show();
                        }
                        customDialog.dialog.dismiss();
                    },
                    "Cancel",
                    v -> customDialog.dialog.dismiss()
            );
        });
        ViewCompat.setOnApplyWindowInsetsListener(binding.settingstitlecontainer, (v, insets) -> {
            Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());

            // Apply padding to the top for the status bar
            v.setPadding(0, statusBarInsets.top, 0, 0);

            return insets; // Return insets to keep consuming them
        });
        binding.englishButton.setOnClickListener(v-> showLanguageChangeDialog("en","English"));
        binding.frenshButton.setOnClickListener(v-> showLanguageChangeDialog("fr","French"));
        binding.spanishButton.setOnClickListener(v-> showLanguageChangeDialog("es","Spanish"));
        binding.arabeButton.setOnClickListener(v-> showLanguageChangeDialog("ar","Arabe"));






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
                "No", v1 -> customDialog.dialog.dismiss()
        );

        customDialog.message.setVisibility(View.VISIBLE);
    }
    private void goToTheNext(){
        Intent intent = new Intent(requireActivity(), signIn.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void callExportDataFunctionWithExecutor(String uid) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            String responseBody = null;
            int responseCode = -1;
            try {
                URL url = new URL(FUNCTION_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("uid", uid);

                try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                    writer.write(jsonBody.toString());
                    writer.flush();
                }

                responseCode = connection.getResponseCode();
                InputStream responseStream;
                if (responseCode >= 200 && responseCode < 300) {
                    responseStream = connection.getInputStream();
                } else {
                    responseStream = connection.getErrorStream();
                }

                if (responseStream != null) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        responseBody = response.toString();
                    }
                }

            }
            catch (IOException | JSONException e) {
                Log.e(TAG, "Network or JSON error calling export data function", e);
                responseBody = null; // Indicate failure
            }
            finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            // Post the result back to the Main Thread for UI updates
            final String finalResponseBody = responseBody;
            final int finalResponseCode = responseCode;
            mainHandler.post(() -> {
                if (finalResponseCode >= 200 && finalResponseCode < 300 && finalResponseBody != null) {
                    Log.i(TAG, "Export data function called successfully! Response: " + finalResponseBody);
                    try {
                        JSONObject jsonResponse = new JSONObject(finalResponseBody);
                        if (jsonResponse.has("filePath")) {
                            String filePath = jsonResponse.getString("filePath");
                            Log.d(TAG, "Received file path: " + filePath);
                            downloadFileFromFirebaseStorage(uid, filePath); // <-- CALL DOWNLOADER
                        } else {
                            Snackbar.make(requireView(), "Export successful, but file path missing in response.", Snackbar.LENGTH_LONG).show();
                            Log.e(TAG, "Response does not contain 'filePath': " + finalResponseBody);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing JSON response for file path", e);
                        Snackbar.make(requireView(), "Error processing export response.", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Log.e(TAG, "Error calling export data function (" + finalResponseCode + "): " + finalResponseBody);
                    Snackbar.make(requireView(), "Error exporting data. Please check logs.", Snackbar.LENGTH_LONG).show();
                }
            });
        });
    }


    private void downloadFileFromFirebaseStorage(String uid, String filePath) {
        StorageReference storageRef = firebaseStorage.getReference().child(filePath);
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d(TAG, "Download URL obtained: " + uri.toString());
            startDownload(uid, uri.toString(), filePath.substring(filePath.lastIndexOf('/') + 1));
        }).addOnFailureListener(exception -> {
            Log.e(TAG, "Failed to get download URL: " + exception.getMessage());
            Snackbar.make(requireView(), "Failed to get download link.", Snackbar.LENGTH_LONG).show();
        });
    }

    private void startDownload(String uid, String url, String fileName) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription("Downloading user data for " + uid);
            request.setTitle(fileName); // e.g., "export.json"
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            // Save to the Downloads directory
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            DownloadManager manager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
            if (manager != null) {
                manager.enqueue(request);
                Snackbar.make(requireView(), "Download started! Check your Downloads folder.", Snackbar.LENGTH_LONG).show();
                Log.i(TAG, "Download started for file: " + fileName + " (UID: " + uid + ")");
            } else {
                Snackbar.make(requireView(), "DownloadManager not available.", Snackbar.LENGTH_LONG).show();
                Log.e(TAG, "DownloadManager is null. Cannot start download.");
            }
        } catch (Exception e) {
            Snackbar.make(requireView(), "Failed to start download: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
            Log.e(TAG, "Exception in startDownload: ", e);
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdown();
        binding = null;
    }

}