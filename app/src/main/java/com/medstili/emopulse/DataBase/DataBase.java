package com.medstili.emopulse.DataBase;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.medstili.emopulse.Auth.Authentication;


import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class DataBase {
    public final DatabaseReference userExercisesRef, userGoalsRef;
    public ValueEventListener exercisesListener;
    public ChildEventListener goalsListener;
    private static DataBase instance;
    FirebaseUser user;


    private DataBase() {
        user = Authentication.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("Not signed in");
        userExercisesRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.getUid())
                .child("exercisesDone");
        userGoalsRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.getUid())
                .child("goals");
                
    }


    /**
     * Singleton instance getter for DataBase.
     *
     * @return The singleton instance of DataBase.
     */
    public static synchronized DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    /**
     * Records the completion of an exercise.
     *
     * @param exerciseName The name of the exercise to record.
     * @param callback     Callback to handle success or failure.
     */

    public void recordExerciseCompletion(
            String exerciseName,
            CompletionCallback callback
    ) {
        DatabaseReference exRef = userExercisesRef.child(exerciseName);
        exRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData root) {
                Integer cnt = root.child("count").getValue(Integer.class);
                if (cnt == null) cnt = 0;
                root.child("count").setValue(cnt + 1);
                root.child("lastDone").setValue(ServerValue.TIMESTAMP);
                return Transaction.success(root);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot snap) {
                if (error != null) {
                    callback.onFailure(error);
                } else if (committed) {
                    Integer newCount = snap.child("count").getValue(Integer.class);
                    callback.onSuccess(newCount);
                }
            }
        });
    }

    public void loadCompletedExercises(LoadExercisesCallback callback) {

        if (exercisesListener != null) {
            userExercisesRef.removeEventListener(exercisesListener);
        }
        // Add a new listener to fetch completed exercises
        exercisesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                for (DataSnapshot exSnap : snap.getChildren()) {
                    String exerciseId = exSnap.getKey();
                    String formattedExercise = Objects.requireNonNull(exerciseId).replace("_", " ");
                    Long count = exSnap.child("count").getValue(Long.class);
//                            Long lastDone = exSnap.child("lastDone").getValue(Long.class);

                    callback.onSuccess(formattedExercise, count != null ? count.intValue() : 0);

                }
                callback.onComplete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB", "Load failed: " + error.getMessage());
                callback.onFailure(error);
                callback.onComplete();
            }
        };
        userExercisesRef.addValueEventListener(exercisesListener);

    }

    public void addUserGoal(
            String title,
            String description,
            String frequency,
            String exercise,
            List<String> customDays,
            GoalCompletionCallback callback
    ) {

        String goalId = userGoalsRef.push().getKey();

        if (goalId == null) {
            callback.onFailure(DatabaseError.fromException(new Exception("Failed to generate goal ID")));
            return;
        }
        Map<String, Object> goalData = new HashMap<>();
        goalData.put("title", title);
        goalData.put("description", description);
        goalData.put("frequency", frequency);
        goalData.put("exercise", exercise);
        goalData.put("createdAt", ServerValue.TIMESTAMP);
        if ("Custom".equals(frequency) && customDays != null) {
            goalData.put("customDays", customDays);
        }
        userGoalsRef.child(goalId).setValue(goalData)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(DatabaseError.fromException(e)));

    }
    
    public void loadUserGoals(
            LoadGoalsCallback callback
    ) {
        // Remove any existing listener to avoid duplicates
        if (goalsListener != null) {
            userGoalsRef.removeEventListener(goalsListener);
        }
        // Add a new ChildEventListener to fetch user goals
        goalsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                String goalId = snapshot.getKey();
                String title = snapshot.child("title").getValue(String.class);
                String description = snapshot.child("description").getValue(String.class);
                String frequency = snapshot.child("frequency").getValue(String.class);
                String exercise = snapshot.child("exercise").getValue(String.class);
                @SuppressWarnings("unchecked")
                List<String> customDays = (List<String>) snapshot.child("customDays").getValue(); // Suppress unchecked cast warning
                callback.onGoalLoaded(goalId, title, description, frequency, exercise, customDays);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                String goalId = snapshot.getKey();
                String title = snapshot.child("title").getValue(String.class);
                String description = snapshot.child("description").getValue(String.class);
                String frequency = snapshot.child("frequency").getValue(String.class);
                String exercise = snapshot.child("exercise").getValue(String.class);
                @SuppressWarnings("unchecked")
                List<String> customDays = (List<String>) snapshot.child("customDays").getValue(); // Suppress unchecked cast warning
                callback.onGoalLoaded(goalId, title, description, frequency, exercise, customDays);
                // ...optional: log or handle as needed...
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String goalId = snapshot.getKey();
                // Notify removal with nulls or a new callback if needed
                callback.onGoalLoaded(goalId, null, null, null, null, null);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
                // Not used
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB", "Load failed: " + error.getMessage());
                callback.onFailure(error);
                callback.onComplete();
            }
        };
        userGoalsRef.addChildEventListener(goalsListener);
    }


    public void checkGoalsExist(GoalsExistCallback callback) {
        userGoalsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onResult(snapshot.exists() && snapshot.hasChildren());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onResult(false);
            }
        });
    }

    public void getGoalById(
            String goalId,
            LoadGoalByIdCallback callback
    ) {
        userGoalsRef.child(goalId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String title = snapshot.child("title").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    String frequency = snapshot.child("frequency").getValue(String.class);
                    String exercise = snapshot.child("exercise").getValue(String.class);
                    Long createdAt = Objects.requireNonNull(snapshot.child("createdAt").getValue(Long.class));
                    @SuppressWarnings("unchecked")
                    List<String> customDays = (List<String>) snapshot.child("customDays").getValue();


                    callback.onGoalLoaded(goalId, title, description, frequency, exercise, customDays, createdAt);
                } else {
                    callback.onFailure(DatabaseError.fromException(new Exception("Goal not found")));
                }
                callback.onComplete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error);
                callback.onComplete();
            }
        });
    }
    public void checkIfExerciseExistsInAnyGoal(
            String exerciseName,
            OnExerciseCheckListener callBack
    ) {
        userGoalsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean exerciseFound = false;
                String foundGoalTitle = null;
                LocalDate today = LocalDate.now();
                String todayKey = today.toString();

                for (DataSnapshot goalSnapshot : dataSnapshot.getChildren()) {
                    String currentExercise = goalSnapshot.child("exercise")
                            .getValue(String.class);
                    if (currentExercise == null
                            || !currentExercise.equalsIgnoreCase(exerciseName)) {
                        continue;
                    }
                    // We have at least one matching goal
                    exerciseFound = true;
                    String goalId = goalSnapshot.getKey();
                    String currentGoalTitle = goalSnapshot.child("title")
                            .getValue(String.class);
                    foundGoalTitle = currentGoalTitle;
                    String frequency = goalSnapshot.child("frequency")
                            .getValue(String.class);

                    boolean matches = false;
                    if ("Daily".equalsIgnoreCase(frequency)) {
                        matches = true;
                    }
                    else if ("Weekly".equalsIgnoreCase(frequency)) {
                        matches = (today.getDayOfWeek() == DayOfWeek.MONDAY);
                    }
                    else if ("Custom".equalsIgnoreCase(frequency)) {
                        @SuppressWarnings("unchecked")
                        List<String> customDays = (List<String>)
                                goalSnapshot.child("customDays").getValue();
                        if (customDays != null) {
                            String todayName = today.getDayOfWeek()
                                    .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                            for (String cd : customDays) {
                                if (cd.equalsIgnoreCase(todayName)) {
                                    matches = true;
                                    break;
                                }
                            }
                        }
                    }

                    // 2) If it matches, write under /checkDays/{yyyy‑MM‑dd}: true,
                    //    but only if that key doesn’t already exist:
                    if (matches && goalId != null) {
                        DataSnapshot checkDaysSnap = goalSnapshot.child("checkDays");
                        if (!checkDaysSnap.hasChild(todayKey)) {
                            userGoalsRef
                                    .child(goalId)
                                    .child("checkDays")
                                    .child(todayKey)
                                    .setValue(true)
                                    .addOnSuccessListener(aVoid ->
                                            Log.d("DataBase",
                                                    "Stamped " + todayKey + " for goal: " + currentGoalTitle))
                                    .addOnFailureListener(e ->
                                            Log.e("DataBase",
                                                    "Failed stamping " + todayKey + " for goal: "
                                                            + currentGoalTitle, e));
                        } else {
                            Log.d("DataBase",
                                    "Already stamped " + todayKey + " for goal: " + currentGoalTitle);
                        }
                    }
                }

                if (exerciseFound) {
                    callBack.onExerciseFound(exerciseName, foundGoalTitle);
                } else {
                    callBack.onExerciseNotFound(exerciseName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callBack.onError("Database error: " + databaseError.getMessage());
            }
        });
    }
    public void loadGoalCheckDays(
            String goalId,
            LoadGoalCheckDays callback
    ) {
        userGoalsRef
                .child(goalId)
                .child("checkDays")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            // No dates → return empty list
                            callback.onSuccess(Collections.emptyList());
                            return;
                        }
                        List<String> dates = new ArrayList<>();
                        for (DataSnapshot dateSnap : snapshot.getChildren()) {
                            // Each child key is "yyyy-MM-dd"
                            String dateKey = dateSnap.getKey();
                            if (dateKey != null) {
                                dates.add(dateKey);
                            }
                        }
                        callback.onSuccess(dates);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error);
                    }
                });
    }

    public void contactUsMessage(
            String email,
            String message,
            contactUsCallback callback

    ){
        FirebaseUser user = Authentication.getInstance().getCurrentUser();
        DatabaseReference contactUsRef = FirebaseDatabase.getInstance()
                .getReference("contact_messages");
        Map<String, Object> contactData = new HashMap<>();
        contactData.put("userId", user.getUid());
        contactData.put("senderEmail", email);
        contactData.put("message", message);
        contactData.put("status", "new");
        contactData.put("timestamp", ServerValue.TIMESTAMP);
        contactUsRef.push().setValue(contactData)
                .addOnSuccessListener(a -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(DatabaseError.fromException(e)));
    }







    ///  interfaces for callbacks
    public interface LoadGoalCheckDays {
        void onSuccess(List<String> dateKeys);
        void onFailure(DatabaseError error);
    }

    public interface contactUsCallback {
        void onSuccess();
        void onFailure(DatabaseError error);
    }
    public interface OnExerciseCheckListener {
        void onExerciseFound(String exerciseName, String goalTitle); // Changed 'goalName' to 'goalTitle' to match your field
        void onExerciseNotFound(String exerciseName);
        void onError(String errorMessage);

    }
    /**
     * Callback interface for checking if goals exist.
     */
    public interface GoalsExistCallback {
        void onResult(boolean hasGoals);
    }
    /**
     * Callback interface for completion of exercise recording.
     */
    public interface CompletionCallback {
        void onSuccess(Integer newCount);
        void onFailure(DatabaseError error);
    }
    /**
     * Callback interface for goal completion.
     */
    public interface GoalCompletionCallback {
        void onSuccess();
        void onFailure(DatabaseError error);
    }
    /**
     * Callback interface for loading completed exercises.
     */
    public interface LoadExercisesCallback {
        void onSuccess(String exerciseName, int count);
        void onFailure(DatabaseError error);
        void onComplete();
    }
    /**
     * Callback interface for loading user goals.
     */
    public interface LoadGoalsCallback {
        void onGoalLoaded(String goalId, String title, String description, String frequency, String exercise, List<String> customDays);

        void onFailure(DatabaseError error);

        void onComplete();
    }
    /**
     * Callback interface for loading a specific goal by its ID.
     */
    public interface LoadGoalByIdCallback {
        void onGoalLoaded(String goalId, String title, String description, String frequency, String exercise, List<String> customDays , Long createdAt);

        void onFailure(DatabaseError error);

        void onComplete();
    }
    /**
     * Removes the goals listener to stop listening for changes.
     */
    public void removeGoalsListener(){
        if (goalsListener != null) {
            userGoalsRef.removeEventListener(goalsListener);
            goalsListener = null;
        }
    }
    /**
     * Stops listening for changes in completed exercises.
     */
    public void stopListeningForExercises() {
        if (exercisesListener != null) {
            userExercisesRef.removeEventListener(exercisesListener);
            exercisesListener = null;
        }
    }
}
