package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

public class Rewards extends AppCompatActivity {

    ImageView closedr;
    TextView empoints;

    RecyclerView recycler;
    LinearLayoutManager linearLayoutManager;

    //firebase
    FirebaseFirestore fStore;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirestoreRecyclerAdapter adapter;
    FirebaseUser user;

    SwipeRefreshLayout swipeRefresh;

    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        //firebase
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        swipeRefresh = findViewById(R.id.swipeRefresh);

        closedr = findViewById(R.id.closedr);
        empoints = findViewById(R.id.empoints);

        closedr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        DocumentReference documentReference = fStore.collection("Users").document(user.getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                Double points = documentSnapShot.getDouble("EMPoints");

                if (points != null) {
                    empoints.setText(points.longValue()+"");
                }else{
                    empoints.setText("0");
                }
            }
        });

        recycler = findViewById(R.id.Recycler);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recycler.setLayoutManager(linearLayoutManager);

        recycler.setItemAnimator(null);

        query = fStore.collection("Reward");

        FirestoreRecyclerOptions<RewardsModel> options = new FirestoreRecyclerOptions.Builder<RewardsModel>()
                .setQuery(query, RewardsModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<RewardsModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull RewardsModel model) {
                holder.bind(model);
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.reward_each, group,false);
                return new ViewHolder(view);
            }
        };

        recycler.setAdapter(adapter);

        //swipe up to refresh
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RewardsModel model;
        TextView rewardtitle, rewardpoint;
        Button redeembtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rewardtitle = itemView.findViewById(R.id.rewardtitle);
            rewardpoint = itemView.findViewById(R.id.rewardpoint);
            redeembtn = itemView.findViewById(R.id.redeembtn);

            redeembtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), RewardsCatalog.class);
                    intent.putExtra("model", model);
                    itemView.getContext().startActivity(intent);
                }
            });

        }
        public void bind(RewardsModel rewardModel){
            model = rewardModel;

            rewardtitle.setText(rewardModel.title);
            rewardpoint.setText(rewardModel.points + " Points");

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

    public void refresh(){
        swipeRefresh.setRefreshing(true);
        recycler.setAdapter(null);
        recycler.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }
}