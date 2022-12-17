package com.capstone.e_waste_manager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class LearnView extends AppCompatActivity {

    ImageButton backButton;
    ImageView cover;
    TextView lTitle, lAuthor, lBody;

    //firebase
    FirebaseFirestore fStore;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learnview);

        //firebase
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        String title = getIntent().getStringExtra("learnTitle");
        String author = getIntent().getStringExtra("learnAuthor");
        String body = getIntent().getStringExtra("learnBody");
        String docId = getIntent().getStringExtra("docId");
        String url = getIntent().getStringExtra("url");
        String filepdf = getIntent().getStringExtra("filepdf");
        String learnSubTitle = getIntent().getStringExtra("learnSubTitle");

        lTitle = findViewById(R.id.lTitle);
        lAuthor = findViewById(R.id.lAuthor);
        lBody = findViewById(R.id.lBody);
        backButton = findViewById(R.id.backButton);
        cover = findViewById(R.id.cover);

        backButton.setOnClickListener(v -> onBackPressed());

        lTitle.setText(title);
        lAuthor.setText(author);
        lBody.setText(body);

        StorageReference coverRef = storageReference.child("LearningMaterial/"+docId+"/coverimg.jpg");
        coverRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(cover);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

    }
}
