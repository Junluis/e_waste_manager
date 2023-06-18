package com.capstone.e_waste_manager.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.capstone.e_waste_manager.R;
import com.capstone.e_waste_manager.RewardsCatalog;
import com.capstone.e_waste_manager.RewardsModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AvailableRewardsFragment extends Fragment {

    public AvailableRewardsFragment() {
        // Required empty public constructor
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_available_rewards, container, false);

        //firebase
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        recycler = view.findViewById(R.id.Recycler);
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
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

        return view;
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
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
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