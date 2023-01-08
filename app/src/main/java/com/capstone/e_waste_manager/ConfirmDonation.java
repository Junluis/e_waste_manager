package com.capstone.e_waste_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ConfirmDonation extends AppCompatActivity {

    ImageButton closedr;
    CardView pickupcard, dropoffcard;
    RadioButton pickup, dropoff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_donation);

        closedr = findViewById(R.id.closedr);
        pickupcard = findViewById(R.id.pickupcard);
        dropoffcard = findViewById(R.id.dropoffcard);
        pickup = findViewById(R.id.pickup);
        dropoff = findViewById(R.id.dropoff);

        closedr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        pickup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    dropoff.setChecked(false);
                }
            }
        });
        dropoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    pickup.setChecked(false);
                }
            }
        });

        pickupcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickup.setChecked(true);
                dropoff.setChecked(false);
            }
        });

        dropoffcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickup.setChecked(false);
                dropoff.setChecked(true);
            }
        });


    }
}