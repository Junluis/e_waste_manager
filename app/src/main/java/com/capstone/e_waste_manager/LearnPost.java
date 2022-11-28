package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
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

public class LearnPost extends AppCompatActivity {

    int REQUEST_CODE_IMAGE = 101;
    EditText LearnPostTitle, LearnPostBody, LearnPostTag;
    MaterialButton LearnPostButton, UploadImage;
    ImageButton closepg;
    ImageView LearnPostCover;
    Uri imageUri;
    boolean isImageAdded;

    FirebaseAuth fAuth;

    FirebaseFirestore fStore;
    DatabaseReference DataRef;
    StorageReference sr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_post);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        DataRef = FirebaseDatabase.getInstance().getReference().child("LearningMaterials");
        sr = FirebaseStorage.getInstance().getReference().child("CoverImages");

        LearnPostTitle = findViewById(R.id.LearnPostTitle);
        LearnPostBody = findViewById(R.id.LearnPostBody);
        LearnPostTag = findViewById(R.id.LearnPostTag);
        LearnPostButton = findViewById(R.id.LearnPostButton);
        UploadImage = findViewById(R.id.UploadImage);
        LearnPostCover = findViewById(R.id.LearnPostCover);
        closepg = findViewById(R.id.closepg);


        closepg.setOnClickListener(v -> {
            onBackPressed();
        });


//
//        if (fAuth.getCurrentUser()!=null){
//
//        }else{
//
//        }

        UploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
                LearnPostCover.setVisibility(View.VISIBLE);
            }
        });

        LearnPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lpTitle = LearnPostTitle.getText().toString();
                String lpBody = LearnPostBody.getText().toString();
                String lpTag = LearnPostTag.getText().toString();
                if (isImageAdded != false && LearnPostCover != null){
                    uploadImage(lpTitle, lpBody, lpTag);
                }
                finish();
            }
        });

    }

    private void uploadImage(final String lpTitle, final String lpBody, final String lpTag) {


        String key = DataRef.push().getKey();
        sr.child(key+".jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                sr.child(key+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        fStore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult()!=null){
                                        String id = task.getResult().getString("Partner");
                                        if(Objects.equals(id, "1")){
                                            String username = task.getResult().getString("Username");
                                            HashMap hashMap = new HashMap();
                                            hashMap.put("learnTitle", lpTitle);
                                            hashMap.put("learnBody", lpBody);
                                            hashMap.put("learnTag", lpTag);
                                            hashMap.put("learnAuthor", username);
                                            hashMap.put("learnImage", uri.toString());

                                            fStore.collection("LearningMaterial").add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    Toast.makeText(LearnPost.this, "Data Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            });
                                        }else{
                                            Toast.makeText(LearnPost.this, "This account is not authorized", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }

                                    }else{
                                        Toast.makeText(LearnPost.this, "Error Getting User Data", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                    }
                });

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE_IMAGE && data!=null){
            imageUri=data.getData();
            isImageAdded=true;
            LearnPostCover.setImageURI(imageUri);
        }
    }
}