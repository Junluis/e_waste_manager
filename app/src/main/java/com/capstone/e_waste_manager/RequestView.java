package com.capstone.e_waste_manager;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class RequestView extends AppCompatActivity {

    TextView reqNameTV, reqAddressTV, reqNumberTV, reqDescTV, reqEmail, reqBarangayTV;
    ImageView reqDTI, reqSEC;
    Button reqApprove, reqReject;
    ImageButton backButton;

    //firebase
    FirebaseFirestore fStore;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirestoreRecyclerAdapter adapter;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_view);

        reqNameTV = findViewById(R.id.reqNameTV);
        reqAddressTV = findViewById(R.id.reqAddressTV);
        reqNumberTV = findViewById(R.id.reqNumberTV);
        reqDescTV = findViewById(R.id.reqDescTV);
        reqDTI = findViewById(R.id.reqDTIIV);
        reqSEC = findViewById(R.id.reqSECIV);
        reqApprove = findViewById(R.id.reqApprove);
        reqReject = findViewById(R.id.reqReject);
        backButton = findViewById(R.id.reqBackBtn);
        reqEmail = findViewById(R.id.reqEmailTV);
        reqBarangayTV = findViewById(R.id.reqBarangayTV);

        //firebase
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        backButton.setOnClickListener(v -> onBackPressed());

        //model
        RequestModel model = (RequestModel) getIntent().getSerializableExtra("model");

        reqNameTV.setText(model.reqName);
        reqAddressTV.setText(model.reqAddress);
        reqBarangayTV.setText(model.reqBarangay);
        reqNumberTV.setText(model.reqNumber);
        reqDescTV.setText(model.reqDesc);
        DocumentReference usernameReference = fStore.collection("Users").document(model.reqUserId);
        usernameReference.addSnapshotListener(RequestView.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                reqEmail.setText(documentSnapShot.getString("Email"));
            }
        });

        StorageReference DTIRef = storageReference.child("RequestRequirements/"+model.reqUserId+"/dti.jpg");
        DTIRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(reqDTI);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "No Image");
            }
        });

        StorageReference SECRef = storageReference.child("RequestRequirements/"+model.reqUserId+"/sec.jpg");
        SECRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(reqSEC);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "No Image");
            }
        });

        reqApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference docRef = fStore.collection("Users").document(model.reqUserId);
                Map<String, Object> edited = new HashMap<>();
                edited.put("Partner", "1");
                edited.put("Barangay", reqBarangayTV.getText().toString().trim());
                edited.put("HouseAddress", reqAddressTV.getText().toString().trim());
                edited.put("OrganizationDesc", reqDescTV.getText().toString().trim());
                edited.put("OrganizationName", reqNameTV.getText().toString().trim());
                edited.put("FirstName", FieldValue.delete());
                edited.put("LastName", FieldValue.delete());

                docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        DocumentReference docRef = fStore.collection("Request").document(model.docId);
                        Map<String, Object> doc = new HashMap<>();
                        doc.put("accepted", true);
                        doc.put("status", "accepted");

                        docRef.update(doc).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                onBackPressed();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RequestView.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RequestView.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        reqReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference docRef = fStore.collection("Users").document(model.reqUserId);
                Map<String, Object> doc = new HashMap<>();
                doc.put("isReq", false);

                docRef.update(doc).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        DocumentReference docRef = fStore.collection("Request").document(model.docId);
                        Map<String, Object> doc = new HashMap<>();
                        doc.put("accepted", false);
                        doc.put("status", "denied");

                        docRef.update(doc).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                onBackPressed();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RequestView.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RequestView.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }


}