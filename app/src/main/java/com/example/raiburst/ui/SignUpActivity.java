package com.example.raiburst.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.raiburst.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SignUpActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etPassword, etBirthday, etMobile, etPin;
    private Button btnSignup;

    private FirebaseAuth auth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize UI elements
        etFirstName = findViewById(R.id.first_name);
        etLastName = findViewById(R.id.last_name);
        etEmail = findViewById(R.id.email_input);
        etPassword = findViewById(R.id.password);
        etBirthday = findViewById(R.id.birthday);
        etMobile = findViewById(R.id.login_mobile_number);
        etPin = findViewById(R.id.user_pin);
        btnSignup = findViewById(R.id.next_button);

        // Signup button click listener
        btnSignup.setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString();
            String lastName = etLastName.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String birthday = etBirthday.getText().toString();
            String mobile = etMobile.getText().toString();
            String pin = etPin.getText().toString();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || birthday.isEmpty() || mobile.isEmpty() || pin.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Register user in Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            if (firebaseUser != null) {
                                // Store additional user information in Realtime Database
                                String userId = firebaseUser.getUid();
                                User user = new User(firstName, lastName, email, birthday, mobile, pin, "5000");

                                database.child(userId).setValue(user)
                                        .addOnCompleteListener(dbTask -> {
                                            if (dbTask.isSuccessful()) {
                                                // Generate and save random friends for the user
                                                generateAndSaveFriends(userId);

                                                Toast.makeText(SignUpActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                                                finish(); // Close activity
                                            } else {
                                                Toast.makeText(SignUpActivity.this, "Failed to save user info", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(SignUpActivity.this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    // Function to generate and save random friends
    private void generateAndSaveFriends(String userId) {
        // List of random first names
        List<String> firstNames = Arrays.asList(
                "Alice", "Bob", "Charlie", "David", "Emma",
                "Fiona", "George", "Hannah", "Ivy", "Jack",
                "Karen", "Leo", "Mia", "Nora", "Oliver",
                "Paul", "Quinn", "Ruby", "Sophia", "Tom");

        // List of email domains for variety
        List<String> emailDomains = Arrays.asList(
                "@example.com", "@mail.com", "@email.com", "@service.com");

        Random random = new Random();
        DatabaseReference friendsRef = database.child(userId).child("friends");

        // Generate 9 random friends
        for (int i = 0; i < 9; i++) {
            String randomName = firstNames.get(random.nextInt(firstNames.size()));
            String randomEmail = randomName.toLowerCase() + random.nextInt(1000) + emailDomains.get(random.nextInt(emailDomains.size()));

            // Save friend to the "friends" node
            friendsRef.child(randomName).setValue(randomEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            System.out.println("Friend " + randomName + " saved successfully!");
                        } else {
                            System.err.println("Failed to save friend: " + task.getException());
                        }
                    });
        }
    }

    // User model class
    public static class User {
        public String firstName, lastName, email, birthday, mobile, pin, balance;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String firstName, String lastName, String email, String birthday, String mobile, String pin, String balance) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.birthday = birthday;
            this.mobile = mobile;
            this.pin = pin;
            this.balance = balance;
        }
    }
}
