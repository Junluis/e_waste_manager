package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.capstone.e_waste_manager.Class.TimeAgo2;
import com.capstone.e_waste_manager.model.ReplyModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeView extends AppCompatActivity {

    ImageButton bckBtn;
    Button commentBtn;
    ImageView prof_img;
    EditText pComment;
    TextView pTitle, pAuthor, pBody, pAuthorUid, pdocId, ptimestamp, upvotecount, downvotecount;
    RecyclerView commentRecycler;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String userID;
    FirebaseUser user;
    SwipeRefreshLayout swipeRefresh;
    LinearLayoutManager linearLayoutManager, linearLayoutManager2;
    FirestoreRecyclerAdapter adapter;
    StorageReference storageReference;
    ToggleButton upvote, downvote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_view);

        // comment try
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        pTitle = findViewById(R.id.pTitle);
        pAuthor = findViewById(R.id.pAuthor);
        pAuthorUid = findViewById(R.id.pAuthorUid);
        pdocId = findViewById(R.id.pdocId);
        pBody = findViewById(R.id.pBody);
        ptimestamp = findViewById(R.id.ptimestamp);
        prof_img = findViewById(R.id.prof_img);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        pComment = findViewById(R.id.pComment);
        commentBtn = findViewById(R.id.commentBtn);
        bckBtn = findViewById(R.id.bckBtn);
        commentRecycler = findViewById(R.id.commentRecycler);


        upvotecount = findViewById(R.id.upvotecount);
        downvotecount = findViewById(R.id.downvotecount);
        upvote = findViewById(R.id.upvote);
        downvote = findViewById(R.id.downvote);


        HomeModel model = (HomeModel) getIntent().getSerializableExtra("model");


        bckBtn.setOnClickListener(v -> finish());

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.stopListening();
                DocumentReference documentReference = fStore.collection("Post").document(model.docId);
                fStore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String username = task.getResult().getString("Username");
                        Map<String, Object> comment = new HashMap<>();
                        comment.put("commentUid", userID);
                        comment.put("commentAuthor", username);
                        comment.put("commentBody", pComment.getText().toString());
                        comment.put("commentPostDate", FieldValue.serverTimestamp());

                        documentReference.collection("comment").add(comment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                Toast.makeText(HomeView.this, "Comment Success", Toast.LENGTH_SHORT).show();
                                pComment.setText("");
                                adapter.startListening();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(HomeView.this, "Comment Failed", Toast.LENGTH_SHORT).show();
                                pComment.setText("");
                            }
                        });
                    }
                });

            }
        });

        //vote system
        DocumentReference documentReference = fStore.collection("Post").document(model.docId).collection("vote")
                .document(fAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
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


        upvote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    Map<String, Object> vote = new HashMap<>();
                    vote.put("Upvote", true);

                    fStore.collection("Post").document(model.docId).collection("vote")
                            .document(fAuth.getCurrentUser().getUid()).set(vote).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    votecounter();
                                    adapter.notifyDataSetChanged();
                                }
                            });
                }else if (!upvote.isChecked() && !downvote.isChecked()){
                    fStore.collection("Post").document(model.docId).collection("vote")
                            .document(fAuth.getCurrentUser().getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    votecounter();
                                    adapter.notifyDataSetChanged();
                                }
                            });
                }
            }
        });

        downvote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    Map<String, Object> vote = new HashMap<>();
                    vote.put("Downvote", true);

                    fStore.collection("Post").document(model.docId).collection("vote")
                            .document(fAuth.getCurrentUser().getUid()).set(vote).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    votecounter();
                                    adapter.notifyDataSetChanged();
                                }
                            });
                }else if (!upvote.isChecked() && !downvote.isChecked()){
                    fStore.collection("Post").document(model.docId).collection("vote")
                            .document(fAuth.getCurrentUser().getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    votecounter();
                                    adapter.notifyDataSetChanged();
                                }
                            });
                }
            }
        });
        //vote system


        pTitle.setText(model.getHomeTitle());
        pAuthor.setText(model.getHomeAuthor());
        pAuthorUid.setText(model.homeAuthorUid);
        TimeAgo2 timeAgo2 = new TimeAgo2();
        String timeago = timeAgo2.covertTimeToText(model.getHomePostDate().toString());
        ptimestamp.setText(timeago);
        pdocId.setText(model.docId);
        pBody.setText(model.getHomeBody());

        StorageReference profileRef = storageReference.child("ProfileImage/"+model.homeAuthorUid+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(prof_img);
            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                votecounter();
                swipeRefresh.setRefreshing(false);
            }
        });

        votecounter();

        commentRecycler = findViewById(R.id.commentRecycler);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        commentRecycler.setLayoutManager(linearLayoutManager);

        commentRecycler.setItemAnimator(null);

        Query query = fStore.collection("Post")
                .document(pdocId.getText().toString()).collection("comment")
                .orderBy("commentPostDate", Query.Direction.DESCENDING)
                .limit(50);

        FirestoreRecyclerOptions<CommentModel> options = new FirestoreRecyclerOptions.Builder<CommentModel>()
                .setQuery(query, CommentModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<CommentModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull CommentModel model) {
                holder.bind(model);
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.comment_each, group,false);
                return new ViewHolder(view);
            }
        };

        commentRecycler.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView author, body, authorUid, docId, timestamp, pReply, upvotecount, downvotecount;
        TextInputLayout tilpReply;
        ImageView prof_img;
        Chip replyChip;
        ToggleButton upvote, downvote;
        ToggleButton replypop;
        Button replyBtn;
        CommentModel model;
        

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.commentAuthor);
            body = itemView.findViewById(R.id.commentBody);
            docId = itemView.findViewById(R.id.docId);
            authorUid = itemView.findViewById(R.id.commentAuthorUid);
            timestamp = itemView.findViewById(R.id.timestamp);
            prof_img = itemView.findViewById(R.id.prof_img);
            replypop = itemView.findViewById(R.id.replypop);
            tilpReply = itemView.findViewById(R.id.tilpReply);
            replyBtn = itemView.findViewById(R.id.replyBtn);
            replyChip = itemView.findViewById(R.id.replyChip);
            pReply = itemView.findViewById(R.id.pReply);

            upvote = itemView.findViewById(R.id.upvote);
            downvote = itemView.findViewById(R.id.downvote);
            upvotecount = itemView.findViewById(R.id.upvotecount);
            downvotecount = itemView.findViewById(R.id.downvotecount);

//            replyRecycler = itemView.findViewById(R.id.replyRecycler);

            replyChip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tilpReply.setVisibility(View.GONE);
                    replyBtn.setVisibility(View.GONE);
                    replyChip.setVisibility(View.GONE);
                    replypop.setChecked(false);
                }
            });

            replypop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        tilpReply.setVisibility(View.VISIBLE);
                        replyBtn.setVisibility(View.VISIBLE);
                        replyChip.setVisibility(View.VISIBLE);
                        replypop.setVisibility(View.GONE);
                    }else{
                        tilpReply.setVisibility(View.GONE);
                        replyBtn.setVisibility(View.GONE);
                        replyChip.setVisibility(View.GONE);
                        replypop.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        public void bind(CommentModel commentModel){
            model = commentModel;
            author.setText(commentModel.commentAuthor);
            body.setText(commentModel.commentBody);
            docId.setText(commentModel.docId);
            authorUid.setText(commentModel.commentUid);
            TimeAgo2 timeAgo2 = new TimeAgo2();
            String timeago = timeAgo2.covertTimeToText(commentModel.getCommentPostDate().toString());
            timestamp.setText(timeago);
            replyChip.setText("@"+commentModel.commentAuthor);



            CollectionReference vote = fStore.collection("Post").document(pdocId.getText().toString())
                    .collection("comment").document(docId.getText().toString()).collection("vote");
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

            //vote system
            DocumentReference documentReference = fStore.collection("Post").document(pdocId.getText().toString())
                    .collection("comment").document(docId.getText().toString()).collection("vote")
                    .document(fAuth.getCurrentUser().getUid());
            documentReference.addSnapshotListener(HomeView.this, new EventListener<DocumentSnapshot>() {
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

            upvote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        Map<String, Object> vote = new HashMap<>();
                        vote.put("Upvote", true);

                        fStore.collection("Post").document(pdocId.getText().toString())
                                .collection("comment").document(docId.getText().toString()).collection("vote")
                                .document(fAuth.getCurrentUser().getUid()).set(vote).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                    }else if (!upvote.isChecked() && !downvote.isChecked()){
                        fStore.collection("Post").document(pdocId.getText().toString())
                                .collection("comment").document(docId.getText().toString()).collection("vote")
                                .document(fAuth.getCurrentUser().getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                    }
                }
            });

            downvote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        Map<String, Object> vote = new HashMap<>();
                        vote.put("Downvote", true);

                        fStore.collection("Post").document(pdocId.getText().toString())
                                .collection("comment").document(docId.getText().toString()).collection("vote")
                                .document(fAuth.getCurrentUser().getUid()).set(vote).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                    }else if (!upvote.isChecked() && !downvote.isChecked()){
                        fStore.collection("Post").document(pdocId.getText().toString())
                                .collection("comment").document(docId.getText().toString()).collection("vote")
                                .document(fAuth.getCurrentUser().getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                    }
                }
            });
            //vote system

            replyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.stopListening();
                    DocumentReference documentReference = fStore.collection("Post").document(pdocId.getText().toString())
                            .collection("comment").document(commentModel.docId);
                    fStore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String username = task.getResult().getString("Username");
                            Map<String, Object> comment = new HashMap<>();
                            comment.put("replyAuthorUid", userID);
                            comment.put("replyAuthor", username);
                            comment.put("replyBody", pReply.getText().toString());
                            comment.put("replyChip", commentModel.commentAuthor);
                            comment.put("replyPostDate", FieldValue.serverTimestamp());

                            documentReference.collection("reply").add(comment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    Toast.makeText(HomeView.this, "Comment Success", Toast.LENGTH_SHORT).show();
                                    pReply.setText("");
                                    adapter.startListening();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(HomeView.this, "Comment Failed", Toast.LENGTH_SHORT).show();
                                    pReply.setText("");
                                }
                            });
                        }
                    });

                }
            });

            StorageReference profileRef = storageReference.child("ProfileImage/"+commentModel.commentUid+"/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(prof_img);
                }
            });
        }
    }

//    class ViewHolder2 extends RecyclerView.ViewHolder{
//        TextView author, body, authorUid, docId, timestamp, pReply, upvotecount, downvotecount;
//        ImageView prof_img;
//        Chip replyChip;
//        ToggleButton upvote, downvote;
//        ToggleButton replypop;
//        ReplyModel model;
//
//        public ViewHolder2(@NonNull View itemView) {
//            super(itemView);
//            author = itemView.findViewById(R.id.commentAuthor);
//            body = itemView.findViewById(R.id.commentBody);
//            docId = itemView.findViewById(R.id.docId);
//            authorUid = itemView.findViewById(R.id.commentAuthorUid);
//            timestamp = itemView.findViewById(R.id.timestamp);
//            prof_img = itemView.findViewById(R.id.prof_img);
//            replypop = itemView.findViewById(R.id.replypop);
//            replyChip = itemView.findViewById(R.id.replyChip);
//            pReply = itemView.findViewById(R.id.pReply);
//
//            upvote = itemView.findViewById(R.id.upvote);
//            downvote = itemView.findViewById(R.id.downvote);
//            upvotecount = itemView.findViewById(R.id.upvotecount);
//            downvotecount = itemView.findViewById(R.id.downvotecount);
//
//        }
//
//        public void bind(ReplyModel replyModel){
//            model = replyModel;
//            author.setText(replyModel.replyAuthor);
//            body.setText(replyModel.replyBody);
//            docId.setText(replyModel.replydocId);
//            authorUid.setText(replyModel.replyAuthorUid);
//            TimeAgo2 timeAgo2 = new TimeAgo2();
//            String timeago = timeAgo2.covertTimeToText(replyModel.getReplyPostDate().toString());
//            timestamp.setText(timeago);
//            replyChip.setText("@"+replyChip);
//        }
//    }

    public void votecounter(){
        CollectionReference vote = fStore.collection("Post").document(pdocId.getText().toString())
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
    }


}