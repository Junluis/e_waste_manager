package com.capstone.e_waste_manager.Fragments;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.capstone.e_waste_manager.Class.TimeAgo2;
import com.capstone.e_waste_manager.Home;
import com.capstone.e_waste_manager.HomeModel;
import com.capstone.e_waste_manager.HomeView;
import com.capstone.e_waste_manager.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.github.ponnamkarthik.richlinkpreview.RichLinkViewTwitter;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import soup.neumorphism.NeumorphFloatingActionButton;


public class ProfilePostFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {


    public ProfilePostFragment() {
        // Required empty public constructor
    }
    ProgressDialog pd;

    RecyclerView prof_posts;
    SwipeRefreshLayout swipeRefresh;
    String userID;

    Query query;
    PagingConfig config;

    FirebaseFirestore fStore;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirestorePagingAdapter<HomeModel, ViewHolder> adapter;
    FirebaseUser user;

    Dialog guestDialog;

    LinearLayoutManager linearLayoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_post, container, false);

        //firebase
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        userID = fAuth.getCurrentUser().getUid();

        prof_posts = (RecyclerView) view.findViewById(R.id.prof_posts);
        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        prof_posts.setLayoutManager(linearLayoutManager);

        storageReference = FirebaseStorage.getInstance().getReference();

        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        prof_posts.setLayoutManager(linearLayoutManager);

        prof_posts.setItemAnimator(null);

        //Guest dialog
        guestDialog = new Dialog(getContext());

        //forums
        query = fStore.collection("Post").orderBy("delete", Query.Direction.DESCENDING)
                .orderBy("homePostDate", Query.Direction.DESCENDING).whereNotEqualTo("delete", true).whereEqualTo("homeAuthorUid", userID)
                .limit(3);

        config = new PagingConfig(/* page size */ 2, /* prefetchDistance */ 2,
                /* enablePlaceHolders */ false);

        FirestorePagingOptions<HomeModel> options = new FirestorePagingOptions.Builder<HomeModel>()
                .setLifecycleOwner(this)
                .setQuery(query, config, HomeModel.class)
                .build();

        adapter =
                new FirestorePagingAdapter<HomeModel, ViewHolder>(options) {
                    @NonNull
                    @Override
                    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        // Create the ItemViewHolder
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.home_each, parent,false);
                        return new ViewHolder(view);
                        // ...
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder holder,
                                                    int position,
                                                    @NonNull HomeModel model) {
                        // Bind the item to the view holder
                        holder.bind(model);
                        // ...
                    }


                };

        prof_posts.setAdapter(adapter);

        adapter.addLoadStateListener(new Function1<CombinedLoadStates, Unit>() {
            @Override
            public Unit invoke(CombinedLoadStates states) {
                LoadState refresh = states.getRefresh();
                LoadState append = states.getAppend();

                if (refresh instanceof LoadState.Error || append instanceof LoadState.Error) {
                    // The previous load (either initial or additional) failed. Call
                    // the retry() method in order to retry the load operation.
                    // ...
                    adapter.retry();
                }

                if (refresh instanceof LoadState.Loading) {
                    // The initial Load has begun
                    // ...
                }

                if (append instanceof LoadState.Loading) {
                    // The adapter has started to load an additional page
                    // ...
                }

                if (append instanceof LoadState.NotLoading) {
                    LoadState.NotLoading notLoading = (LoadState.NotLoading) append;
                    if (notLoading.getEndOfPaginationReached()) {
                        // The adapter has finished loading all of the data set
                        // ...
                        return null;
                    }

                    if (refresh instanceof LoadState.NotLoading) {
                        // The previous load (either initial or additional) completed
                        // ...
                        return null;
                    }
                }
                return null;
            }
        });

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
        TextView author, title, body, authorUid, docId, timestamp, upvotecount, downvotecount, urltext;
        Button addcoment;
        ToggleButton upvote, downvote;
        ImageView prof_img, partnerBadge, postImg;
        HomeModel model;
        FrameLayout urllink;
        ImageButton more;

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
            urllink = itemView.findViewById(R.id.urllink);
            postImg = itemView.findViewById(R.id.postImg);
            urltext = itemView.findViewById(R.id.urltext);
            more = itemView.findViewById(R.id.more);

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
            urltext.setText(homeModel.url);
            TimeAgo2 timeAgo2 = new TimeAgo2();
            if(homeModel.getHomePostDate() != null){
                String timeago = timeAgo2.covertTimeToText(homeModel.getHomePostDate().toString());
                timestamp.setText(timeago);
            }

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (homeModel.homeAuthorUid.equals(user.getUid())){
                        postpopup(more, true, homeModel.docId);
                    } else{
                        postpopup(more, false, homeModel.docId);
                    }
                }
            });

            if (body.getText().length() == 0){
                body.setVisibility(View.GONE);
            }else{
                body.setVisibility(View.VISIBLE);
            }

            if(urltext.getText() != null && Patterns.WEB_URL.matcher(urltext.getText().toString()).matches()){
                urltext.setVisibility(View.VISIBLE);
                RichLinkViewTwitter richLinkView = (RichLinkViewTwitter) View.inflate(getContext(), R.layout.links, null);
                richLinkView.setLink(urltext.getText().toString(), new ViewListener() {
                    @Override
                    public void onSuccess(boolean status) {
                        urllink.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                    }
                });
                urllink.addView(richLinkView);
            }else{
                urltext.setVisibility(View.GONE);
                urllink.setVisibility(View.GONE);
            }


            if (homeModel.hasImage != null && homeModel.hasImage){
                postImg.setVisibility(View.VISIBLE);
                StorageReference profileRef = storageReference.child("ForumPost/"+homeModel.docId+"/postimg.jpg");
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(postImg);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }else{
                postImg.setImageBitmap(null);
                postImg.setVisibility(View.GONE);
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
            if (user != null && !user.isAnonymous()) {
                DocumentReference documentReference = fStore.collection("Post").document(docId.getText().toString()).collection("vote")
                        .document(user.getUid());
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
            }

            //place vote
            if (user != null && !user.isAnonymous()) {
                upvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (upvote.isChecked()) {
                            Map<String, Object> vote = new HashMap<>();
                            vote.put("Upvote", true);

                            upvote.setEnabled(false);
                            downvote.setEnabled(false);
                            fStore.collection("Post").document(docId.getText().toString()).collection("vote")
                                    .document(user.getUid()).set(vote).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                    .document(user.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                    .document(user.getUid()).set(vote).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                    .document(user.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
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
            } else{
                upvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        upvote.setChecked(false);
                        downvote.setChecked(false);
                    }
                });

                downvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        upvote.setChecked(false);
                        downvote.setChecked(false);
                    }
                });
            }

            upvote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    adapter.notifyItemChanged(getAdapterPosition());
                }
            });
            downvote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    adapter.notifyItemChanged(getAdapterPosition());
                }
            });

            //profile image per post
            StorageReference profileRef = storageReference.child("ProfileImage/"+authorUid.getText().toString()+"/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(prof_img);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "No Profile Image");
                }
            });
        }
    }

    public void refresh(){
        swipeRefresh.setRefreshing(true);
        adapter.stopListening();
        prof_posts.setAdapter(null);
        prof_posts.setAdapter(adapter);
        adapter.startListening();
        adapter.refresh();
        swipeRefresh.setRefreshing(false);
    }

    public void postpopup(View v, boolean isUser, String docid){
        PopupMenu popup = new PopupMenu(getContext(), v, Gravity.END);
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
                Toast.makeText(getContext(), "Post deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        });
        refresh();
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
                Toast.makeText(getContext(), "Post reported", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}