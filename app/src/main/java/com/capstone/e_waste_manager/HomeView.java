package com.capstone.e_waste_manager;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.capstone.e_waste_manager.Class.TimeAgo2;
import com.capstone.e_waste_manager.Fragments.RepliesToComment;
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
import java.util.Objects;

import io.github.ponnamkarthik.richlinkpreview.RichLinkViewSkype;
import io.github.ponnamkarthik.richlinkpreview.RichLinkViewTelegram;
import io.github.ponnamkarthik.richlinkpreview.RichLinkViewTwitter;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;
import soup.neumorphism.NeumorphFloatingActionButton;

public class HomeView extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    ImageButton bckBtn, more;
    Button commentBtn;
    ImageView prof_img, partnerBadge, postImg;
    EditText pComment;
    TextView pTitle, pAuthor, pBody, pAuthorUid, pdocId, ptimestamp, upvotecount, downvotecount, urltext;
    RecyclerView commentRecycler;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String userID;
    FirebaseUser user;
    SwipeRefreshLayout swipeRefresh;
    LinearLayoutManager linearLayoutManager;
    FirestoreRecyclerAdapter adapter2;
    StorageReference storageReference;
    ToggleButton upvote, downvote;
    TextInputLayout tilpComment;

    RichLinkViewTwitter urllink;

    AlertDialog dialog;
    Dialog guestDialog;

    RelativeLayout insetaddcomment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_view);

        //transparent status
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        //firebase
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        //post
        pTitle = findViewById(R.id.homeTitle);
        pAuthor = findViewById(R.id.homeAuthor);
        pAuthorUid = findViewById(R.id.homeAuthorUid);
        pdocId = findViewById(R.id.docId);
        pBody = findViewById(R.id.homeBody);
        ptimestamp = findViewById(R.id.timestamp);
        prof_img = findViewById(R.id.prof_img);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        partnerBadge = findViewById(R.id.partnerBadge);
        urllink = findViewById(R.id.urllink);
        postImg = findViewById(R.id.postImg);
        urltext = findViewById(R.id.urltext);

        //comments
        pComment = findViewById(R.id.pComment);
        tilpComment = findViewById(R.id.tilpComment);
        commentBtn = findViewById(R.id.commentBtn);
        bckBtn = findViewById(R.id.bckBtn);
        more = findViewById(R.id.more);
        commentRecycler = findViewById(R.id.commentRecycler);
        insetaddcomment = findViewById(R.id.insetaddcomment);

        //votes
        upvotecount = findViewById(R.id.upvotecount);
        downvotecount = findViewById(R.id.downvotecount);
        upvote = findViewById(R.id.upvote);
        downvote = findViewById(R.id.downvote);

        //model
        HomeModel model = (HomeModel) getIntent().getSerializableExtra("model");

        //back
        bckBtn.setOnClickListener(v -> finish());

        //Guest dialog
        guestDialog = new Dialog(this);

        //transparent inset
        ViewCompat.setOnApplyWindowInsetsListener(commentRecycler, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply the insets as a margin to the view. Here the system is setting
            // only the bottom, left, and right dimensions, but apply whichever insets are
            // appropriate to your layout. You can also update the view padding
            // if that's more appropriate.
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.leftMargin = insets.left;
            mlp.bottomMargin = insets.bottom;
            mlp.rightMargin = insets.right;
            v.setLayoutParams(mlp);

            // Return CONSUMED if you don't want want the window insets to keep being
            // passed down to descendant views.
            return WindowInsetsCompat.CONSUMED;
        });

        //post comment
        pComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(!s.toString().isEmpty()){
                    tilpComment.setError(null);
                    if (tilpComment.getChildCount() == 2)
                        tilpComment.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null && !user.isAnonymous()) {
                    if(pComment.getText().toString().length() == 0 || !TextUtils.isEmpty(tilpComment.getError())){
                        if(pComment.getText().toString().length() == 0) {
                            if (tilpComment.getChildCount() == 2)
                                tilpComment.getChildAt(1).setVisibility(View.VISIBLE);
                            tilpComment.setError("required*");
                            pComment.setText("");
                        }
                        pComment.requestFocus();
                    }else {
                        showProgressDialog();
                        DocumentReference documentReference = fStore.collection("Post").document(model.docId);
                        fStore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                String username = task.getResult().getString("Username");
                                Map<String, Object> comment = new HashMap<>();
                                comment.put("commentUid", userID);
                                comment.put("commentAuthor", username);
                                comment.put("commentBody", pComment.getText().toString());
                                comment.put("commentPostDate", FieldValue.serverTimestamp());
                                comment.put("commentPostOrigin", model.docId);

                                documentReference.collection("comment").add(comment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        Toast.makeText(HomeView.this, "Comment Success", Toast.LENGTH_SHORT).show();
                                        pComment.setText("");
                                        hideProgressDialog();
                                        commentRecycler.setAdapter(null);
                                        commentRecycler.setAdapter(adapter2);
                                        adapter2.startListening();
                                        adapter2.notifyDataSetChanged();
                                        votecounter();
                                        pComment.clearFocus();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(HomeView.this, "Comment Failed", Toast.LENGTH_SHORT).show();
                                        pComment.setText("");
                                        hideProgressDialog();
                                        commentRecycler.setAdapter(null);
                                        commentRecycler.setAdapter(adapter2);
                                        adapter2.startListening();
                                        adapter2.notifyDataSetChanged();
                                        votecounter();
                                        pComment.clearFocus();
                                    }
                                });
                            }
                        });
                    }
                }else{
                    ShowPopup();
                }
            }
        });

        //vote system
        if (user != null && !user.isAnonymous()) {
            DocumentReference documentReference = fStore.collection("Post").document(model.docId).collection("vote")
                    .document(fAuth.getCurrentUser().getUid());
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                    if (Boolean.TRUE.equals(documentSnapShot.getBoolean("Upvote"))) {
                        upvote.setChecked(true);
                        downvote.setChecked(false);
                    } else if (Boolean.TRUE.equals(documentSnapShot.getBoolean("Downvote"))) {
                        downvote.setChecked(true);
                        upvote.setChecked(false);
                    } else {
                        upvote.setChecked(false);
                        downvote.setChecked(false);
                    }
                }
            });
        }
        if (user != null && !user.isAnonymous()) {
            upvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (upvote.isChecked()) {
                        Map<String, Object> vote = new HashMap<>();
                        vote.put("Upvote", true);

                        upvote.setEnabled(false);
                        downvote.setEnabled(false);
                        fStore.collection("Post").document(model.docId).collection("vote")
                                .document(fAuth.getCurrentUser().getUid()).set(vote).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        votecounter();
                                        upvote.setEnabled(true);
                                        downvote.setEnabled(true);
                                    }
                                });
                    } else if (!upvote.isChecked() && !downvote.isChecked()) {
                        upvote.setEnabled(false);
                        downvote.setEnabled(false);
                        fStore.collection("Post").document(model.docId).collection("vote")
                                .document(fAuth.getCurrentUser().getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        votecounter();
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
                        fStore.collection("Post").document(model.docId).collection("vote")
                                .document(fAuth.getCurrentUser().getUid()).set(vote).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        votecounter();
                                        downvote.setEnabled(true);
                                        upvote.setEnabled(true);
                                    }
                                });
                    } else if (!upvote.isChecked() && !downvote.isChecked()) {
                        fStore.collection("Post").document(model.docId).collection("vote")
                                .document(fAuth.getCurrentUser().getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        votecounter();
                                        downvote.setEnabled(true);
                                        upvote.setEnabled(true);
                                    }
                                });
                    }
                }
            });
        } else{
            upvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowPopup();
                    upvote.setChecked(false);
                    downvote.setChecked(false);
                }
            });

            downvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowPopup();
                    upvote.setChecked(false);
                    downvote.setChecked(false);
                }
            });
        }
        //vote system

        //model post
        pTitle.setText(model.getHomeTitle());
        pAuthor.setText(model.getHomeAuthor());
        pAuthorUid.setText(model.getHomeAuthorUid());
        TimeAgo2 timeAgo2 = new TimeAgo2();
        String timeago = timeAgo2.covertTimeToText(model.getHomePostDate().toString());
        ptimestamp.setText(timeago);
        pdocId.setText(model.docId);
        pBody.setText(model.getHomeBody());
        urltext.setText(model.url);


        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null && !user.isAnonymous()) {
                    if (pAuthorUid.getText().toString().equals(user.getUid())){
                        postpopup(more, true, pdocId.getText().toString());
                    } else{
                        postpopup(more, false, pdocId.getText().toString());
                    }
                } else{
                    ShowPopup();
                }
            }
        });

        if (pBody.getText().length() == 0){
            pBody.setVisibility(View.GONE);
        }else{
            pBody.setVisibility(View.VISIBLE);
        }

        if(urltext.getText() != null && Patterns.WEB_URL.matcher(urltext.getText().toString()).matches()){
            urltext.setVisibility(View.VISIBLE);
            urllink.setLink(urltext.getText().toString(), new ViewListener() {
                @Override
                public void onSuccess(boolean status) {
                    urllink.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Exception e) {
                }
            });
        }else{
            urltext.setVisibility(View.GONE);
            urllink.setVisibility(View.GONE);
        }

        if (model.hasImage != null && model.hasImage){
            //profile image per post
            StorageReference profileRef = storageReference.child("ForumPost/"+model.docId+"/postimg.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(postImg);
                    postImg.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    postImg.setVisibility(View.VISIBLE);
                }
            });
        }

        DocumentReference usernameReference = fStore.collection("Users").document(model.getHomeAuthorUid());
        usernameReference.addSnapshotListener(HomeView.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                pAuthor.setText(documentSnapShot.getString("Username"));

                if(Objects.equals(documentSnapShot.getString("Partner"), "1")){
                    partnerBadge.setVisibility(View.VISIBLE);
                } else{
                    partnerBadge.setVisibility(View.GONE);
                }
            }
        });

        StorageReference profileRef = storageReference.child("ProfileImage/"+model.homeAuthorUid+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(prof_img);
            }
        });

        //count votes
        votecounter();

        //comment recycler
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

        adapter2 = new FirestoreRecyclerAdapter<CommentModel, ViewHolder>(options) {
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

        commentRecycler.setAdapter(adapter2);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRefresh() {
                commentRecycler.setAdapter(null);
                commentRecycler.setAdapter(adapter2);
                adapter2.startListening();
                adapter2.notifyDataSetChanged();
                votecounter();
                swipeRefresh.setRefreshing(false);
            }
        });

    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView author, body, authorUid, docId, timestamp, upvotecountcomment, downvotecountcomment;
        EditText pReply;
        TextInputLayout tilpReply;
        ImageView prof_imgreply, partnerBadge;
        Chip replyChip;
        ToggleButton upvotecomment, downvotecomment;
        ToggleButton replypop;
        Button replyBtn, replyCount;
        CommentModel model;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.commentAuthor);
            body = itemView.findViewById(R.id.commentBody);
            docId = itemView.findViewById(R.id.docId);
            authorUid = itemView.findViewById(R.id.commentAuthorUid);
            timestamp = itemView.findViewById(R.id.timestamp);
            prof_imgreply = itemView.findViewById(R.id.prof_img);
            replypop = itemView.findViewById(R.id.replypop);
            tilpReply = itemView.findViewById(R.id.tilpReply);
            replyBtn = itemView.findViewById(R.id.replyBtn);
            replyCount = itemView.findViewById(R.id.replyCount);
            replyChip = itemView.findViewById(R.id.replyChip);
            pReply = itemView.findViewById(R.id.pReply);

            upvotecomment = itemView.findViewById(R.id.upvotecomment);
            downvotecomment = itemView.findViewById(R.id.downvotecomment);
            upvotecountcomment = itemView.findViewById(R.id.upvotecount);
            downvotecountcomment = itemView.findViewById(R.id.downvotecount);
            partnerBadge = itemView.findViewById(R.id.partnerBadge);

            replyChip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tilpReply.setVisibility(View.GONE);
                    replyBtn.setVisibility(View.GONE);
                    replyChip.setVisibility(View.GONE);
                    replypop.setChecked(false);
                    pReply.setText("");
                }
            });

            replypop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (user != null && !user.isAnonymous()) {
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
                    } else{
                        ShowPopup();
                        replypop.setChecked(false);
                    }
                }
            });

            replyCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RepliesToComment bottomsheetfragment = new RepliesToComment();

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("model", model);

                    bottomsheetfragment.setArguments(bundle);

                    bottomsheetfragment.show(getSupportFragmentManager(), bottomsheetfragment.getTag());
                }
            });
        }

        public void bind(CommentModel commentModel){
            model = commentModel;
            body.setText(commentModel.commentBody);
            docId.setText(commentModel.docId);
            authorUid.setText(commentModel.commentUid);
            TimeAgo2 timeAgo2 = new TimeAgo2();
            if(commentModel.getCommentPostDate() != null){
                String timeago = timeAgo2.covertTimeToText(commentModel.getCommentPostDate().toString());
                timestamp.setText(timeago);
            }

            DocumentReference usernameReference = fStore.collection("Users").document(commentModel.commentUid);
            usernameReference.addSnapshotListener(HomeView.this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                    author.setText(documentSnapShot.getString("Username"));

                    replyChip.setText("@"+author.getText().toString());
                    if(Objects.equals(documentSnapShot.getString("Partner"), "1")){
                        partnerBadge.setVisibility(View.VISIBLE);
                    } else{
                        partnerBadge.setVisibility(View.GONE);
                    }
                }
            });


            //post counter
            CollectionReference reply = fStore.collection("Post").document(pdocId.getText().toString())
                    .collection("comment").document(docId.getText().toString())
                    .collection("reply");
            AggregateQuery replies = reply.count();
            replies.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                    AggregateQuerySnapshot snapshot = task.getResult();
                    if (snapshot.getCount() > 1) {
                        replyCount.setVisibility(View.VISIBLE);
                        replyCount.setText(snapshot.getCount() + " replies");
                    }else if (snapshot.getCount() == 1) {
                        replyCount.setVisibility(View.VISIBLE);
                        replyCount.setText(snapshot.getCount() + " reply");
                    }else {
                        replyCount.setVisibility(View.GONE);
                    }
                }
            });

            //vote counter
            CollectionReference vote = fStore.collection("Post").document(commentModel.getCommentPostOrigin())
                    .collection("comment").document(commentModel.docId)
                    .collection("vote");
            AggregateQuery Upvotecount = vote.whereEqualTo("Upvote", true).count();
            AggregateQuery Downvotecount = vote.whereEqualTo("Downvote", true).count();
            Upvotecount.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                    AggregateQuerySnapshot snapshot = task.getResult();
                    if (snapshot.getCount() != 0) {
                        upvotecountcomment.setText("" + snapshot.getCount());
                    } else {
                        upvotecountcomment.setText("Upvote");
                    }
                }
            });
            Downvotecount.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                    AggregateQuerySnapshot snapshot = task.getResult();
                    if (snapshot.getCount() != 0){
                        downvotecountcomment.setText(""+snapshot.getCount());
                    }else {
                        downvotecountcomment.setText("Downvote");
                    }
                }
            });

            //vote history for logged in user
            if (user != null && !user.isAnonymous()) {
                DocumentReference documentReference = fStore.collection("Post").document(commentModel.getCommentPostOrigin())
                        .collection("comment").document(commentModel.docId)
                        .collection("vote")
                        .document(user.getUid());
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            documentReference.addSnapshotListener(HomeView.this, new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                                    if(Boolean.TRUE.equals(documentSnapShot.getBoolean("Upvote"))){
                                        upvotecomment.setChecked(true);
                                        downvotecomment.setChecked(false);
                                    }else if (Boolean.TRUE.equals(documentSnapShot.getBoolean("Downvote"))){
                                        downvotecomment.setChecked(true);
                                        upvotecomment.setChecked(false);
                                    }else{
                                        upvotecomment.setChecked(false);
                                        downvotecomment.setChecked(false);
                                    }
                                }
                            });
                        }else{
                            upvotecomment.setChecked(false);
                            downvotecomment.setChecked(false);
                        }
                    }
                });
            }

            //place vote
            if (user != null && !user.isAnonymous()) {
                upvotecomment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (upvotecomment.isChecked()) {
                            Map<String, Object> vote = new HashMap<>();
                            vote.put("Upvote", true);

                            upvotecomment.setEnabled(false);
                            downvotecomment.setEnabled(false);
                            fStore.collection("Post").document(commentModel.getCommentPostOrigin())
                                    .collection("comment").document(commentModel.docId)
                                    .collection("vote")
                                    .document(user.getUid()).set(vote).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            adapter2.notifyItemChanged(getAdapterPosition());
                                            upvotecomment.setEnabled(true);
                                            downvotecomment.setEnabled(true);
                                        }
                                    });
                        }else if (!upvotecomment.isChecked() && !downvotecomment.isChecked()){
                            upvotecomment.setEnabled(false);
                            downvotecomment.setEnabled(false);
                            fStore.collection("Post").document(commentModel.getCommentPostOrigin())
                                    .collection("comment").document(commentModel.docId)
                                    .collection("vote")
                                    .document(user.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            adapter2.notifyItemChanged(getAdapterPosition());
                                            upvotecomment.setEnabled(true);
                                            downvotecomment.setEnabled(true);
                                        }
                                    });
                        }
                    }
                });
                downvotecomment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (downvotecomment.isChecked()) {
                            Map<String, Object> vote = new HashMap<>();
                            vote.put("Downvote", true);


                            downvotecomment.setEnabled(false);
                            upvotecomment.setEnabled(false);
                            fStore.collection("Post").document(commentModel.getCommentPostOrigin())
                                    .collection("comment").document(commentModel.docId)
                                    .collection("vote")
                                    .document(user.getUid()).set(vote).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            adapter2.notifyItemChanged(getAdapterPosition());
                                            downvotecomment.setEnabled(true);
                                            upvotecomment.setEnabled(true);
                                        }
                                    });
                        }else if (!upvotecomment.isChecked() && !downvotecomment.isChecked()){
                            downvotecomment.setEnabled(false);
                            upvotecomment.setEnabled(false);
                            fStore.collection("Post").document(commentModel.getCommentPostOrigin())
                                    .collection("comment").document(commentModel.docId)
                                    .collection("vote")
                                    .document(user.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            adapter2.notifyItemChanged(getAdapterPosition());
                                            downvotecomment.setEnabled(true);
                                            upvotecomment.setEnabled(true);
                                        }
                                    });
                        }
                    }
                });
            } else{
                upvotecomment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ShowPopup();
                        upvotecomment.setChecked(false);
                        downvotecomment.setChecked(false);
                    }
                });

                downvotecomment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ShowPopup();
                        upvotecomment.setChecked(false);
                        downvotecomment.setChecked(false);
                    }
                });
            }

            upvotecomment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    adapter2.notifyItemChanged(getAdapterPosition());
                }
            });
            downvotecomment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    adapter2.notifyItemChanged(getAdapterPosition());
                }
            });

            //reply to comment
            pReply.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                }
                @Override
                public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                    if(!s.toString().isEmpty()){
                        tilpReply.setError(null);
                        if (tilpReply.getChildCount() == 2)
                            tilpReply.getChildAt(1).setVisibility(View.GONE);
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            replyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showProgressDialog();
                    if (user != null && !user.isAnonymous()) {
                        if(pReply.getText().toString().length() == 0 || !TextUtils.isEmpty(tilpReply.getError())){
                            if(pReply.getText().toString().length() == 0) {
                                if (tilpReply.getChildCount() == 2)
                                    tilpReply.getChildAt(1).setVisibility(View.VISIBLE);
                                tilpReply.setError("required*");
                                pReply.setText("");
                            }
                            pReply.requestFocus();
                            hideProgressDialog();
                        }else {
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
                                    comment.put("replyPostOrigin", commentModel.docId);

                                    documentReference.collection("reply").add(comment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            Toast.makeText(HomeView.this, "Comment Success", Toast.LENGTH_SHORT).show();
                                            replyChip.performCloseIconClick();
                                            pReply.setText("");
                                            adapter2.notifyDataSetChanged();
                                            pReply.clearFocus();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(HomeView.this, "Comment Failed", Toast.LENGTH_SHORT).show();
                                            pReply.setText("");
                                            showEmptyView();
                                            adapter2.notifyDataSetChanged();
                                            pReply.clearFocus();
                                        }
                                    });
                                    hideProgressDialog();
                                }
                            });
                        }
                    }else{
                        ShowPopup();
                        hideProgressDialog();
                    }

                }
            });

            //profile img per post
            StorageReference profileRef = storageReference.child("ProfileImage/"+commentModel.commentUid+"/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(prof_imgreply);
                }
            });
        }
    }

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

    @Override
    protected void onStart() {
        super.onStart();
        showProgressDialog();
        if (isNetworkAvailable()){
            adapter2.startListening();
            hideProgressDialog();
        }else{
            showEmptyView();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (adapter2 != null) {
            adapter2.stopListening();
        } else{
            hideProgressDialog();
            showEmptyView();
        }
    }

    public void ShowPopup(){
        Button registerbutton, loginButton;
        NeumorphFloatingActionButton close_popup;
        guestDialog.setContentView(R.layout.custom_popup_guest);

        close_popup = (NeumorphFloatingActionButton) guestDialog.findViewById(R.id.close_popup);
        loginButton = (Button) guestDialog.findViewById(R.id.loginButton);
        registerbutton = (Button) guestDialog.findViewById(R.id.registerbutton);

        close_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guestDialog.dismiss();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Register.class));
                finish();
            }
        });
        guestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        guestDialog.show();
    }

    //loading and error view
    public void showProgressDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.progress_layout, null,false);
        builder.setView(v);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    public void hideProgressDialog(){
        dialog.dismiss();
    }

    public void showEmptyView(){
        startActivity(new Intent(HomeView.this, NoConnection.class));
        finish();
    }
    //check connection
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void postpopup(View v, boolean isUser, String docid){
        PopupMenu popup = new PopupMenu(this, v, Gravity.END);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.post_options);
        confirmdocid=docid;
        if (!isUser){
            popup.getMenu().findItem(R.id.delete).setVisible(false);
            popup.getMenu().findItem(R.id.report).setVisible(true);

            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            DocumentReference docIdRef = rootRef.collection("Post").document(confirmdocid).collection("report").document(user.getUid());
            docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            popup.getMenu().findItem(R.id.report).setEnabled(false);
                        } else {
                            popup.getMenu().findItem(R.id.report).setEnabled(true);
                        }
                    } else {
                        Log.d(TAG, "Failed with: ", task.getException());
                    }
                }
            });
        }else{
            popup.getMenu().findItem(R.id.delete).setVisible(true);
            popup.getMenu().findItem(R.id.report).setVisible(false);
        }
        popup.show();
    }

    String confirmdocid;

    public void confirmPopup(String Title, String subtitle, String btn){
        Button registerbutton, loginButton;
        NeumorphFloatingActionButton close_popup;
        guestDialog.setContentView(R.layout.custom_popup_guest);
        TextView popup_title, popup_subtitle;

        close_popup = (NeumorphFloatingActionButton) guestDialog.findViewById(R.id.close_popup);
        loginButton = (Button) guestDialog.findViewById(R.id.loginButton);
        registerbutton = (Button) guestDialog.findViewById(R.id.registerbutton);
        popup_title = (TextView) guestDialog.findViewById(R.id.popup_title);
        popup_subtitle = (TextView) guestDialog.findViewById(R.id.popup_subtitle);

        loginButton.setText("Confirm");
        registerbutton.setText("Back");
        popup_title.setText(Title);
        popup_subtitle.setText(subtitle);

        close_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guestDialog.dismiss();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(btn, "delete")){
                    delete(confirmdocid);
                }else{
                    report(confirmdocid);
                }
                guestDialog.dismiss();
            }
        });
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guestDialog.dismiss();
            }
        });
        guestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        guestDialog.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.delete:
                confirmPopup("Delete post?", "This action cannot be undone.", "delete");
                return true;
            case R.id.report:
                confirmPopup("Report post?", "This action cannot be undone.", "report");
                return true;
            default:
                return false;
        }
    }

    private void delete(String docId){
        DocumentReference status = fStore.collection("Post").document(docId);
        Map<String, Object> edited = new HashMap<>();

        edited.put("delete", true);

        status.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(HomeView.this, "Post deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomeView.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        });
        onBackPressed();
    }

    private void report(String docId){

        Map<String, Object> vote = new HashMap<>();
        vote.put("report", true);

        fStore.collection("Post").document(docId).collection("report")
                .document(user.getUid()).set(vote).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });


        DocumentReference status = fStore.collection("Post").document(docId);
        Map<String, Object> edited = new HashMap<>();

        edited.put("report", FieldValue.increment(1));

        status.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(HomeView.this, "Post reported", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomeView.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}