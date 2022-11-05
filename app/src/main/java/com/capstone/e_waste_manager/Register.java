package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import java.util.Calendar;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;



public class Register extends AppCompatActivity {
    EditText regUsername, regPassword, regConfPassword, regEmail, regFirstName, regLastName, dateOfBirth;
    Button regRegister;
    TextView regLogin, regPrivacyPolicy, regTermsService;
    boolean valid = true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();



        //date picker start
        dateOfBirth = findViewById(R.id.dateOfBirth);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        Register.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;
                        String date = day+"/"+month+"/"+year;
                        dateOfBirth.setText(date);
                    }
                }, year,month,day);
                datePickerDialog.show();
            }
        });
        //date picker end

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


        checkField(regUsername);
        checkField(regPassword);
        checkField(regConfPassword);
        checkField(regEmail);
        checkField(regFirstName);
        checkField(regLastName);
        checkField(dateOfBirth);

        regLogin.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Login.class)));

        regRegister.setOnClickListener(v -> {
//            if(valid){
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
                            userInfo.put("DateOfBirth", dateOfBirth.getText().toString());
                            userInfo.put("Partner", "0");
                            df.set(userInfo);
                            startActivity(new Intent(getApplicationContext(), Home.class));
                            finish();
                        }).addOnFailureListener(e -> Toast.makeText(Register.this, "Failed to Create Account", Toast.LENGTH_SHORT).show());
//            }
        });



    }

    public boolean checkField(EditText textField){
        if (textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }else{
            valid = true;
        }
        return valid;

    }
}