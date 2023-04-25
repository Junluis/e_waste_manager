package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Rewards extends AppCompatActivity {

    ImageView closedr;
    TextView empoints;
    Button rewardA, rewardB;

    //firebase
    FirebaseFirestore fStore;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirestorePagingAdapter<HomeModel, Home.ViewHolder> adapter;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        //firebase
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        closedr = findViewById(R.id.closedr);
        empoints = findViewById(R.id.empoints);
        rewardA = findViewById(R.id.rewardA);
        rewardB = findViewById(R.id.rewardB);

        closedr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        DocumentReference documentReference = fStore.collection("Users").document(user.getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapShot.getDouble("EMPoints") != null) {
                    empoints.setText(Objects.requireNonNull(documentSnapShot.getDouble("EMPoints")).toString());
                }else{
                    empoints.setText("0");
                }
            }
        });

        rewardA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!empoints.getText().toString().equals("0")){
                    double points = Double.parseDouble(empoints.getText().toString());

                    if(points >= 1){
                        DocumentReference status = fStore.collection("Users").document(user.getUid());
                        Map<String, Object> edited = new HashMap<>();

                        edited.put("EMPoints", FieldValue.increment(-1));

                        status.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Rewards.this, "Reward claimed", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Rewards.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        Toast.makeText(Rewards.this, "Insufficient points.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(Rewards.this, "Insufficient points.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rewardB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!empoints.getText().toString().equals("0")){
                    double points = Double.parseDouble(empoints.getText().toString());

                    if(points >= 2){
                        DocumentReference status = fStore.collection("Users").document(user.getUid());
                        Map<String, Object> edited = new HashMap<>();

                        edited.put("EMPoints", FieldValue.increment(-2));

                        status.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Rewards.this, "Reward claimed", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Rewards.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        Toast.makeText(Rewards.this, "Insufficient points.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(Rewards.this, "Insufficient points.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}