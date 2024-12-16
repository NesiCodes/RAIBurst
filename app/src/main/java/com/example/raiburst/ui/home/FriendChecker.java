package com.example.raiburst.ui.home;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CountDownLatch;

public class FriendChecker {

    private FirebaseAuth auth;
    private DatabaseReference database;

    public FriendChecker() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("Users");
    }

    public boolean isFriend(String friendName) {
        String currentUserId = auth.getCurrentUser().getUid();

        if (currentUserId == null) {
            return false; // User is not authenticated
        }

        DatabaseReference friendsRef = database.child(currentUserId).child("friends");

        final boolean[] isFriend = {false}; // Result variable
        CountDownLatch latch = new CountDownLatch(1); // To block until Firebase query completes

        friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isFriend[0] = snapshot.hasChild(friendName); // Check if the friend's name exists
                latch.countDown(); // Signal that the operation is complete
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                latch.countDown(); // Signal completion even on failure
            }
        });

        try {
            latch.await(); // Wait for the Firebase operation to complete
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return isFriend[0]; // Return the result
    }

    public String getFriendEmail(String friendName) {
        String currentUserId = auth.getCurrentUser().getUid();

        if (currentUserId == null) {
            return null; // User is not authenticated
        }

        DatabaseReference friendsRef = database.child(currentUserId).child("friends");

        final String[] friendEmail = {null}; // Result variable to store the email
        CountDownLatch latch = new CountDownLatch(1); // To block until Firebase query completes

        friendsRef.child(friendName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    friendEmail[0] = snapshot.getValue(String.class); // Retrieve the email
                }
                latch.countDown(); // Signal that the operation is complete
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                latch.countDown(); // Signal completion even on failure
            }
        });

        try {
            latch.await(); // Wait for the Firebase operation to complete
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return friendEmail[0]; // Return the email or null if not found
    }
}
