package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import java.util.Calendar;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;



public class Register extends AppCompatActivity {
    EditText regUsername, regPassword, regConfPassword, regEmail, regFirstName, regLastName, regdateOfBirth;
    Button regRegister;
    TextView regLogin;
    TextView regPrivacyPolicy;
    TextView regTermsService;
    TextInputLayout tilUsername, tilPassword, tilConfPassword, tilEmail, tilDateOfBirth, tilFirstName, tilLastName;
    boolean valid = true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String Dateval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        //text input layout
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfPassword = findViewById(R.id.tilConfPassword);
        tilEmail = findViewById(R.id.tilEmail);
        tilFirstName = findViewById(R.id.tilFirstName);
        tilLastName = findViewById(R.id.tilLastName);
        tilDateOfBirth = findViewById(R.id.tilDateOfBirth);

        //edit text
        regUsername = findViewById(R.id.regUsername);
        regPassword = findViewById(R.id.regPassword);
        regConfPassword = findViewById(R.id.regConfPassword);
        regEmail = findViewById(R.id.regEmail);
        regFirstName = findViewById(R.id.regFirstName);
        regLastName = findViewById(R.id.regLastName);
        regdateOfBirth = findViewById(R.id.dateOfBirth);

        //buttons
        regRegister = findViewById(R.id.regRegister);
        regLogin = findViewById(R.id.regLogin);
        regPrivacyPolicy = findViewById(R.id.regPrivacyPolicy);
        regTermsService = findViewById(R.id.regTermsService);

        //date picker start
        tilDateOfBirth = findViewById(R.id.tilDateOfBirth);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        tilDateOfBirth.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        Register.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;
                        int daylength = (int)(Math.log10(day)+1);
                        int monthlength = (int)(Math.log10(month)+1);
                        if (daylength < 2){
                            @SuppressLint("DefaultLocale") String daychange = String.format("%02d", day);
                            if(monthlength < 2){
                                @SuppressLint("DefaultLocale") String monthchange = String.format("%02d", month);
                                String date = daychange+"/"+monthchange+"/"+year;
                                regdateOfBirth.setText(date);
                            }else{
                                String date = daychange+"/"+month+"/"+year;
                                regdateOfBirth.setText(date);
                            }

                        }else if(monthlength < 2){
                            @SuppressLint("DefaultLocale") String monthchange = String.format("%02d", month);
                            String date = day+"/"+monthchange+"/"+year;
                            regdateOfBirth.setText(date);
                        }else{
                            String date = day+"/"+month+"/"+year;
                            regdateOfBirth.setText(date);
                        }
                    }
                }, year,month,day);
                datePickerDialog.show();
                regdateOfBirth.requestFocus();
            }
        });
        //date picker end

        //date mask start
        new DateInputMask(regdateOfBirth);
        //date mask end

        //checkField error start
        regUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilUsername.getChildCount() == 2)
                        tilUsername.getChildAt(1).setVisibility(View.VISIBLE);
                    tilUsername.setError("required*");
                }else{
                    tilUsername.setError(null);
                    if (tilUsername.getChildCount() == 2)
                        tilUsername.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        regPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilPassword.getChildCount() == 2)
                        tilPassword.getChildAt(1).setVisibility(View.VISIBLE);
                    tilPassword.setError("required*");
                    tilPassword.setErrorIconDrawable(0);
                }else if(s.length() < 6){
                    if (tilPassword.getChildCount() == 2)
                        tilPassword.getChildAt(1).setVisibility(View.VISIBLE);
                    tilPassword.setError("Use 6 characters or more for your password.");
                    tilPassword.setErrorIconDrawable(0);
                }else{
                    tilPassword.setError(null);
                    if (tilPassword.getChildCount() == 2)
                        tilPassword.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        regConfPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty() || !s.toString().equals(regPassword.getText().toString())){
                    if (tilConfPassword.getChildCount() == 2)
                        tilConfPassword.getChildAt(1).setVisibility(View.VISIBLE);
                    tilConfPassword.setError("Password does not match. Try again.");
                    tilConfPassword.setErrorIconDrawable(0);
                }else{
                    tilConfPassword.setError(null);
                    if (tilConfPassword.getChildCount() == 2)
                        tilConfPassword.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        regEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilEmail.getChildCount() == 2)
                        tilEmail.getChildAt(1).setVisibility(View.VISIBLE);
                    tilEmail.setError("required*");
                }else if(!Patterns.EMAIL_ADDRESS.matcher(s).matches()){
                    if (tilEmail.getChildCount() == 2)
                        tilEmail.getChildAt(1).setVisibility(View.VISIBLE);
                    tilEmail.setError("Invalid email.");
                }else{
                    tilEmail.setError(null);
                    if (tilEmail.getChildCount() == 2)
                        tilEmail.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        regdateOfBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean hasfocus) {
                if (hasfocus) {
                    regdateOfBirth.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                        }
                        @Override
                        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                            Dateval = s.toString().replaceAll("[^\\d./]", "");
                            if(Dateval.length() < 10){
                                tilDateOfBirth.setError("required*");
                                tilDateOfBirth.setErrorIconDrawable(0);
                            }else if(Dateval.length() > 10){
                                Dateval = Dateval.substring(0, 10);
                            }else{
                                tilDateOfBirth.setError(null);
                            }
                        }
                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });
                } else {
                    regdateOfBirth.setText(Dateval);
                }
            }
        });
        regFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilFirstName.getChildCount() == 2)
                        tilFirstName.getChildAt(1).setVisibility(View.VISIBLE);
                    tilFirstName.setError("required*");
                }else{
                    tilFirstName.setError(null);
                    if (tilFirstName.getChildCount() == 2)
                        tilFirstName.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        regLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilLastName.getChildCount() == 2)
                        tilLastName.getChildAt(1).setVisibility(View.VISIBLE);
                    tilLastName.setError("required*");
                }else{
                    tilLastName.setError(null);
                    if (tilLastName.getChildCount() == 2)
                        tilLastName.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //checkField error end

        regLogin.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Login.class)));

        regRegister.setOnClickListener(v -> {
//            if(valid){
            if(regUsername.getText().toString().length() == 0 || !TextUtils.isEmpty(tilUsername.getError())){
                if(regUsername.getText().toString().length() == 0)
                    regUsername.setText("");
                tilUsername.requestFocus();
            }else if(regPassword.getText().toString().length() == 0 || !TextUtils.isEmpty(tilPassword.getError())){
                if(regPassword.getText().toString().length() == 0)
                    regPassword.setText("");
                tilPassword.requestFocus();
            }else if(regConfPassword.getText().toString().length() == 0 || !TextUtils.isEmpty(tilConfPassword.getError())){
                if(regConfPassword.getText().toString().length() == 0)
                    regConfPassword.setText("");
                tilConfPassword.requestFocus();
            }else if(regEmail.getText().toString().length() == 0 || !TextUtils.isEmpty(tilEmail.getError())){
                if(regEmail.getText().toString().length() == 0)
                    regEmail.setText("");
                tilEmail.requestFocus();
            }else if(regdateOfBirth.getText().toString().length() == 0 || !TextUtils.isEmpty(tilDateOfBirth.getError())){
                if(regdateOfBirth.getText().toString().length() == 0)
                    regdateOfBirth.setText("");
                tilDateOfBirth.requestFocus();
                tilDateOfBirth.clearFocus();
                tilDateOfBirth.requestFocus();
            }else if(regFirstName.getText().toString().length() == 0 || !TextUtils.isEmpty(tilFirstName.getError())){
                if(regFirstName.getText().toString().length() == 0)
                    regFirstName.setText("");
                tilFirstName.requestFocus();
            }else if(regLastName.getText().toString().length() == 0 || !TextUtils.isEmpty(tilLastName.getError())){
                if(regLastName.getText().toString().length() == 0)
                    regLastName.setText("");
                tilLastName.requestFocus();
            }else{
                fAuth.createUserWithEmailAndPassword(regEmail.getText().toString(), regPassword.getText().toString())
                        .addOnSuccessListener(authResult -> {
                            FirebaseUser user = fAuth.getCurrentUser();
                            Toast.makeText(Register.this, "Account Created", Toast.LENGTH_SHORT).show();
                            DocumentReference df = fStore.collection("Users").document(user.getUid());
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("Username", regUsername.getText().toString());
                            userInfo.put("Email", regEmail.getText().toString());
                            userInfo.put("FirstName", regFirstName.getText().toString());
                            userInfo.put("LastName", regLastName.getText().toString());
                            userInfo.put("DateOfBirth", regdateOfBirth.getText().toString());
                            userInfo.put("Partner", "0");
                            df.set(userInfo);
                            startActivity(new Intent(getApplicationContext(), Home.class));
                            finish();
                        }).addOnFailureListener(e -> Toast.makeText(Register.this, "Failed to Create Account", Toast.LENGTH_SHORT).show());
            }
        });
    }
}