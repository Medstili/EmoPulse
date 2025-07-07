package com.medstili.emopulse.Auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.EmailAuthProvider;

import com.medstili.emopulse.R;
import java.util.Objects;


/**
 * A singleton manager for all Firebase‑Auth operations.
 * Exposes simple methods: signIn, resendVerification, etc., each taking a callback.
 */
public class Authentication {
    private static Authentication instance;
    private final FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;


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
    public interface ResetPasswordCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
    public interface DeleteAccountCallBack{
        void onSuccess();
        void onFailure();
        void onReAuthenticate();
    }
    public interface UpdatePasswordCallBack{
        void onSuccess(String SuccessMsg);
        void onFailure(String ErrorMsg);
        void onReAuthenticate();
    }
    public interface GoogleAuthCallback {
        void onSuccess(FirebaseUser user, boolean isNewUser);
        void onFailure(String errorMessage);
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
                            callback.onFailure(err);
                            return;
                        }
                        if (user.isEmailVerified()) {
                            callback.onSuccess(user);
                        } else {
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
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && user.isEmailVerified()) {
                        callback.onVerified();
                    } else {
                        callback.onNotVerified();
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


    /// delete user data



    /**
     * updating password
     */

    public void updatePassword(String newPassword, UpdatePasswordCallBack callBack){
        FirebaseUser user = getCurrentUser();
        user.updatePassword(newPassword)
                .addOnCompleteListener(task ->{
                        if (task.isSuccessful()) {
                            callBack.onSuccess("Password updated successfully!");
                        } else {
                            if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                                callBack.onReAuthenticate();

                            } else {
                                callBack.onFailure( "Error updating password: " + Objects.requireNonNull(task.getException()).getMessage());
                            }
                        }

                    });
    }
    /**
     * reset the password
     */

    public void sendPasswordResetEmail(String email, ResetPasswordCallback callback){
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        String err = task.getException() != null
                                ? task.getException().getMessage()
                                : "Unknown error sending reset email";
                        callback.onFailure("Failed to send reset email: " + err);
                    }
                });


    }

    public AuthCredential getCredential(String currentPassword, String Token){
        FirebaseUser user = getCurrentUser();
        if(Token !=null){
          return  GoogleAuthProvider.getCredential(Token, null);
        }
        if(user == null) return null;
        return EmailAuthProvider
                .getCredential(Objects.requireNonNull(user.getEmail()), currentPassword);
    }

    /** Call this once (e.g. in Application.onCreate or your SplashActivity) **/
    public void initGoogleSignIn(Context ctx) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(ctx.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(ctx, gso);
    }

    /** Returns the Intent you should pass to startActivityForResult(...) **/
    public Intent getGoogleSignInIntent() {
        if (googleSignInClient == null) {
            throw new IllegalStateException("Must call initGoogleSignIn() first");
        }
        return googleSignInClient.getSignInIntent();
    }


    /** Call in your Activity.onActivityResult after you get the Google SignInIntent result */
    public void handleGoogleSignInResult(
            Intent data,
            final GoogleAuthCallback callback
    ) {
        Task<GoogleSignInAccount> task =
                GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount acct = task.getResult(ApiException.class);
            AuthCredential cred = getCredential(null,acct.getIdToken());
            mAuth.signInWithCredential(cred)
                    .addOnCompleteListener(authTask -> {
                        if (authTask.isSuccessful()) {
                            boolean isNew = Objects.requireNonNull(authTask.getResult()
                                            .getAdditionalUserInfo())
                                    .isNewUser();
                            callback.onSuccess(getCurrentUser(), isNew);
                        } else {
                            callback.onFailure(
                                    authTask.getException() != null
                                            ? authTask.getException().getMessage()
                                            : "Firebase Auth failed"
                            );
                        }
                    });
        } catch (ApiException e) {
            callback.onFailure("Google sign-in failed: " + e.getMessage());
        }
    }
}

