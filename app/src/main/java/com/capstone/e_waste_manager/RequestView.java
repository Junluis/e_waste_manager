package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class RequestView extends AppCompatActivity {

    TextView reqNameTV, reqAddressTV, reqNumberTV, reqDescTV, reqEmail;
    ImageView reqDTI, reqSEC;
    Button reqApprove, reqReject;
    ImageButton backButton;

    FirebaseFirestore fb;
    FirebaseAuth fAuth;
    DocumentReference dr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_view);

        fAuth = FirebaseAuth.getInstance();
        fb = FirebaseFirestore.getInstance();

        reqNameTV = findViewById(R.id.reqNameTV);
        reqAddressTV = findViewById(R.id.reqAddressTV);
        reqNumberTV = findViewById(R.id.reqNumberTV);
        reqDescTV = findViewById(R.id.reqDescTV);
        reqDTI = findViewById(R.id.reqDTIIV);
        reqSEC = findViewById(R.id.reqSECIV);
        reqApprove = (Button) findViewById(R.id.reqApprove);
        reqReject = (Button) findViewById(R.id.reqReject);
        backButton = findViewById(R.id.reqBackBtn);
        reqEmail = findViewById(R.id.reqEmailTV);

        String userDoc = getIntent().getStringExtra("reqUserId");
        String address = getIntent().getStringExtra("reqAddress");
        String dti = getIntent().getStringExtra("reqDTI");
        String desc = getIntent().getStringExtra("reqDesc");
        String name = getIntent().getStringExtra("reqName");
        String number = getIntent().getStringExtra("reqNumber");
        String sec = getIntent().getStringExtra("reqSEC");
        String email = getIntent().getStringExtra("reqUserMail");

        backButton.setOnClickListener(v -> {startActivity(new Intent(getApplicationContext(), RequestTab.class));});

        reqAddressTV.setText(address);
        Picasso.get().load(dti).into(reqDTI);
        reqDescTV.setText(desc);
        reqNameTV.setText(name);
        reqNumberTV.setText(number);
        Picasso.get().load(sec).into(reqSEC);
        reqEmail.setText(email);

        reqApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Accepted(userDoc);

            }
        });
        
        reqReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void Accepted(String userDoc) {
        dr = fb.collection("Users").document(userDoc);
//        Toast.makeText(RequestView.this, users, Toast.LENGTH_SHORT).show();
//        fStore.collection("Users").document(userDoc)
//                .update("Partner", "1").addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Toast.makeText(RequestView.this, userDoc, Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                });

    }


}