package com.capstone.e_waste_manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class LearnView extends AppCompatActivity {

    Button backButton;
    ImageView cover;
    TextView lTitle, lAuthor, lBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_view);

        String title = getIntent().getStringExtra("learnTitle");
        String author = getIntent().getStringExtra("learnAuthor");
        String body = getIntent().getStringExtra("learnBody");
        String image = getIntent().getStringExtra("learnImage");

        lTitle = findViewById(R.id.lTitle);
        lAuthor = findViewById(R.id.lAuthor);
        lBody = findViewById(R.id.lBody);
        backButton = findViewById(R.id.backButton);
        cover = findViewById(R.id.cover);

        backButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Learn.class)));

        lTitle.setText(title);
        lAuthor.setText(author);
        lBody.setText(body);
        Picasso.get().load(image).into(cover);

    }
}
