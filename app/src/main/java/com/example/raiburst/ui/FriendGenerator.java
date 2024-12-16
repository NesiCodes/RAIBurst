package com.example.raiburst.ui;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FriendGenerator {

    private FirebaseAuth auth;
    private DatabaseReference database;

    public FriendGenerator() {
        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
    }

    public void generateAndSaveFriends(int numberOfFriends) {
        // List of random first names
        List<String> firstNames = Arrays.asList(
                "Alice", "Bob", "Charlie", "David", "Emma", "Fiona", "George", "Hannah", "Ivy", "Jack",
                "Karen", "Leo", "Mia", "Nora", "Oliver", "Paul", "Quinn", "Ruby", "Sophia", "Tom",
                "Uma", "Victor", "Wendy", "Xander", "Yara", "Zoe", "Liam", "Noah", "Elijah", "Logan",
                "Lucas", "Mason", "Ethan", "James", "Alexander", "William", "Benjamin", "Matthew");

        // List of email domains for variety
        List<String> emailDomains = Arrays.asList(
                "@example.com", "@mail.com", "@email.com", "@service.com", "@test.com");

        Random random = new Random();
        DatabaseReference friendsRef = database.child("friends");

        List<String> generatedFriends = new ArrayList<>();

        for (int i = 0; i < numberOfFriends; i++) {
            // Generate a random name
            String randomName;
            do {
                randomName = firstNames.get(random.nextInt(firstNames.size()));
            } while (generatedFriends.contains(randomName)); // Ensure names are unique for this run

            generatedFriends.add(randomName);

            // Generate a random email
            String randomEmail = randomName.toLowerCase() + random.nextInt(1000) + emailDomains.get(random.nextInt(emailDomains.size()));

            // Save friend to the "friends" node
            String finalRandomName = randomName;
            friendsRef.child(randomName).setValue(randomEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            System.out.println("Friend " + finalRandomName + " saved successfully!");
                        } else {
                            System.err.println("Failed to save friend: " + task.getException());
                        }
                    });
        }
    }
}
