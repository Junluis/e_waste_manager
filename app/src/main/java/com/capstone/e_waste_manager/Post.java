package com.capstone.e_waste_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

public class Post extends AppCompatActivity {

    ImageButton postBtnHome, postBtnPost, postBtnLearn, postBtnMenu, postBtnUser;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postBtnHome = findViewById(R.id.postBtnHome);
        postBtnPost = findViewById(R.id.postBtnPost);
        postBtnLearn = findViewById(R.id.postBtnLearn);
        postBtnMenu = findViewById(R.id.postBtnMenu);
        postBtnUser = findViewById(R.id.postBtnUser);
        logout = findViewById(R.id.postLogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        postBtnHome.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Home.class)));
        postBtnPost.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Post.class)));
        postBtnLearn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Learn.class)));
    }
}