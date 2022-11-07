package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Post extends AppCompatActivity {

    ImageButton postBtnHome, postBtnPost, postBtnLearn, postBtnMenu, postBtnUser;
    Button postButton;
    EditText postTitle, postBody;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        postBtnHome = findViewById(R.id.postBtnHome);
        postBtnPost = findViewById(R.id.postBtnPost);
        postBtnLearn = findViewById(R.id.postBtnLearn);
        postBtnMenu = findViewById(R.id.postBtnMenu);
        postBtnUser = findViewById(R.id.postBtnUser);
        postButton = findViewById(R.id.postButton);
        postTitle = findViewById(R.id.postTitle);
        postBody = findViewById(R.id.postBody);

        postBtnHome.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Home.class)));
        postBtnPost.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Post.class)));
        postBtnLearn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Learn.class)));
        String name = user.getDisplayName();



        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fAuth.getCurrentUser()!=null){

                    uploadData();
                    Toast.makeText(Post.this, "Logged in "+name, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Post.this, "Not Logged in", Toast.LENGTH_SHORT).show();
                }
                finish();
            }

            private void uploadData() {

            fStore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null){
                            String username = task.getResult().getString("Username");
                            Map<String, Object> doc = new HashMap<>();
                            doc.put("homeTitle", postTitle.getText().toString().trim());
                            doc.put("homeBody", postBody.getText().toString().trim());
                            doc.put("homeAuthor", username);

                            fStore.collection("Post").add(doc).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    postBody.setText("");
                                    postTitle.setText("");
                                    Toast.makeText(Post.this, "Post Successful", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{

                        }
                    });

            }
        });
    }



}