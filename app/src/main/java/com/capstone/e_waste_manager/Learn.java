package com.capstone.e_waste_manager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Learn extends AppCompatActivity {

    RecyclerView learnRecycler;
    ImageButton learnBtnHome, learnBtnPost, learnBtnLearn, learnBtnMenu, learnBtnUser, addButton;
    View learnImage;

    ArrayList<LearnModel> learnModelArrayList;
    LearnAdapter learnAdapter;
    ProgressDialog pd;

    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        learnRecycler = findViewById(R.id.learnRecycler);
        learnBtnHome = findViewById(R.id.learnBtnHome);
        learnBtnPost = findViewById(R.id.learnBtnPost);
        learnBtnLearn = findViewById(R.id.learnBtnLearn);
        learnBtnMenu = findViewById(R.id.learnBtnMenu);
        learnBtnUser = findViewById(R.id.learnBtnUser);
        learnImage = findViewById(R.id.learnImage);
        addButton = findViewById(R.id.addButton);

        learnBtnHome.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Home.class)));
        learnBtnPost.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Post.class)));
        learnBtnLearn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Learn.class)));
        addButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LearnPost.class)));

        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd .setMessage("Fetching Data...");
        pd.show();

        fStore = FirebaseFirestore.getInstance();
        learnModelArrayList = new ArrayList<LearnModel>();
        learnAdapter = new LearnAdapter(Learn.this, learnModelArrayList);

        learnRecycler = findViewById(R.id.learnRecycler);
        learnRecycler.setHasFixedSize(true);
        learnRecycler.setLayoutManager(new LinearLayoutManager(this));
        learnRecycler.setAdapter(learnAdapter);

        EventChangeListener();
    }

    private void EventChangeListener() {
        fStore.collection("LearningMaterial").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    if(pd.isShowing())
                        pd.dismiss();
                    Log.e("Firestore error", error.getMessage());
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()){
                    learnModelArrayList.add(dc.getDocument().toObject(LearnModel.class));
                }
                learnAdapter.notifyDataSetChanged();
                if(pd.isShowing())
                    pd.dismiss();
            }
        });
    }

}