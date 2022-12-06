package com.capstone.e_waste_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class RequestView extends AppCompatActivity {

    TextView reqNameTV, reqAddressTV, reqNumberTV, reqDescTV;
    ImageView reqDTI, reqSEC;
    Button reqApprove, reqReject;
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_view);

        reqNameTV.findViewById(R.id.reqNameTV);
        reqAddressTV.findViewById(R.id.reqAddressTV);
        reqNumberTV.findViewById(R.id.reqNumberTV);
        reqDescTV.findViewById(R.id.reqDescTV);
        reqDTI.findViewById(R.id.reqDTIIV);
        reqSEC.findViewById(R.id.reqSECIV);
        reqApprove.findViewById(R.id.reqApprove);
        reqReject.findViewById(R.id.reqReject);
        backButton.findViewById(R.id.reqBackBtn);

        backButton.setOnClickListener(v -> {startActivity(new Intent(getApplicationContext(), RequestTab.class));});
    }
}