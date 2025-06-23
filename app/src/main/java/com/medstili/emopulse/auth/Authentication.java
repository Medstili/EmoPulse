package com.medstili.emopulse.auth;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.EmailAuthProvider;

import java.util.Objects;


/**
 * A singleton manager for all Firebase‑Auth operations.
 * Exposes simple methods: signIn, resendVerification, etc., each taking a callback.
 */
public class Authentication {
    private static Authentication instance;
    private final FirebaseAuth mAuth;

    private Authentication() {
        mAuth = FirebaseAuth.getInstance();
    }

    public static synchronized Authentication getInstance() {
        if (instance == null) {
            instance = new Authentication();
        }
        return instance;
    }

    /** Callback for sign‑in result. */
    public interface SignInCallback {
        /** Called when user is successfully signed in and email is verified. */
        void onSuccess(FirebaseUser user);
        /** Called on failure or if email is not verified. */
        void onFailure(String errorMessage);
    }
    public interface SignUpCallback{
        void onSuccess();
        void onFailure(String errorMessage);
    }
    public interface ResendCallback {
        void onResent();
        void onFailure(String errorMessage);
    }
    public interface VerificationCallback {
        /** Called when we discover the user *is* verified. */
        void onVerified();
        /** Called when we discover the user is *not* verified (or on error). */
        void onNotVerified();
    }
    public  interface DeleteAccountCallBack{
        void onSuccess();
        void onFailure();
        void onReAuthenticate();
    }
    public  interface UpdatePasswordCallBack{
        void onSuccess(String SuccessMsg);
        void onFailure(String ErrorMsg);
    }

    /**
     * Attempts to sign in with email/password, reloads user, 
     * checks emailVerified, and notifies callback.
     */
    public void signInWithEmail(
            final String email,
            final String password,
            final SignInCallback callback
    ) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        callback.onFailure(
                                "Login failed: " +
                                        (task.getException() != null
                                                ? task.getException().getMessage()
                                                : "Unknown error")
                        );

                        return;
                    }
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user == null) {
                        callback.onFailure("Login failed: user is null");
                        return;
                    }
                    user.reload().addOnCompleteListener(reloadTask -> {
                        if (!reloadTask.isSuccessful()) {
                            String err = reloadTask.getException() != null
                                    ? reloadTask.getException().getMessage()
                                    : "Could not verify email status";
//                            mAuth.signOut();
                            callback.onFailure(err);
                            return;
                        }
                        if (user.isEmailVerified()) {
                            callback.onSuccess(user);
                        } else {
//                            mAuth.signOut();
                            callback.onFailure("Please verify your email before logging in.");
                        }
                    });
                });
    }

    public void signUpWithEmail(
            final String email,
            final String password,
            final SignUpCallback callback
    ) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(createTask -> {
                    if (!createTask.isSuccessful()) {

                        if (createTask.getException() instanceof FirebaseAuthUserCollisionException) {
                            String err = "This email is already registered. Please try logging in or use a different email.";
                            callback.onFailure(err);
                        }else if (createTask.getException() instanceof FirebaseAuthWeakPasswordException){
                            String err = "Password is too weak. Please choose a stronger password (at least 6 characters).";
                            callback.onFailure(err);
                        }else if (createTask.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            String err = "The email address is badly formatted. Please enter a valid email.";
                            callback.onFailure(err);
                        }else if (createTask.getException() instanceof FirebaseNetworkException){
                            String err = "Network error. Please check your internet connection and try again.";
                            callback.onFailure(err);
                        }else {

                            String err ="Authentication failed:";
                            Log.e("SignUp Error:", "createUserWithEmail:failure", createTask.getException());
                            callback.onFailure(err);
                        }



                        return;
                    }

                    // User was created; send verification email:
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user == null) {
                        callback.onFailure("Sign-up failed: user is null");
                        return;
                    }

                    user.sendEmailVerification()
                            .addOnCompleteListener(verifyTask -> {
                                if (verifyTask.isSuccessful()) {
                                    callback.onSuccess();
                                } else {
                                    // Delete the unverified "ghost" account
                                    user.delete();
                                    String err = verifyTask.getException() != null
                                            ? verifyTask.getException().getMessage()
                                            : "Unknown error sending verification email";
                                    callback.onFailure("Failed to send verification email: " + err);
                                }
                            });
                });
    }

    public void checkEmailVerified(final VerificationCallback callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            callback.onNotVerified();
            return;
        }
        user.reload()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful() && user.isEmailVerified()) {
                            callback.onVerified();
                        } else {
                            callback.onNotVerified();
                        }
                    }
                });
    }

    /** Resends the email‑verification to the current user, if not yet verified. */
    public void resendVerificationEmail(ResendCallback callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            callback.onFailure("No user is currently signed in.");
            return;
        }
        if (user.isEmailVerified()) {
            callback.onFailure("Email is already verified.");
            return;
        }
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onResent();
                    } else {
                        String err = task.getException() != null
                                ? task.getException().getMessage()
                                : "Unknown error";
                        callback.onFailure("Failed to resend: " + err);
                    }
                });
    }

    /** Signs out the current user immediately. */
    public void signOut() {
        mAuth.signOut();
    }


    public boolean isUserSignedIn() {
        return mAuth.getCurrentUser() != null;

    }

    /**
     * getting the current user
     */
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    /**
     * deleting the user account and its data
     */
    public void deleteAccount(Activity activity, DeleteAccountCallBack callBack){
       FirebaseUser user = getCurrentUser();
        user.delete()
                .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            System.out.println("User account deleted.");
                            callBack.onSuccess();
                        }
                        else {
                            if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {

                                callBack.onReAuthenticate();
                            } else {
                                System.out.println("Error deleting user account: " + Objects.requireNonNull(task.getException()).getMessage());
                                callBack.onFailure();
                            }
                        }
                    });
    }

    /**
     * updating password
     */

    public void updatePassword(String newPassword, String currentPassword, UpdatePasswordCallBack callBack){
        FirebaseUser user = getCurrentUser();
        user.updatePassword(newPassword)
                .addOnCompleteListener(task ->{
                        if (task.isSuccessful()) {
                            callBack.onSuccess("Password updated successfully!");
                        } else {
                            if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {

                                // HERE'S THE REAUTHENTICATION PART:
                                // 1. Show a dialog asking for current password again.
                                // 2. Once user provides it (assuming 'currentPassword' holds it)
                                AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), currentPassword);

                                user.reauthenticate(credential)
                                        .addOnCompleteListener(reauthTask -> {
                                                if (reauthTask.isSuccessful()) {
                                                    System.out.println("User re-authenticated successfully! Retrying password update...");
                                                    // Now, retry the password update
                                                    user.updatePassword(newPassword)
                                                            .addOnCompleteListener(retryTask ->{
                                                                    if (retryTask.isSuccessful()) {
                                                                        callBack.onSuccess("Password updated successfully!");
                                                                    } else {
                                                                        callBack.onFailure("Error updating password after re-auth: " + Objects.requireNonNull(retryTask.getException()).getMessage());
                                                                    }

                                                            });
                                                }
                                                else {
                                                    callBack.onFailure("Re-authentication failed:" + Objects.requireNonNull(reauthTask.getException()).getMessage());
                                                }

                                        });
                            } else {
                                    callBack.onFailure( "Error updating password: " + task.getException().getMessage());
                            }
                        }

                    });
    }
    /**
     * reset the password
     */

    public void resetPassword(){}

    public AuthCredential getCredential(String currentPassword){
        FirebaseUser user = getCurrentUser();
        if(user == null) return null;
        return EmailAuthProvider
                .getCredential(Objects.requireNonNull(user.getEmail()), currentPassword);
    }
}
