package com.capstone.e_waste_manager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class RequestTab extends AppCompatActivity implements RequestInterface{

    ImageButton reqBackIB;
    RecyclerView requestRecycler;

    ArrayList<RequestModel> requestModelArrayList;
    RequestAdapter requestAdapter;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

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

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        requestModelArrayList = new ArrayList<RequestModel>();
        requestAdapter = new RequestAdapter(RequestTab.this, requestModelArrayList, this);

        requestRecycler.setHasFixedSize(true);
        requestRecycler.setLayoutManager(new LinearLayoutManager(this));
        requestRecycler.setAdapter(requestAdapter);

        EventChangeListener();

    }

    private void EventChangeListener() {
        fStore.collection("Request").whereEqualTo("accepted", false).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.e("Firestore error", error.getMessage());
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()){
                    requestModelArrayList.add(dc.getDocument().toObject(RequestModel.class));
                }
                requestAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(RequestTab.this, RequestView.class);

        intent.putExtra("reqAddress", requestModelArrayList.get(position).getReqAddress());
        intent.putExtra("reqDTI", requestModelArrayList.get(position).getReqDTI());
        intent.putExtra("reqDesc", requestModelArrayList.get(position).getReqDesc());
        intent.putExtra("reqName", requestModelArrayList.get(position).getReqName());
        intent.putExtra("reqNumber", requestModelArrayList.get(position).getReqNumber());
        intent.putExtra("reqSEC", requestModelArrayList.get(position).getReqSEC());
        intent.putExtra("reqUserMail", requestModelArrayList.get(position).getReqUserMail());
        intent.putExtra("reqUserId", requestModelArrayList.get(position).getReqUserId());

        startActivity(intent);

    }
}