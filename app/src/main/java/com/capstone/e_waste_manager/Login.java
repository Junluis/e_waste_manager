package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    TextInputEditText loginEmail, loginPassword;
    Button loginBtnLogin, loginBtnReg;
    TextView loginForgot;
    boolean valid = true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        loginEmail = findViewById(R.id.LoginEmail);
        loginPassword = findViewById(R.id.LoginPassword);
        loginBtnLogin = findViewById(R.id.loginBtnLogin);
        loginBtnReg = findViewById(R.id.loginBtnReg);
        loginForgot = findViewById(R.id.loginForgot);


        loginBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkField(loginEmail);
                checkField(loginPassword);

                if(valid){
                    fAuth.signInWithEmailAndPassword(loginEmail.getText().toString(), loginPassword.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(Login.this, "Logged in Success", Toast.LENGTH_SHORT).show();
                                    checkUserAccessLevel(authResult.getUser().getUid());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }
            }
        });

        loginBtnReg.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Register.class)));

    }

    public void checkUserAccessLevel(String uid) {
        DocumentReference df = fStore.collection("Users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG", "onSuccess: "+ documentSnapshot.getData());

                if(documentSnapshot.getString("Partner")!= null){
                    finish();
                }
                if(documentSnapshot.getString("Partner")=="1"){
                    finish();
                }
            }
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

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() !=null){
            startActivity(new Intent(getApplicationContext(), Home.class));
            finish();
        }
    }

}