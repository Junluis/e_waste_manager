package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    Button bckBtn, commentBtn;
    EditText pComment;
    TextView pTitle, pAuthor, pBody, pAuthorUid;
    RecyclerView commentRecycler;
    String docId;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    ArrayList<CommentModel> commentModelArrayList;
    CommentAdapter commentAdapter;
    ProgressDialog pd;
    StorageReference storageReference;
    String userID;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_view);

        String title = getIntent().getStringExtra("homeTitle");
        String author = getIntent().getStringExtra("homeAuthor");
        String body = getIntent().getStringExtra("homeBody");
        String AuthorUid = getIntent().getStringExtra("homeAuthorUid");
        String docId = getIntent().getStringExtra("docId");

        Toast.makeText(HomeView.this, docId, Toast.LENGTH_SHORT).show();

        // comment try
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();


        pTitle = findViewById(R.id.pTitle);
        pAuthor = findViewById(R.id.pAuthor);
        pAuthorUid = findViewById(R.id.pAuthorUid);
        pBody = findViewById(R.id.pBody);
        pComment = findViewById(R.id.pComment);
        commentBtn = findViewById(R.id.commentBtn);
        bckBtn = findViewById(R.id.bckBtn);
        commentRecycler = findViewById(R.id.commentRecycler);

        bckBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Home.class)));

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

                        documentReference.collection("comment").add(comment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                Toast.makeText(HomeView.this, "Comment Success", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(HomeView.this, "Comment Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }
        });

        pTitle.setText(title);
        pAuthor.setText(author);
        pAuthorUid.setText(AuthorUid);
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

        EventChangeListener();

    }

    private void EventChangeListener() {
        fStore.collection("Post").document().collection("Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
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