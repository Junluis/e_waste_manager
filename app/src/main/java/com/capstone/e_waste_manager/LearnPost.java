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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class LearnPost extends AppCompatActivity {

    int REQUEST_CODE_IMAGE = 101;
    EditText LearnPostTitle, LearnPostBody, LearnPostTag;
    Button LearnPostButton, UploadImage;
    ImageView LearnPostCover;
    Uri imageUri;
    boolean isImageAdded;

    FirebaseFirestore fStore;
    DocumentReference df;
    DatabaseReference dr;
    StorageReference sr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_post);

        fStore = FirebaseFirestore.getInstance();
        df = FirebaseFirestore.getInstance().document("LearningMaterials");
        dr = FirebaseDatabase.getInstance().getReference().child("LearningMaterials");
        sr = FirebaseStorage.getInstance().getReference().child("CoverImages");

        LearnPostTitle = findViewById(R.id.LearnPostTitle);
        LearnPostBody = findViewById(R.id.LearnPostBody);
        LearnPostTag = findViewById(R.id.LearnPostTag);
        LearnPostButton = findViewById(R.id.LearnPostButton);
        UploadImage = findViewById(R.id.UploadImage);
        LearnPostCover = findViewById(R.id.LearnPostCover);

        UploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
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
            }
        });

    }

    private void uploadImage(String lpTitle, String lpBody, String lpTag) {

        String key = dr.push().getKey();
        sr.child(key+".jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                sr.child(key+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap hashMap = new HashMap();
                        hashMap.put("LearnTitle", lpTitle);
                        hashMap.put("LearnBody", lpBody);
                        hashMap.put("LearnTag", lpTag);
                        hashMap.put("ImageURL", uri.toString());

                        dr.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(LearnPost.this, "Data Successfully Uploaded ", Toast.LENGTH_SHORT).show();
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