package com.capstone.e_waste_manager;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.os.Bundle;

public class UserProfilePage extends AppCompatActivity {
    ImageButton closeup;
    Button editProfilebtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);


        closeup = findViewById(R.id.closeup);
        editProfilebtn = findViewById(R.id.editProfilebtn);

        closeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}