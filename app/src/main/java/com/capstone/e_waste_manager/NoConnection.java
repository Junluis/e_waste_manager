package com.capstone.e_waste_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class NoConnection extends AppCompatActivity {

    TextView textView1, textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);

        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);

        textView1.animate().alpha(1).setDuration(1000).setStartDelay(1000);
        textView2.animate().alpha(1).setDuration(1000).setStartDelay(1500);
    }
}