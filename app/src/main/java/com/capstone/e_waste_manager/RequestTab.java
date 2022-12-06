package com.capstone.e_waste_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class RequestTab extends AppCompatActivity {

    ImageButton reqBackIB;
    RecyclerView requestRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_tab);

        requestRecycler = (RecyclerView) findViewById(R.id.requestRecycler);
        reqBackIB = (ImageButton) findViewById(R.id.reqBackIB);

        reqBackIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestTab.this, Home.class);
                startActivity(intent);
            }
        });

    }
}