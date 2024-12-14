package com.example.raiburst.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.raiburst.R;

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView welcomeText = findViewById(R.id.welcome_text);
        String fullText = "Welcome to RAIBURST";
        SpannableString spannable = new SpannableString(fullText);
        ForegroundColorSpan yellowSpan = new ForegroundColorSpan(getResources().getColor(R.color.yellow));
        spannable.setSpan(yellowSpan, 11, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        welcomeText.setText(spannable);
        TextView signUpText = findViewById(R.id.sign_up_text);

           //Telksti i 2
        String fullText2 = "Didn't have an account? SIGN UP NOW";
        SpannableString spannable2 = new SpannableString(fullText2);

        int startIndex2 = fullText2.indexOf("SIGN UP NOW");
        int endIndex2 = startIndex2 + "SIGN UP NOW".length();

        int yellowColor = ContextCompat.getColor(this, R.color.yellow);

        spannable2.setSpan(new ForegroundColorSpan(yellowColor),
                startIndex2, endIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        signUpText.setText(spannable2);

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to move to SignUpActivity
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(intent); // Start the SignUpActivity
            }
        });



    }
}