package com.capstone.e_waste_manager.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.capstone.e_waste_manager.Class.TimeAgo2;
import com.capstone.e_waste_manager.Home;
import com.capstone.e_waste_manager.HomeModel;
import com.capstone.e_waste_manager.HomeView;
import com.capstone.e_waste_manager.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


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
    StorageReference storageReference;


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

        storageReference = FirebaseStorage.getInstance().getReference();

        prof_posts.setItemAnimator(null);

        Query query = fStore.collection("Post").whereEqualTo("homeAuthorUid", userID)
                .orderBy("homePostDate", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<HomeModel> options = new FirestoreRecyclerOptions.Builder<HomeModel>()
                .setQuery(query, HomeModel.class)
                .build();

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
        TextView author, title, body, authorUid, docId, timestamp, upvotecount, downvotecount;
        Button addcoment;
        ToggleButton upvote, downvote;
        ImageView prof_img, partnerBadge;
        HomeModel model;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //post details
            author = itemView.findViewById(R.id.homeAuthor);
            title = itemView.findViewById(R.id.homeTitle);
            body = itemView.findViewById(R.id.homeBody);
            docId = itemView.findViewById(R.id.docId);
            authorUid = itemView.findViewById(R.id.homeAuthorUid);
            timestamp = itemView.findViewById(R.id.timestamp);
            prof_img = itemView.findViewById(R.id.prof_img);
            addcoment = itemView.findViewById(R.id.addcoment);
            //vote
            upvote = itemView.findViewById(R.id.upvote);
            downvote = itemView.findViewById(R.id.downvote);
            upvotecount = itemView.findViewById(R.id.upvotecount);
            downvotecount = itemView.findViewById(R.id.downvotecount);
            partnerBadge = itemView.findViewById(R.id.partnerBadge);

            addcoment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), HomeView.class);
                    intent.putExtra("model", model);
                    itemView.getContext().startActivity(intent);
                }
            });

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
            //put details
            model = homeModel;
            title.setText(homeModel.homeTitle);
            body.setText(homeModel.homeBody);
            docId.setText(homeModel.docId);
            authorUid.setText(homeModel.homeAuthorUid);
            TimeAgo2 timeAgo2 = new TimeAgo2();
            if(homeModel.getHomePostDate() != null){
                String timeago = timeAgo2.covertTimeToText(homeModel.getHomePostDate().toString());
                timestamp.setText(timeago);
            }

            DocumentReference usernameReference = fStore.collection("Users").document(homeModel.homeAuthorUid);
            usernameReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                    author.setText(documentSnapShot.getString("Username"));

                    if(Objects.equals(documentSnapShot.getString("Partner"), "1")){
                        partnerBadge.setVisibility(View.VISIBLE);
                    } else{
                        partnerBadge.setVisibility(View.GONE);
                    }
                }
            });

            //vote counter
            CollectionReference vote = fStore.collection("Post").document(docId.getText().toString())
                    .collection("vote");
            AggregateQuery Upvotecount = vote.whereEqualTo("Upvote", true).count();
            AggregateQuery Downvotecount = vote.whereEqualTo("Downvote", true).count();
            Upvotecount.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                    AggregateQuerySnapshot snapshot = task.getResult();
                    if (snapshot.getCount() != 0) {
                        upvotecount.setText("" + snapshot.getCount());
                    }else {
                        upvotecount.setText("Upvote");
                    }
                }
            });
            Downvotecount.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                    AggregateQuerySnapshot snapshot = task.getResult();
                    if (snapshot.getCount() != 0){
                        downvotecount.setText(""+snapshot.getCount());
                    }else {
                        downvotecount.setText("Downvote");
                    }
                }
            });

            //vote history for logged in user
            DocumentReference documentReference = fStore.collection("Post").document(docId.getText().toString()).collection("vote")
                    .document(fAuth.getCurrentUser().getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()){
                        documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                                if(Boolean.TRUE.equals(documentSnapShot.getBoolean("Upvote"))){
                                    upvote.setChecked(true);
                                    downvote.setChecked(false);
                                }else if (Boolean.TRUE.equals(documentSnapShot.getBoolean("Downvote"))){
                                    downvote.setChecked(true);
                                    upvote.setChecked(false);
                                }else{
                                    upvote.setChecked(false);
                                    downvote.setChecked(false);
                                }
                            }
                        });
                    }else{
                        upvote.setChecked(false);
                        downvote.setChecked(false);
                    }
                }
            });

            //place vote
            upvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (upvote.isChecked()) {
                        Map<String, Object> vote = new HashMap<>();
                        vote.put("Upvote", true);

                        upvote.setEnabled(false);
                        downvote.setEnabled(false);
                        fStore.collection("Post").document(docId.getText().toString()).collection("vote")
                                .document(fAuth.getCurrentUser().getUid()).set(vote).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        adapter.notifyItemChanged(getAdapterPosition());
                                        upvote.setEnabled(true);
                                        downvote.setEnabled(true);
                                    }
                                });
                    }else if (!upvote.isChecked() && !downvote.isChecked()){
                        upvote.setEnabled(false);
                        downvote.setEnabled(false);
                        fStore.collection("Post").document(docId.getText().toString()).collection("vote")
                                .document(fAuth.getCurrentUser().getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        adapter.notifyItemChanged(getAdapterPosition());
                                        upvote.setEnabled(true);
                                        downvote.setEnabled(true);
                                    }
                                });
                    }
                }
            });
            downvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (downvote.isChecked()) {
                        Map<String, Object> vote = new HashMap<>();
                        vote.put("Downvote", true);


                        downvote.setEnabled(false);
                        upvote.setEnabled(false);
                        fStore.collection("Post").document(docId.getText().toString()).collection("vote")
                                .document(fAuth.getCurrentUser().getUid()).set(vote).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        adapter.notifyItemChanged(getAdapterPosition());
                                        downvote.setEnabled(true);
                                        upvote.setEnabled(true);
                                    }
                                });
                    }else if (!upvote.isChecked() && !downvote.isChecked()){
                        downvote.setEnabled(false);
                        upvote.setEnabled(false);
                        fStore.collection("Post").document(docId.getText().toString()).collection("vote")
                                .document(fAuth.getCurrentUser().getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        adapter.notifyItemChanged(getAdapterPosition());
                                        downvote.setEnabled(true);
                                        upvote.setEnabled(true);
                                    }
                                });
                    }
                }
            });

            //profile image per post
            StorageReference profileRef = storageReference.child("ProfileImage/"+authorUid.getText().toString()+"/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(prof_img);
                }
            });
        }
    }

    public void refresh(){
        swipeRefresh.setRefreshing(true);
        prof_posts.setAdapter(null);
        prof_posts.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }


}