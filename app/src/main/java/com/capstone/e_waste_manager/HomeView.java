package com.capstone.e_waste_manager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeView extends AppCompatActivity {

    Button bckBtn, commentBtn;
    EditText pComment;
    TextView pTitle, pAuthor, pBody;
    RecyclerView commentRecycler;
    String docId;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    ArrayList<CommentModel> commentModelArrayList;
    CommentAdapter commentAdapter;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_view);

        String title = getIntent().getStringExtra("homeTitle");
        String author = getIntent().getStringExtra("homeAuthor");
        String body = getIntent().getStringExtra("homeBody");

        pTitle = findViewById(R.id.pTitle);
        pAuthor = findViewById(R.id.pAuthor);
        pBody = findViewById(R.id.pBody);
        pComment = findViewById(R.id.pComment);
        commentBtn = findViewById(R.id.commentBtn);
        bckBtn = findViewById(R.id.bckBtn);
        commentRecycler = findViewById(R.id.commentRecycler);

        bckBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Home.class)));

        pTitle.setText(title);
        pAuthor.setText(author);
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