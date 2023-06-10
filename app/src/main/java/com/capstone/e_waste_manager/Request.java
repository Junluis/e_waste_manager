package com.capstone.e_waste_manager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Request extends AppCompatActivity {


    ImageButton back;
    TextInputEditText prName, prAddress, prNumber, prDesc ;
    ImageView prDTI, prSEC;
    Button submit, btDTI, btSEC;

    AutoCompleteTextView regBarangay;
    ArrayAdapter<String> barangayList;
    List<String> barangay = new ArrayList<>();

    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    String userID;


    TextInputLayout tilName, tilAddress, tilBarangay, tilNumber, tilDesc;


    ActivityResultLauncher<String> galleryOpenDTI;
    ActivityResultLauncher<String> galleryOpenSEC;
    Uri DTIUri, SECUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        back = findViewById(R.id.closedr);
        btDTI = findViewById(R.id.btDTI);
        btSEC = findViewById(R.id.btSEC);
        prName = findViewById(R.id.prName);
        prAddress = findViewById(R.id.prAddress);
        prNumber = findViewById(R.id.prNumber);
        prDesc = findViewById(R.id.prDesc);
        prDTI = findViewById(R.id.prDTI);
        prSEC = findViewById(R.id.prSEC);
        submit = findViewById(R.id.prSubmit);
        regBarangay = findViewById(R.id.regBarangay);

        tilName = findViewById(R.id.textInputLayout3);
        tilAddress = findViewById(R.id.textInputLayout8);
        tilBarangay = findViewById(R.id.tilBarangay);
        tilNumber = findViewById(R.id.textInputLayout4);
        tilDesc = findViewById(R.id.textInputLayout9);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        back.setOnClickListener(v -> onBackPressed());

        DocumentReference documentReference = fStore.collection("Users").document(fAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                prAddress.setText(documentSnapShot.getString("HouseAddress"));
                regBarangay.setText(documentSnapShot.getString("Barangay"));
                prNumber.setText(documentSnapShot.getString("Number"));
            }
        });

        DocumentReference docRef = fStore.collection("Miscellaneous").document("cvUA8BB7Pk0Ud7kYwxoT");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                barangay = (List<String>) documentSnapshot.get("Barangay");
//                for(String log : barangay)
//                {
//                    Log.e("Tag",log);
//                }
                if (barangay != null) {
                    Collections.sort(barangay, String.CASE_INSENSITIVE_ORDER);
                    barangayList = new ArrayAdapter<String>(Request.this, R.layout.dropdown_barangay, barangay);
                    regBarangay.setAdapter(barangayList);
                }
            }
        });

        //update profile icon
        galleryOpenDTI = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri imageUri) {
                if (imageUri != null && !imageUri.equals(Uri.EMPTY)){
                    DTIUri = imageUri;
                    prDTI.setImageURI(imageUri);
                }
            }
        });
        galleryOpenSEC = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri imageUri) {
                if (imageUri != null && !imageUri.equals(Uri.EMPTY)){
                    SECUri = imageUri;
                    prSEC.setImageURI(imageUri);
                }
            }
        });

        btDTI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryOpenDTI.launch("image/");
            }
        });

        btSEC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryOpenSEC.launch("image/");
            }
        });

        prName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilName.getChildCount() == 2)
                        tilName.getChildAt(1).setVisibility(View.VISIBLE);
                    tilName.setError("required*");
                }else{
                    tilName.setError(null);
                    if (tilName.getChildCount() == 2)
                        tilName.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        prAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilAddress.getChildCount() == 2)
                        tilAddress.getChildAt(1).setVisibility(View.VISIBLE);
                    tilAddress.setError("required*");
                }else{
                    tilAddress.setError(null);
                    if (tilAddress.getChildCount() == 2)
                        tilAddress.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        regBarangay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilBarangay.getChildCount() == 2)
                        tilBarangay.getChildAt(1).setVisibility(View.VISIBLE);
                    tilBarangay.setError("required*");
                    tilBarangay.setErrorIconDrawable(0);
                } else if (!s.toString().isEmpty()) {
                    for (int z = 0; z <= barangay.size() - 1; z++) {
                        if (!barangay.contains(s.toString())) {
                            if (tilBarangay.getChildCount() == 2)
                                tilBarangay.getChildAt(1).setVisibility(View.VISIBLE);
                            tilBarangay.setError("Select barangay.");
                            tilBarangay.setErrorIconDrawable(0);
                        }else{
                            tilBarangay.setError(null);
                            if (tilBarangay.getChildCount() == 2)
                                tilBarangay.getChildAt(1).setVisibility(View.GONE);
                            break;
                        }
                    }
                }else {
                    tilBarangay.setError(null);
                    if (tilBarangay.getChildCount() == 2)
                        tilBarangay.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        prNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().startsWith("0")){
                    prNumber.setText(s.toString().replaceFirst("0",""));
                }else if(s.length() > 10){
                    if (tilNumber.getChildCount() == 2)
                        tilNumber.getChildAt(1).setVisibility(View.VISIBLE);
                    tilNumber.setError("Invalid number.");
                }else{
                    tilNumber.setError(null);
                    if (tilNumber.getChildCount() == 2)
                        tilNumber.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        prDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    tilDesc.setError("required*");
                } else if(!(s.toString().length() <= 160)){
                    tilDesc.setError("Oops! You run out of characters.");
                }else{
                    tilDesc.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(prName.getText().toString().length() == 0 || !TextUtils.isEmpty(tilName.getError())){
                    if(prName.getText().toString().length() == 0)
                        prName.setText("");
                    prName.requestFocus();
                }else if(prAddress.getText().toString().length() == 0 || !TextUtils.isEmpty(tilAddress.getError())){
                    if(prAddress.getText().toString().length() == 0)
                        prAddress.setText("");
                    prAddress.requestFocus();
                }else if(regBarangay.getText().toString().length() == 0 || !TextUtils.isEmpty(tilBarangay.getError())){
                    if(regBarangay.getText().toString().length() == 0)
                        regBarangay.setText("");
                    regBarangay.requestFocus();
                }else if(prNumber.getText().toString().length() == 0 || !TextUtils.isEmpty(tilNumber.getError())){
                    if(prNumber.getText().toString().length() == 0)
                        prNumber.setText("");
                    prNumber.requestFocus();
                }else if(prDesc.getText().toString().length() == 0 || !TextUtils.isEmpty(tilDesc.getError())){
                    if(prDesc.getText().toString().length() == 0)
                        prDesc.setText("");
                    prDesc.requestFocus();
                }else if(DTIUri == null || DTIUri.equals(Uri.EMPTY)){
                    Toast.makeText(Request.this, "Please upload DTI permit", Toast.LENGTH_SHORT).show();
                }else if(SECUri == null || SECUri.equals(Uri.EMPTY)){
                    Toast.makeText(Request.this, "Please upload SEC permit", Toast.LENGTH_SHORT).show();
                }else{
                    //upload Image to Firebase
                    uploadDTIImageToFirebase(DTIUri);
                    uploadSECImageToFirebase(SECUri);
                    uploadData();
                }
            }
        });


    }

    private void uploadData() {
        fStore.collection("Users").document(userID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null){
                String id = task.getResult().getString("Partner");
                if(Objects.equals(id, "0")) {
                    Map<String, Object> doc = new HashMap<>();
                    doc.put("status", "pending");
                    doc.put("reqName", prName.getText().toString().trim());
                    doc.put("reqAddress", prAddress.getText().toString().trim());
                    doc.put("reqBarangay", regBarangay.getText().toString().trim());
                    doc.put("reqNumber", prNumber.getText().toString().trim());
                    doc.put("reqDesc", prDesc.getText().toString().trim());
                    doc.put("reqUserId", userID);
                    doc.put("reqDate", FieldValue.serverTimestamp());

                    fStore.collection("Request").add(doc).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            DocumentReference docRef = fStore.collection("Users").document(user.getUid());
                            Map<String, Object> edited = new HashMap<>();
                            edited.put("isReq", true);
                            docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });

                            Toast.makeText(Request.this, "Post Successful", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Request.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    });
                }
            }else{
                Toast.makeText(Request.this, "Error Getting User Data", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void uploadDTIImageToFirebase(Uri imageUri){
        //upload Image to Firebase storage
        StorageReference fileRef = storageReference.child("RequestRequirements/"+fAuth.getCurrentUser().getUid()+"/dti.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Request.this, "Failed to update profile picture.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void uploadSECImageToFirebase(Uri imageUri){
        //upload Image to Firebase storage
        StorageReference fileRef = storageReference.child("RequestRequirements/"+fAuth.getCurrentUser().getUid()+"/sec.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Request.this, "Failed to update profile picture.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}