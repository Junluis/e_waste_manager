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
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Objects;

import soup.neumorphism.NeumorphFloatingActionButton;

public class Learn extends AppCompatActivity implements LearnInterface{

    RecyclerView learnRecycler;
    ImageButton closebtn, btnGuideSearch;
    NeumorphFloatingActionButton addButton;

    ArrayList<LearnModel> learnModelArrayList;
    LearnAdapter learnAdapter;
    ProgressDialog pd;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        learnRecycler = findViewById(R.id.learnRecycler);
        closebtn = findViewById(R.id.closebtn);
        addButton = findViewById(R.id.addButton);
        btnGuideSearch = findViewById(R.id.btnGuideSearch);

        btnGuideSearch.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), GuideSearch.class)));
        closebtn.setOnClickListener(v -> onBackPressed());
        addButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LearnPost.class)));

        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd .setMessage("Fetching Data...");
        pd.show();

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        learnModelArrayList = new ArrayList<LearnModel>();
        learnAdapter = new LearnAdapter(Learn.this, learnModelArrayList, this);

        learnRecycler = findViewById(R.id.learnRecycler);
        learnRecycler.setHasFixedSize(true);
        learnRecycler.setLayoutManager(new LinearLayoutManager(this));
        learnRecycler.setAdapter(learnAdapter);
        addButton.setVisibility(View.GONE);

        UserCheck();
        EventChangeListener();
    }

    private void UserCheck() {
        if (fAuth.getCurrentUser()!=null){
            fStore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult()!=null){
                            String id = task.getResult().getString("Partner");
                            if(Objects.equals(id, "1") || Objects.equals(id, "2")){
                                addButton.setVisibility(View.VISIBLE);
                            }
                        }else{
                            addButton.setVisibility(View.GONE);
                        }
                    });
        }
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

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(Learn.this, LearnView.class);

        intent.putExtra("learnTitle", learnModelArrayList.get(position).getLearnTitle());
        intent.putExtra("learnAuthor", learnModelArrayList.get(position).getLearnAuthor());
        intent.putExtra("learnBody", learnModelArrayList.get(position).getLearnBody());
        intent.putExtra("docId", learnModelArrayList.get(position).getDocId());
        intent.putExtra("url", learnModelArrayList.get(position).getUrl());
        intent.putExtra("filepdf", learnModelArrayList.get(position).getFilepdf());
        intent.putExtra("learnSubTitle", learnModelArrayList.get(position).getLearnSubTitle());


        startActivity(intent);

    }
}