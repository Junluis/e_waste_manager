package com.capstone.e_waste_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import java.util.Calendar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;


public class Register extends AppCompatActivity {
    EditText dateOfBirth;

    TextInputEditText regUsername, regPassword, regConfPassword, regEmail, regFirstName, regLastName;
    EditText dateOfBirth;
    Button regRegister;
    TextView regLogin, regPrivacyPolicy, regTermsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



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

    }
}