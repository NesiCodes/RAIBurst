package com.example.raiburst.ui.home;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CountDownLatch;

public class BalanceChecker {

    private FirebaseAuth auth;
    private DatabaseReference database;

    public BalanceChecker() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("Users");
    }

    public String getUserBalance() {
        String currentUserId = auth.getCurrentUser().getUid();

        if (currentUserId == null) {
            return null; // User is not authenticated
        }

        DatabaseReference balanceRef = database.child(currentUserId).child("balance");

        final String[] balance = {null}; // Variable to store the balance
        CountDownLatch latch = new CountDownLatch(1); // To block until Firebase query completes

        balanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    balance[0] = snapshot.getValue(String.class); // Retrieve the balance value
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

        return balance[0]; // Return the balance or null if not found
    }

    public boolean subtractFromBalance(double amount) {
        String currentUserId = auth.getCurrentUser().getUid();

        if (currentUserId == null) {
            return false; // User is not authenticated
        }

        DatabaseReference balanceRef = database.child(currentUserId).child("balance");

        CountDownLatch latch = new CountDownLatch(1); // To block until Firebase query completes
        final boolean[] success = {false};

        balanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String currentBalanceStr = snapshot.getValue(String.class);
                    if (currentBalanceStr != null) {
                        try {
                            double currentBalance = Double.parseDouble(currentBalanceStr);

                            if (currentBalance >= amount) {
                                double newBalance = currentBalance - amount;

                                // Update balance in Firebase
                                balanceRef.setValue(String.valueOf(newBalance))
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                success[0] = true;
                                            }
                                            latch.countDown();
                                        });
                            } else {
                                System.out.println("Insufficient balance.");
                                latch.countDown();
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid balance format in database.");
                            latch.countDown();
                        }
                    } else {
                        System.out.println("Balance is null in database.");
                        latch.countDown();
                    }
                } else {
                    System.out.println("Balance field does not exist.");
                    latch.countDown();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error: " + error.getMessage());
                latch.countDown();
            }
        });

        try {
            latch.await(); // Wait for the Firebase operation to complete
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return success[0];
    }

    public String getUserPin() {
        String currentUserId = auth.getCurrentUser().getUid();

        if (currentUserId == null) {
            return null; // User is not authenticated
        }

        DatabaseReference pinRef = database.child(currentUserId).child("pin");

        final String[] pin = {null}; // Variable to store the PIN
        CountDownLatch latch = new CountDownLatch(1); // To block until Firebase query completes

        pinRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    pin[0] = snapshot.getValue(String.class); // Retrieve the PIN value
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

        return pin[0]; // Return the PIN or null if not found
    }
}
