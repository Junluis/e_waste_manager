package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.e_waste_manager.Class.TimeAgo2;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class RequestTab extends AppCompatActivity {

    ImageButton reqBackIB;
    RecyclerView requestRecycler;
    LinearLayoutManager linearLayoutManager;

    //firebase
    FirebaseFirestore fStore;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirestoreRecyclerAdapter adapter;
    FirebaseUser user;

    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_tab);

        requestRecycler = (RecyclerView) findViewById(R.id.requestRecycler);
        reqBackIB = (ImageButton) findViewById(R.id.reqBackIB);

        //firebase
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        reqBackIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        requestRecycler = findViewById(R.id.requestRecycler);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        requestRecycler.setLayoutManager(linearLayoutManager);

        requestRecycler.setItemAnimator(null);

        query = fStore.collection("Request")
                .whereEqualTo("status", "pending");

        FirestoreRecyclerOptions<RequestModel> options = new FirestoreRecyclerOptions.Builder<RequestModel>()
                .setQuery(query, RequestModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<RequestModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull RequestModel model) {
                holder.bind(model);
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.requests_each, group,false);
                return new ViewHolder(view);
            }
        };

        requestRecycler.setAdapter(adapter);

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView adReqName, adReqDetail, adReqDate;
        RequestModel model;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            adReqName = itemView.findViewById(R.id.adReqName);
            adReqDetail = itemView.findViewById(R.id.adReqDetail);
            adReqDate = itemView.findViewById(R.id.adReqDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), RequestView.class);
                    intent.putExtra("model", model);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
        public void bind(RequestModel requestModel){
            model = requestModel;
            adReqName.setText(requestModel.reqName);
            TimeAgo2 timeAgo2 = new TimeAgo2();
            if(requestModel.reqDate != null){
                String timeago = timeAgo2.covertTimeToText(requestModel.getReqDate().toString());
                adReqDate.setText(timeago);
            }

            DocumentReference usernameReference = fStore.collection("Users").document(requestModel.reqUserId);
            usernameReference.addSnapshotListener(RequestTab.this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                    adReqDetail.setText(documentSnapShot.getString("Email"));
                }
            });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}