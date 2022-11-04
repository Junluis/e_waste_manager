package com.capstone.e_waste_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class Home extends AppCompatActivity {

    RecyclerView homeRecycler;
    ImageButton homeBtnHome, homeBtnPost, homeBtnLearn, homeBtnMenu, homeBtnUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeRecycler = findViewById(R.id.homeRecycler);
        homeBtnHome = findViewById(R.id.homeBtnHome);
        homeBtnPost = findViewById(R.id.homeBtnPost);
        homeBtnLearn = findViewById(R.id.homeBtnLearn);
        homeBtnMenu = findViewById(R.id.homeBtnMenu);
        homeBtnUser = findViewById(R.id.homeBtnUser);

        homeBtnHome.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Home.class)));

        homeBtnPost.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Post.class)));

        homeBtnLearn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Learn.class)));
    }
}