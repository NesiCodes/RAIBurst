package com.example.raiburst.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.raiburst.MainActivity;
import com.example.raiburst.R;
import com.example.raiburst.ui.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignup;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        etEmail = findViewById(R.id.email_input);
        etPassword = findViewById(R.id.password_input);
        btnLogin = findViewById(R.id.sign_in_button);
        tvSignup = findViewById(R.id.sign_up_text);

        // Sign in button click listener
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LogInActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase Authentication sign-in
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                Toast.makeText(LogInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                                // Navigate to home screen
                                Intent intent = new Intent(LogInActivity.this, MainActivity.class); // Replace with your home activity
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(LogInActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Signup link click listener
        tvSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LogInActivity.this, SignUpActivity.class); // Replace with your signup activity
            startActivity(intent);
        });
    }
}
