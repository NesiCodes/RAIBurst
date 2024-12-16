package com.example.raiburst.ui.home;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class FriendNameLister {

    private FirebaseAuth auth;
    private DatabaseReference database;

    public FriendNameLister() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("Users");
    }

    public List<String> listFriendsNames() {
        List<String> friendNames = new ArrayList<>();
        String currentUserId = auth.getCurrentUser().getUid();

        if (currentUserId == null) {
            return friendNames; // Return empty list if not authenticated
        }

        DatabaseReference friendsRef = database.child(currentUserId).child("friends");

        CountDownLatch latch = new CountDownLatch(1); // To block until data is retrieved

        friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                    String friendName = friendSnapshot.getKey(); // Get the name (key) only
                    if (friendName != null) {
                        friendNames.add(friendName);
                    }
                }
                latch.countDown(); // Signal that the data has been retrieved
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                latch.countDown(); // Signal even if there's an error
            }
        });

        try {
            latch.await(); // Wait until the Firebase operation is complete
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return friendNames; // Return the list of friend names
    }
}
