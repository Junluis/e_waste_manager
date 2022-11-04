package com.capstone.e_waste_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class Login extends AppCompatActivity {

    TextInputEditText loginUsername, loginPassword;
    Button loginBtnLogin, loginBtnReg;
    TextView loginForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.LoginUsername);
        loginPassword = findViewById(R.id.LoginPassword);
        loginBtnLogin = findViewById(R.id.loginBtnLogin);
        loginBtnReg = findViewById(R.id.loginBtnReg);
        loginForgot = findViewById(R.id.loginForgot);

    }
}