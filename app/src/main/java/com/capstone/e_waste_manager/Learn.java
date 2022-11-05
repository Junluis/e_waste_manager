package com.capstone.e_waste_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class Learn extends AppCompatActivity {

    RecyclerView learnRecycler;
    ImageButton learnBtnHome, learnBtnPost, learnBtnLearn, learnBtnMenu, learnBtnUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        learnRecycler = findViewById(R.id.learnRecycler);
        learnBtnHome = findViewById(R.id.learnBtnHome);
        learnBtnPost = findViewById(R.id.learnBtnPost);
        learnBtnLearn = findViewById(R.id.learnBtnLearn);
        learnBtnMenu = findViewById(R.id.learnBtnMenu);
        learnBtnUser = findViewById(R.id.learnBtnUser);

        learnBtnHome.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Home.class)));

        learnBtnPost.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Post.class)));

        learnBtnLearn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Learn.class)));
    }

}