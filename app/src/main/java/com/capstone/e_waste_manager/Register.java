package com.capstone.e_waste_manager;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.widget.ImageButton;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import java.util.Calendar;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Register extends AppCompatActivity {
    EditText regUsername, regPassword, regConfPassword, regEmail, regFirstName, regLastName, regdateOfBirth;
    Button regRegister;
    ImageButton closereg;
    TextView regLogin;
    TextView regPrivacyPolicy;
    TextView regTermsService;
    TextInputLayout tilUsername, tilPassword, tilConfPassword, tilEmail, tilDateOfBirth, tilFirstName, tilLastName;
    boolean valid = true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String Dateval;
    Integer ageInteger = 0;

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
        closereg = findViewById(R.id.closereg);

        closereg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //date picker start

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
                }else if(s.length() < 6 || !s.toString().matches("[a-zA-Z0-9._-]+")){
                    if (tilUsername.getChildCount() == 2)
                        tilUsername.getChildAt(1).setVisibility(View.VISIBLE);
                    tilUsername.setError("Must be at least 6 characters and cannot contain spaces and special characters other than \".\" \"-\" \"_\".");
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
                                Calendar today = Calendar.getInstance();
                                int cYear = Integer.parseInt(regdateOfBirth.getText().toString().substring(regdateOfBirth.length() - 4, regdateOfBirth.length()));
                                int cMonth = Integer.parseInt(regdateOfBirth.getText().toString().substring(3, 5));
                                int cday = Integer.parseInt(regdateOfBirth.getText().toString().substring(0, 2));

                                if (cYear == today.get(Calendar.YEAR)) {
                                    if (today.get(Calendar.MONTH) + 1 == cMonth) {
                                        if (today.get(Calendar.DAY_OF_MONTH) < cday) {
                                            tilDateOfBirth.setError("Are you a time traveler?");
                                            tilDateOfBirth.setErrorIconDrawable(0);
                                        }else{
                                            tilDateOfBirth.setError("You must be over 13 to register.");
                                            tilDateOfBirth.setErrorIconDrawable(0);
                                        }
                                    }else if(today.get(Calendar.MONTH) + 1 < cMonth){
                                        tilDateOfBirth.setError("Are you a time traveler?");
                                        tilDateOfBirth.setErrorIconDrawable(0);
                                    }else{
                                        tilDateOfBirth.setError("You must be over 13 to register.");
                                        tilDateOfBirth.setErrorIconDrawable(0);
                                    }
                                }else if (cYear > today.get(Calendar.YEAR)) {
                                    tilDateOfBirth.setError("Are you a time traveler?");
                                    tilDateOfBirth.setErrorIconDrawable(0);
                                }else if(cYear < today.get(Calendar.YEAR)){
                                    if ((today.get(Calendar.YEAR) - cYear) == 13 ) {
                                        if (today.get(Calendar.MONTH) + 1 == cMonth) {
                                            if (today.get(Calendar.DAY_OF_MONTH) < cday) {
                                                tilDateOfBirth.setError("You must be over 13 to register.");
                                                tilDateOfBirth.setErrorIconDrawable(0);
                                            }else{
                                                ageInteger = today.get(Calendar.YEAR) - cYear;
                                                if (today.get(Calendar.MONTH) + 1 == cMonth) {
                                                    if (today.get(Calendar.DAY_OF_MONTH) < cday) {
                                                        ageInteger = ageInteger - 1;
                                                    }
                                                } else if (today.get(Calendar.MONTH) < cMonth) {
                                                    ageInteger = ageInteger - 1;
                                                }
                                                tilDateOfBirth.setError(null);
                                            }
                                        }else if (today.get(Calendar.MONTH) + 1 < cMonth) {
                                            tilDateOfBirth.setError("You must be over 13 to register.");
                                            tilDateOfBirth.setErrorIconDrawable(0);
                                        }else{
                                            ageInteger = today.get(Calendar.YEAR) - cYear;
                                            if (today.get(Calendar.MONTH) + 1 == cMonth) {
                                                if (today.get(Calendar.DAY_OF_MONTH) < cday) {
                                                    ageInteger = ageInteger - 1;
                                                }
                                            } else if (today.get(Calendar.MONTH) < cMonth) {
                                                ageInteger = ageInteger - 1;
                                            }

                                            tilDateOfBirth.setError(null);
                                        }
                                    }else if ((today.get(Calendar.YEAR) - cYear) < 13 ){
                                        tilDateOfBirth.setError("You must be over 13 to register.");
                                        tilDateOfBirth.setErrorIconDrawable(0);
                                    }else{
                                        ageInteger = today.get(Calendar.YEAR) - cYear;
                                        if (today.get(Calendar.MONTH) + 1 == cMonth) {
                                            if (today.get(Calendar.DAY_OF_MONTH) < cday) {
                                                ageInteger = ageInteger - 1;
                                            }
                                        } else if (today.get(Calendar.MONTH) < cMonth) {
                                            ageInteger = ageInteger - 1;
                                        }
                                        tilDateOfBirth.setError(null);
                                    }

                                } else {
                                    ageInteger = today.get(Calendar.YEAR) - cYear;
                                    if (today.get(Calendar.MONTH) + 1 == cMonth) {
                                        if (today.get(Calendar.DAY_OF_MONTH) < cday) {
                                            ageInteger = ageInteger - 1;
                                        }
                                    } else if (today.get(Calendar.MONTH) < cMonth) {
                                        ageInteger = ageInteger - 1;
                                    }
                                    tilDateOfBirth.setError(null);
                                }
                            }
                        }
                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });
                } else {
                    regdateOfBirth.setText(Dateval);
                    if (ageInteger < 13) {
                        tilDateOfBirth.setError("You must be over 13 to register.");
                        tilDateOfBirth.setErrorIconDrawable(0);
                    }
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
                }else if(!s.toString().matches("[a-zA-Z ]+")){
                    if (tilFirstName.getChildCount() == 2)
                        tilFirstName.getChildAt(1).setVisibility(View.VISIBLE);
                    tilFirstName.setError("Special characters and numeric values are not allowed.");
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
                }else if(!s.toString().matches("[a-zA-Z ]+")){
                    if (tilLastName.getChildCount() == 2)
                        tilLastName.getChildAt(1).setVisibility(View.VISIBLE);
                    tilLastName.setError("Special characters and numeric values are not allowed.");
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

        regLogin.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });

        regRegister.setOnClickListener(v -> {
            if(regUsername.getText().toString().length() == 0 || !TextUtils.isEmpty(tilUsername.getError())){
                if(regUsername.getText().toString().length() == 0)
                    regUsername.setText("");
                regUsername.requestFocus();
            }else if(regPassword.getText().toString().length() == 0 || !TextUtils.isEmpty(tilPassword.getError())){
                if(regPassword.getText().toString().length() == 0)
                    regPassword.setText("");
                regPassword.requestFocus();
            }else if(regConfPassword.getText().toString().length() == 0 || !TextUtils.isEmpty(tilConfPassword.getError())){
                if(regConfPassword.getText().toString().length() == 0)
                    regConfPassword.setText("");
                regConfPassword.requestFocus();
            }else if(regEmail.getText().toString().length() == 0 || !TextUtils.isEmpty(tilEmail.getError())){
                if(regEmail.getText().toString().length() == 0)
                    regEmail.setText("");
                regEmail.requestFocus();
            }else if(regdateOfBirth.getText().toString().length() == 0 || !TextUtils.isEmpty(tilDateOfBirth.getError())) {
                if(regdateOfBirth.getText().toString().length() == 0)
                    regdateOfBirth.setText("");
                regdateOfBirth.requestFocus();
            }else if(regFirstName.getText().toString().length() == 0 || !TextUtils.isEmpty(tilFirstName.getError())){
                if(regFirstName.getText().toString().length() == 0)
                    regFirstName.setText("");
                regFirstName.requestFocus();
            }else if(regLastName.getText().toString().length() == 0 || !TextUtils.isEmpty(tilLastName.getError())){
                if(regLastName.getText().toString().length() == 0)
                    regLastName.setText("");
                regLastName.requestFocus();
            }else if(!isNetworkAvailable()){
                startActivity(new Intent(getApplicationContext(), NoConnection.class));
                finish();
            }else{
                regdateOfBirth.clearFocus();
                fAuth.createUserWithEmailAndPassword(regEmail.getText().toString(), regPassword.getText().toString())
                        .addOnSuccessListener(authResult -> {
                            FirebaseUser user = fAuth.getCurrentUser();
                            Toast.makeText(Register.this, "Account Created", Toast.LENGTH_SHORT).show();
                            DocumentReference df = fStore.collection("Users").document(user.getUid());
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("Username", regUsername.getText().toString());
                            userInfo.put("Email", regEmail.getText().toString());
                            userInfo.put("FirstName", regFirstName.getText().toString().substring(0,1).toUpperCase() + regFirstName.getText().toString().substring(1).toLowerCase());
                            userInfo.put("LastName", regLastName.getText().toString().substring(0,1).toUpperCase() + regLastName.getText().toString().substring(1).toLowerCase());
                            userInfo.put("DateOfBirth", regdateOfBirth.getText().toString());
                            userInfo.put("Partner", "0");
                            df.set(userInfo);
                            checkUserAccessLevel(authResult.getUser().getUid());
                        }).addOnFailureListener(e -> Toast.makeText(Register.this, "Failed to Create Account. Email Already in Use.", Toast.LENGTH_LONG).show());
            }
        });
    }

    public void checkUserAccessLevel(String uid) {
        DocumentReference df = fStore.collection("Users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG", "onSuccess: "+ documentSnapshot.getData());

                if(Objects.equals(documentSnapshot.getString("Partner"), "0")){
                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                if(Objects.equals(documentSnapshot.getString("Partner"), "1")){
                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() !=null && !FirebaseAuth.getInstance().getCurrentUser().isAnonymous()){
            startActivity(new Intent(getApplicationContext(), Home.class));
            finish();
        }else{
            FirebaseAuth.getInstance().signOut();
        }
    }

    //check connection
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}