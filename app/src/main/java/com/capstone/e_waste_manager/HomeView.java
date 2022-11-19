package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.e_waste_manager.Class.TimeAgo2;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class HomeView extends AppCompatActivity {

    ImageButton bckBtn;
    Button commentBtn;
    ImageView prof_img;
    EditText pComment;
    TextView pTitle, pAuthor, pBody, pAuthorUid, pdocId, ptimestamp;
    RecyclerView commentRecycler;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String userID;
    FirebaseUser user;
    SwipeRefreshLayout swipeRefresh;
    LinearLayoutManager linearLayoutManager;
    FirestoreRecyclerAdapter adapter;
    StorageReference storageReference;

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
                swipeRefresh.setRefreshing(false);
            }
        });

        commentRecycler = findViewById(R.id.commentRecycler);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        commentRecycler.setLayoutManager(linearLayoutManager);

        commentRecycler.setItemAnimator(null);

        Query query = fStore.collection("Post")
                .document(pdocId.getText().toString()).collection("comment")
                .orderBy("commentPostDate", Query.Direction.DESCENDING);

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
        TextView author, body, authorUid, docId, timestamp;
        ImageView prof_img;
        CommentModel model;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.commentAuthor);
            body = itemView.findViewById(R.id.commentBody);
            docId = itemView.findViewById(R.id.docId);
            authorUid = itemView.findViewById(R.id.commentAuthorUid);
            timestamp = itemView.findViewById(R.id.timestamp);
            prof_img = itemView.findViewById(R.id.prof_img);
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

            StorageReference profileRef = storageReference.child("ProfileImage/"+commentModel.commentUid+"/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(prof_img);
                }
            });
        }
    }

}