package com.example.raiburst.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.raiburst.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView raiburstText = findViewById(R.id.raiburstText);

        String fullText = "RAIBURST";

        SpannableString spannable = new SpannableString(fullText);

        ForegroundColorSpan yellowSpan = new ForegroundColorSpan(getResources().getColor(R.color.yellow));
        spannable.setSpan(yellowSpan, 1, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        raiburstText.setText(spannable);

        TextView textView = findViewById(R.id.pleaseWait);


        Animation blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink);
        textView.startAnimation(blinkAnimation);


        textView.postDelayed(() -> {
            Intent intent = new Intent(SplashScreen.this, LogInActivity.class);
            startActivity(intent);
            finish();
        }, 1500);
    }
}