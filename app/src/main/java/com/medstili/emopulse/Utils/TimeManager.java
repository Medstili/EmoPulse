package com.medstili.emopulse.Utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.medstili.emopulse.DataBase.DataBase;

public class TimeManager {

    private static long serverOffset = 0;

    public static void syncServerTime() {
        DataBase db = DataBase.getInstance();
        DatabaseReference offsetRef = db.serverTimeOffset;
        offsetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    serverOffset = snapshot.getValue(Long.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Use this instead of System.currentTimeMillis()
    public static long getCurrentTime() {
        return System.currentTimeMillis() + serverOffset;
    }
}
