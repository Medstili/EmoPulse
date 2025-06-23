package com.medstili.emopulse.User;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User {

    private String password, email, id;

    public User() {}

    public User(String password, String email, String id) {
        this.password = password;
//        this.username = username;
        this.email = email;
        this.id = id;
    }

//    getters
//    public String getUsername() {
//        return username;
//    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }
//    setters


//    public void setUsername(String username) {
//        this.username = username;
//    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }


//    ************************

    public void addUser(View view, String userPassword, String userEmail){

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
        String userId = userRef.push().getKey();
        User newUser = new User(userPassword, userEmail, userId);
        assert userId != null;
        userRef.child(userId).setValue(newUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Snackbar.make(view , "Your Account  Created successfully", Snackbar.LENGTH_SHORT).show();
            }
            else {
                Snackbar.make(view, "Account creation failed", Snackbar.LENGTH_SHORT).show();
            }
        });

    }
}
