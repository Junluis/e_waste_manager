package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ForumSearch extends AppCompatActivity{

    ImageButton fsBack, fsBtn;
    EditText fsET;
    RecyclerView fsRV;

    ArrayList<LearnModel> learnModelArrayList;
    LearnAdapter learnAdapter;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_search);

        fsBack = findViewById(R.id.fsBack);
        fsBtn = findViewById(R.id.fsBtn);
        fsET = findViewById(R.id.fsET);
        fsRV = findViewById(R.id.fsRV);

        fsBack.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Home.class)));

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

//        //forum
//        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
//        fsRV.setLayoutManager(linearLayoutManager);
//
//        fsRV.setItemAnimator(null);
//
//        Query query = fStore.collection("Post")
//                .orderBy("homePostDate", Query.Direction.DESCENDING)
//                .limit(50);
//
//        FirestoreRecyclerOptions<HomeModel> options = new FirestoreRecyclerOptions.Builder<HomeModel>()
//                .setQuery(query, HomeModel.class)
//                .build();
//
//        adapter = new FirestoreRecyclerAdapter<HomeModel, Home.ViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull Home.ViewHolder holder, int position, @NonNull HomeModel model) {
//                holder.bind(model);
//            }
//
//            @NonNull
//            @Override
//            public Home.ViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
//                View view = LayoutInflater.from(group.getContext())
//                        .inflate(R.layout.home_each, group,false);
//                return new Home.ViewHolder(view);
//            }
//        };
//
//        fsRV.setAdapter(adapter);

    }
}