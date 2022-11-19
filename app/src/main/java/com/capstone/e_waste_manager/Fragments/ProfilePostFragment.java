package com.capstone.e_waste_manager.Fragments;

import android.app.ProgressDialog;
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
import android.widget.TextView;

import com.capstone.e_waste_manager.Class.TimeAgo2;
import com.capstone.e_waste_manager.HomeModel;
import com.capstone.e_waste_manager.HomeView;
import com.capstone.e_waste_manager.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class ProfilePostFragment extends Fragment {


    public ProfilePostFragment() {
        // Required empty public constructor
    }
    ProgressDialog pd;

    FirebaseFirestore fStore;
    RecyclerView prof_posts;
    SwipeRefreshLayout swipeRefresh;
    String userID;
    FirebaseAuth fAuth;

    FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_post, container, false);

        fStore = FirebaseFirestore.getInstance();
        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        prof_posts = (RecyclerView) view.findViewById(R.id.prof_posts);
        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        prof_posts.setLayoutManager(linearLayoutManager);

        prof_posts.setItemAnimator(null);

        Query query = fStore.collection("Post").whereEqualTo("homeAuthorUid", userID)
                .orderBy("homePostDate", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<HomeModel> options = new FirestoreRecyclerOptions.Builder<HomeModel>()
                .setQuery(query, HomeModel.class)
                .build();

        //swipe up to refresh.. not really needed firebase is already in real time
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }
        });

        adapter = new FirestoreRecyclerAdapter<HomeModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull HomeModel model) {
                holder.bind(model);
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.home_each, group,false);
                return new ViewHolder(view);
            }
        };

        prof_posts.setAdapter(adapter);

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView author, title, body, authorUid, docId, timestamp;
        HomeModel model;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.homeAuthor);
            title = itemView.findViewById(R.id.homeTitle);
            body = itemView.findViewById(R.id.homeBody);
            docId = itemView.findViewById(R.id.docId);
            authorUid = itemView.findViewById(R.id.homeAuthorUid);
            timestamp = itemView.findViewById(R.id.timestamp);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), HomeView.class);
                    intent.putExtra("model", model);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
        public void bind(HomeModel homeModel){
            model = homeModel;
            author.setText(homeModel.homeAuthor);
            title.setText(homeModel.homeTitle);
            body.setText(homeModel.homeBody);
            docId.setText(homeModel.docId);
            authorUid.setText(homeModel.homeAuthorUid);
            TimeAgo2 timeAgo2 = new TimeAgo2();
            String timeago = timeAgo2.covertTimeToText(homeModel.getHomePostDate().toString());
            timestamp.setText(timeago);
        }
    }

}