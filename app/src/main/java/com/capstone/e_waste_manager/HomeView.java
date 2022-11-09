package com.capstone.e_waste_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class HomeView extends AppCompatActivity {

    Button bckBtn, commentBtn;
    EditText pComment;
    TextView pTitle, pAuthor, pBody;
    RecyclerView commentRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_view);

        String title = getIntent().getStringExtra("homeTitle");
        String author = getIntent().getStringExtra("homeAuthor");
        String body = getIntent().getStringExtra("homeBody");

        pTitle = findViewById(R.id.pTitle);
        pAuthor = findViewById(R.id.pAuthor);
        pBody = findViewById(R.id.pBody);
        pComment = findViewById(R.id.pComment);
        commentBtn = findViewById(R.id.commentBtn);
        bckBtn = findViewById(R.id.bckBtn);
        commentRecycler = findViewById(R.id.commentRecycler);

        bckBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Home.class)));

        pTitle.setText(title);
        pAuthor.setText(author);
        pBody.setText(body);

    }
}