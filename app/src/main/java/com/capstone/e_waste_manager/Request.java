package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

public class Request extends AppCompatActivity {

    int REQUEST_CODE_DTI = 101;
    int REQUEST_CODE_SEC = 102;
    ImageButton back;
    TextInputEditText prName, prAddress, prNumber, prDesc;
    ImageView prDTI, prSEC;
    Button submit, btDTI, btSEC;
    Uri uriDTI, uriSEC;
    boolean isImageAdded;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    DatabaseReference DataRef;
    StorageReference sr;

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

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        DataRef = FirebaseDatabase.getInstance().getReference().child("Users");
        sr = FirebaseStorage.getInstance().getReference().child("DocImage");

        back.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Home.class)));

        btDTI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_DTI);
            }
        });

        btSEC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_SEC);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reqName = prName.getText().toString();
                String reqAddress = prAddress.getText().toString();
                String reqNumber = prNumber.getText().toString();
                String reqDesc = prDesc.getText().toString();
                if (isImageAdded != false && prDTI != null && prSEC != null){
                    uploadRequest(reqName, reqAddress, reqNumber, reqDesc);
                }
                finish();
            }
        });

    }

    private void uploadRequest(final String reqName, final String reqAddress,
                               final String reqNumber, final String reqDesc) {

        String key = DataRef.push().getKey();
        sr.child(key+".jpg").putFile(uriDTI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                sr.child(key+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap hashMap = new HashMap();
                        hashMap.put("reqName", reqName);
                        hashMap.put("reqAddress", reqAddress);
                        hashMap.put("reqNumber", reqNumber);
                        hashMap.put("reqDesc", reqDesc);
                        hashMap.put("reqDTI", uriDTI.toString());
                        hashMap.put("reqSEC", uriSEC.toString());

                        fStore.collection("Request").add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                Toast.makeText(Request.this, "Data Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
//                        sr.child(key+".jpg").putFile(uriSEC).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                sr.child(key+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                    @Override
//                                    public void onSuccess(Uri uri) {
//
//                                    }
//                                });
//                            }
//                        });
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE_DTI && data!=null){
            uriDTI=data.getData();
            isImageAdded=true;
            prDTI.setImageURI(uriDTI);
        }
        if (requestCode==REQUEST_CODE_SEC && data!=null){
            uriSEC=data.getData();
            isImageAdded=true;
            prSEC.setImageURI(uriSEC);
        }
    }
}