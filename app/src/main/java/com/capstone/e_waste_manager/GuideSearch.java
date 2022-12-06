package com.capstone.e_waste_manager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GuideSearch extends AppCompatActivity implements LearnInterface{
    
    ImageButton gsBack, gsBtn;
    EditText gsET;
    RecyclerView gsRV;

    ArrayList<LearnModel> learnModelArrayList;
    LearnAdapter learnAdapter;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_search);

        gsBack = findViewById(R.id.gsBack);
        gsBtn = findViewById(R.id.gsBtn);
        gsET = findViewById(R.id.gsET);
        gsRV = findViewById(R.id.gsRV);

        gsBack.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Learn.class)));

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        learnModelArrayList = new ArrayList<LearnModel>();
        learnAdapter = new LearnAdapter(GuideSearch.this, learnModelArrayList, this);

        gsRV.setHasFixedSize(true);
        gsRV.setLayoutManager(new LinearLayoutManager(this));
        gsRV.setAdapter(learnAdapter);

        EventChangeListener();

    }

    private void EventChangeListener() {
        fStore.collection("LearningMaterial").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.e("Firestore error", error.getMessage());
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()){
                    learnModelArrayList.add(dc.getDocument().toObject(LearnModel.class));
                }
                learnAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(GuideSearch.this, LearnView.class);

        intent.putExtra("learnTitle", learnModelArrayList.get(position).getLearnTitle());
        intent.putExtra("learnAuthor", learnModelArrayList.get(position).getLearnAuthor());
        intent.putExtra("learnBody", learnModelArrayList.get(position).getLearnBody());
        intent.putExtra("cover", learnModelArrayList.get(position).getLearnImage());

        startActivity(intent);
    }
}