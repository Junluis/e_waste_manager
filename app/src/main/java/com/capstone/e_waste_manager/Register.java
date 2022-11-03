package com.capstone.e_waste_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class Register extends AppCompatActivity {

    TextInputEditText regUsername, regPassword, regConfPassword, regEmail, regFirstName, regLastName;
    EditText dateOfBirth;
    Button regRegister;
    TextView regLogin, regPrivacyPolicy, regTermsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regUsername = findViewById(R.id.regUsername);
        regPassword = findViewById(R.id.regPassword);
        regConfPassword = findViewById(R.id.regConfPassword);
        regEmail = findViewById(R.id.regEmail);
        regFirstName = findViewById(R.id.regFirstName);
        regLastName = findViewById(R.id.regLastName);
        dateOfBirth = findViewById(R.id.dateOfBirth);
        regRegister = findViewById(R.id.regRegister);
        regLogin = findViewById(R.id.regLogin);
        regPrivacyPolicy = findViewById(R.id.regPrivacyPolicy);
        regTermsService = findViewById(R.id.regTermsService);
    }
}