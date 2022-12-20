package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Login extends AppCompatActivity {

    TextInputLayout tilEmail, tilPassword;
    TextInputEditText loginEmail, loginPassword;
    Button loginBtnLogin, loginBtnReg;
    TextView loginForgot;
    ImageButton closelog;

    //firebase
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    boolean valid = true;

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

        tilEmail =  findViewById(R.id.tilEmail);
        tilPassword =  findViewById(R.id.tilPassword);

        closelog =  findViewById(R.id.closelog);
        closelog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loginEmail.addTextChangedListener(new TextWatcher() {
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
        loginPassword.addTextChangedListener(new TextWatcher() {
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

        loginBtnReg.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Register.class));
            finish();
        });

        loginBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginEmail.getText().toString().length() == 0 || !TextUtils.isEmpty(tilEmail.getError())){
                    if(loginEmail.getText().toString().length() == 0)
                        loginEmail.setText("");
                    loginEmail.requestFocus();
                } else if(loginPassword.getText().toString().length() == 0 || !TextUtils.isEmpty(tilPassword.getError())){
                    if(loginPassword.getText().toString().length() == 0)
                        loginPassword.setText("");
                    loginPassword.requestFocus();
                } else if(!isNetworkAvailable()){
                    startActivity(new Intent(getApplicationContext(), NoConnection.class));
                    finish();
                } else {

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
                                    Toast.makeText(Login.this, "Login failed. Your email or password is incorrect. Please try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
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
                }else{
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

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}