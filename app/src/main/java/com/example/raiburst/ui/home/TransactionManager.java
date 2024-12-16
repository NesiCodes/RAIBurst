package com.example.raiburst.ui.home;

import com.example.raiburst.ui.home.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TransactionManager {

    private FirebaseAuth auth;
    private DatabaseReference database;

    public TransactionManager() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("transactions");
    }

    public void addTransaction(String message) {
        // Get the current user's ID
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId == null) {
            System.out.println("User not authenticated");
            return;
        }

        // Create a unique ID for the transaction
        String transactionId = database.push().getKey();

        // Create a transaction object
        Transaction transaction = new Transaction(userId, message, System.currentTimeMillis());

        // Save the transaction to Firebase
        database.child(transactionId).setValue(transaction)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Transaction saved successfully.");
                    } else {
                        System.out.println("Failed to save transaction: " + task.getException());
                    }
                });
    }
}
