package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeView extends AppCompatActivity {

    ImageButton bckBtn;
    Button commentBtn;
    EditText pComment;
    TextView pTitle, pAuthor, pBody, pAuthorUid, pdocId, ptimestamp;
    RecyclerView commentRecycler;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    ArrayList<CommentModel> commentModelArrayList;
    CommentAdapter commentAdapter;
    ProgressDialog pd;
    StorageReference storageReference;
    String userID;
    FirebaseUser user;
    SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_view);

        String title = getIntent().getStringExtra("homeTitle");
        String author = getIntent().getStringExtra("homeAuthor");
        String body = getIntent().getStringExtra("homeBody");
        String AuthorUid = getIntent().getStringExtra("homeAuthorUid");
        String docId = getIntent().getStringExtra("docId");
        String homePostDate = getIntent().getStringExtra("homePostDate");


        // comment try
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();


        pTitle = findViewById(R.id.pTitle);
        pAuthor = findViewById(R.id.pAuthor);
        pAuthorUid = findViewById(R.id.pAuthorUid);
        pdocId = findViewById(R.id.pdocId);
        pBody = findViewById(R.id.pBody);
        ptimestamp = findViewById(R.id.ptimestamp);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        pComment = findViewById(R.id.pComment);
        commentBtn = findViewById(R.id.commentBtn);
        bckBtn = findViewById(R.id.bckBtn);
        commentRecycler = findViewById(R.id.commentRecycler);

        bckBtn.setOnClickListener(v -> finish());

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference documentReference = fStore.collection("Post").document(docId);
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

        pTitle.setText(title);
        pAuthor.setText(author);
        pAuthorUid.setText(AuthorUid);
        ptimestamp.setText(homePostDate);
        pdocId.setText(docId);
        pBody.setText(body);

        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Fetching Data...");
        pd.show();

        fStore = FirebaseFirestore.getInstance();
        commentModelArrayList = new ArrayList<CommentModel>();
        commentAdapter = new CommentAdapter(HomeView.this, commentModelArrayList);

        commentRecycler.setHasFixedSize(true);
        commentRecycler.setLayoutManager(new LinearLayoutManager(this));
        commentRecycler.setAdapter(commentAdapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        commentRecycler.setLayoutManager(mLayoutManager);

        EventChangeListener();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                commentModelArrayList.clear();
                EventChangeListener();
                swipeRefresh.setRefreshing(false);
            }
        });

    }

    private void EventChangeListener() {
        Toast.makeText(HomeView.this, pdocId.getText().toString(), Toast.LENGTH_SHORT).show();
        fStore.collection("Post").document(pdocId.getText().toString()).collection("comment").orderBy("commentPostDate").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    if(pd.isShowing())
                        pd.dismiss();
                    Log.e("Firestore error", error.getMessage());
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()){
                    commentModelArrayList.add(dc.getDocument().toObject(CommentModel.class));
                }
                commentAdapter.notifyDataSetChanged();
                if(pd.isShowing())
                    pd.dismiss();
            }
        });
    }



}