package com.medstili.emopulse.DataBase;


import android.util.Log;
import androidx.annotation.NonNull;

import com.github.mikephil.charting.data.BarEntry;
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
import com.medstili.emopulse.Models.MoodLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class DataBase {
    public final DatabaseReference userExercisesRef, userGoalsRef, serverTimeOffset, dashboardSummariesRef;
    public ValueEventListener exercisesListener;
    public ChildEventListener goalsListener;
    private ChildEventListener goalSummariesListener;
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
        dashboardSummariesRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.getUid())
                .child("dashboardSummaries");
        serverTimeOffset =  FirebaseDatabase.getInstance()
                .getReference(".info/serverTimeOffset");
//        moodRef = FirebaseDatabase.getInstance()
//                .getReference("users")
//                .child(user.getUid())
//                .child("moodLogs");
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

    /**
     * Loads completed exercises for the current user.
     * @param callback Callback to handle success or failure.
     */
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
            long created_at,
            String title,
            String description,
            String frequency,
            String exercise,
            Long deadline,
            List<Integer> customDays,
            int targetCount,
            int completedCount,
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
        goalData.put("deadline", deadline);
        goalData.put("createdAt", created_at);
        goalData.put("targetCount", targetCount);
        goalData.put("completedCount", completedCount);
        if ("Custom".equals(frequency) && customDays != null) {
            goalData.put("customDays", customDays);
        }
        userGoalsRef.child(goalId).setValue(goalData)
                .addOnSuccessListener(aVoid -> callback.onSuccess(goalId))
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

                // Convert customDays from Long to Integer
                List<Integer> customDays = null;
                if (snapshot.child("customDays").exists()) {
                    @SuppressWarnings("unchecked")
                    List<Long> customDaysLong = (List<Long>) snapshot.child("customDays").getValue();
                    if (customDaysLong != null) {
                        customDays = new ArrayList<>();
                        for (Long day : customDaysLong) {
                            if (day != null) {
                                customDays.add(day.intValue());
                            }
                        }
                    }
                }

                callback.onGoalLoaded(goalId, title, description, frequency, exercise, customDays);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                String goalId = snapshot.getKey();
                String title = snapshot.child("title").getValue(String.class);
                String description = snapshot.child("description").getValue(String.class);
                String frequency = snapshot.child("frequency").getValue(String.class);
                String exercise = snapshot.child("exercise").getValue(String.class);

                // Convert customDays from Long to Integer
                List<Integer> customDays = null;
                if (snapshot.child("customDays").exists()) {
                    @SuppressWarnings("unchecked")
                    List<Long> customDaysLong = (List<Long>) snapshot.child("customDays").getValue();
                    if (customDaysLong != null) {
                        customDays = new ArrayList<>();
                        for (Long day : customDaysLong) {
                            if (day != null) {
                                customDays.add(day.intValue());
                            }
                        }
                    }
                }

                callback.onGoalLoaded(goalId, title, description, frequency, exercise, customDays);

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
                    Long deadline = Objects.requireNonNull(snapshot.child("deadline").getValue(Long.class));

                    // Convert customDays from Long to Integer
                    List<Integer> customDays = null;
                    if (snapshot.child("customDays").exists()) {
                        List<Long> customDaysLong = (List<Long>) snapshot.child("customDays").getValue();
                        if (customDaysLong != null) {
                            customDays = new ArrayList<>();
                            for (Long day : customDaysLong) {
                                if (day != null) {
                                    customDays.add(day.intValue());
                                }
                            }
                        }
                    }

                    callback.onGoalLoaded(goalId, title, description, frequency, exercise, customDays, createdAt, deadline);
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
                        List<Long> customDaysLong = (List<Long>)
                                goalSnapshot.child("customDays").getValue();
                        if (customDaysLong != null) {
                            DayOfWeek todayDow = today.getDayOfWeek();
                            for (Long dayLong : customDaysLong) {
                                if (dayLong != null) {
                                    // Convert from app's numbering (1=Sun, 2=Mon, ..., 7=Sat)
                                    // to Java's DayOfWeek for comparison
                                    DayOfWeek dayOfWeek = DayOfWeek.of(dayLong.intValue());
                                    if (dayOfWeek == todayDow) {
                                        matches = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    // 2) If it matches, write under /checkDays/{yyyy‑MM‑dd}: true,
                    //    but only if that key doesn’t already exist:
                    if (matches && goalId != null) {
                        DataSnapshot checkDaysSnap = goalSnapshot.child("checkDays");
                        if (!checkDaysSnap.hasChild(todayKey)) {
                            // Stamp today's date
                            userGoalsRef
                                    .child(goalId)
                                    .child("checkDays")
                                    .child(todayKey)
                                    .setValue(true)
                                    .addOnSuccessListener(aVoid ->
                                            Log.d("DataBase", "Stamped " + todayKey + " for goal: " + currentGoalTitle)

                                    )
                                    .addOnFailureListener(e ->
                                            Log.e("DataBase", "Failed stamping " + todayKey + " for goal: " + currentGoalTitle, e));

                            userGoalsRef.child(goalId).child("completedCount")
                                    .runTransaction(new Transaction.Handler() {
                                        @NonNull
                                        @Override
                                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                            Integer currentCount = mutableData.getValue(Integer.class);
                                            if (currentCount == null) {
                                                currentCount = 0;
                                            }
                                            mutableData.setValue(currentCount + 1);
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                                            if (error != null) {
                                                Log.e("DataBase", "Failed to increment completedCount for goal: " + goalId, error.toException());
                                            } else if (committed) {
                                                Log.d("DataBase", "Incremented completedCount for goal: " + goalId);
                                                dashboardSummariesRef
                                                        .child("goalsSummary")
                                                        .child(goalId)
                                                        .child("completedCount")
                                                        .runTransaction(new Transaction.Handler() {
                                                            @NonNull
                                                            @Override
                                                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                                                Integer currentCount = mutableData.getValue(Integer.class);
                                                                if (currentCount == null) {
                                                                    currentCount = 0;
                                                                }
                                                                mutableData.setValue(currentCount + 1);
                                                                return Transaction.success(mutableData);
                                                            }

                                                            @Override
                                                            public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                                                                if (error != null) {
                                                                    Log.e("DataBase", "Failed to update goalsSummary for: " + goalId, error.toException());
                                                                } else if (committed) {
                                                                    Log.d("DataBase", "Updated goalsSummary completedCount for: " + goalId);
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
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
    //  get just the targetCount and the completedCount
    public void loadGoalsSummaries(
            loadGoalsSummariesCallback callback
    ) {
        DatabaseReference goalsSummaryRef = dashboardSummariesRef.child("goalsSummary");
        // Remove any existing listener to avoid duplicates
        if (goalSummariesListener != null) {
            goalsSummaryRef.removeEventListener(goalSummariesListener);
        }
        // Add a new ChildEventListener to fetch user goals
        goalSummariesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {

                String goalId = snapshot.getKey();
                String title = snapshot.child("title").getValue(String.class);
                Integer completedCount = snapshot.child("completedCount").getValue(Integer.class);
                Integer targetCount = snapshot.child("targetCount").getValue(Integer.class);
                Boolean done = snapshot.child("done").getValue(Boolean.class);

                callback.onGoalLoaded(
                        goalId,
                        title,
                        targetCount != null ? targetCount : 0,
                        completedCount != null ? completedCount : 0,
                        done != null ? done : false
                );


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                String goalId = snapshot.getKey();
                String title = snapshot.child("title").getValue(String.class);
                Integer completedCount = snapshot.child("completedCount").getValue(Integer.class);
                Integer targetCount = snapshot.child("targetCount").getValue(Integer.class);
                Boolean done = snapshot.child("done").getValue(Boolean.class);

                callback.onGoalLoaded(
                        goalId,
                        title,
                        targetCount != null ? targetCount : 0,
                        completedCount != null ? completedCount : 0,
                        done != null ? done : false
                );
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String goalId = snapshot.getKey();
                callback.onGoalLoaded(goalId, null, 0, 0, false);
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

        goalsSummaryRef.addChildEventListener(goalSummariesListener);

    }

    public void addGoalSummary(
            String goalId,
            String title,
            int targetCount,
            int completedCount,
            boolean done,
            goalSummaryCallback callback
    ) {
        Map<String, Object> summaryData = new HashMap<>();
        summaryData.put("title", title);
        summaryData.put("targetCount", targetCount);
        summaryData.put("completedCount", completedCount);
        summaryData.put("done", done);

        dashboardSummariesRef
                .child("goalsSummary")
                .child(goalId)
                .setValue(summaryData)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(DatabaseError.fromException(e)));
    }

    public void loadUserMoodData( loadUserMoodLogsCallback callback) {
        long timeLimit = System.currentTimeMillis();
        timeLimit -= 24 * 60 * 60 * 1000L;

        DatabaseReference moodRef = dashboardSummariesRef.child("moodLogs");

        moodRef.orderByChild("timestamp").startAt(timeLimit)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<MoodLog> logs = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            logs.add(ds.getValue(MoodLog.class));
                        }
                        callback.onSuccess(logs);
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error);
                    }
                });
    }

    public void incrementMessageCount() {
        String userId = user != null ? user.getUid() : null;
        if (userId == null) return;

        String today = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        DatabaseReference ref = dashboardSummariesRef
                .child("interactivity")
                .child(today)
                .child("messageCount");


        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer currentCount = currentData.getValue(Integer.class);
                if (currentCount == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue(currentCount + 1);
                }
                Log.d("DB", "Message count for " + today + " incremented.");
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                if (error != null) {
                    Log.e("DB", "incrementMessageCount failed: " + error.getMessage());
                }
            }
        });
    }


    public void loadInteractivityRawData(LoadInteractivityRawCallback callback) {
        String userId = user != null ? user.getUid() : null;
        if (userId == null) return;

        DatabaseReference ref = dashboardSummariesRef.child("interactivity");
        ref.orderByKey().limitToLast(7).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Integer> dateToCount = new HashMap<>();
                for (DataSnapshot daySnap : snapshot.getChildren()) {
                    Log.d(
                            "DB",
                            "Raw interactivity data - date: " + daySnap.getKey() +
                                    ", messageCount: " + daySnap.child("messageCount").getValue()
                    );
                    Integer count = daySnap.child("messageCount").getValue(Integer.class);
                    if (count != null) {
                        Log.d("DB", "Adding to map: " + daySnap.getKey() + " -> " + count);
                        dateToCount.put(daySnap.getKey(), count);
                    }
                }
                callback.onSuccess(dateToCount);
                callback.onComplete();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error);
            }
        });
    }
    public interface LoadInteractivityRawCallback {
        void onSuccess(Map<String, Integer> dateToCount);
        void onFailure(DatabaseError error);
        void onComplete();
    }
    public interface loadUserMoodLogsCallback{
        void onSuccess(List<MoodLog> moodLogs);
        void onFailure(DatabaseError error);
    }
    public interface goalSummaryCallback{

        void onSuccess();
        void onFailure(DatabaseError error);
        void onComplete();
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
        void onSuccess(String goalId);
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
        void onGoalLoaded(String goalId, String title, String description, String frequency, String exercise, List<Integer> customDays);

        void onFailure(DatabaseError error);

        void onComplete();
    }
    public interface loadGoalsSummariesCallback{
        void onGoalLoaded(String summary, String title, int targetCount, int completedCount, boolean done);

        void onFailure(DatabaseError error);

        void onComplete();
    }
    /**
     * Callback interface for loading a specific goal by its ID.
     */
    public interface LoadGoalByIdCallback {
        void onGoalLoaded(String goalId, String title, String description, String frequency, String exercise, List<Integer> customDays , Long createdAt, Long deadline);

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
