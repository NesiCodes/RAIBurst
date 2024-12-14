package com.example.raiburst.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.raiburst.R;
import com.hbb20.CountryCodePicker;

public class SignUpActivity extends AppCompatActivity {

    String phoneNumber;
    EditText phoneInput;
    Button nextButton;
    CountryCodePicker countryCodePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nextButton = findViewById(R.id.next_button);
        countryCodePicker = findViewById(R.id.login_countrycode);
        phoneInput = findViewById(R.id.login_mobile_number);
        phoneNumber = getIntent().getExtras().getString("phone");

        countryCodePicker.registerCarrierNumberEditText(phoneInput);


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!countryCodePicker.isValidFullNumber()){
                    phoneInput.setError("Phone number not valid");
                    return;
                }
                // Create an intent to move to SignUpActivity

                Intent intent = new Intent(SignUpActivity.this, SignUpActivityOTP.class);
                intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
                startActivity(intent); // Start the SignUpActivity
            }
        });


    }
}