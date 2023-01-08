package com.capstone.e_waste_manager;


import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.paging.PagedList;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.capstone.e_waste_manager.Class.TimeAgo2;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
import io.github.ponnamkarthik.richlinkpreview.RichLinkViewSkype;
import io.github.ponnamkarthik.richlinkpreview.RichLinkViewTelegram;
import io.github.ponnamkarthik.richlinkpreview.RichLinkViewTwitter;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import soup.neumorphism.NeumorphFloatingActionButton;


public class Home extends AppCompatActivity{

    //firebase
    FirebaseFirestore fStore;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirestorePagingAdapter<HomeModel, ViewHolder> adapter;
    FirebaseUser user;

    DrawerLayout drawerLayout;
    SearchView postSearch;
    RecyclerView homeRecycler;
    ImageButton homeBtnHome, homeBtnPost, homeBtnLearn, search_btn;
    MaterialButton request;
    ImageView menu_nav, prof_img;
    NavigationView navView_profile, navView_menu;
    SwipeRefreshLayout swipeRefresh;

    LinearLayoutManager linearLayoutManager;

    TextView signout, titlePage, requesttext;

    Dialog guestDialog;
    AlertDialog dialog;
    ConstraintLayout emptyView;
    LinearLayout activityHome;

    Query querysearch, query;

    PagingConfig config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //request
        request = findViewById(R.id.request);
        request.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Request.class)));

        //transparent status
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        //drawers
        drawerLayout = findViewById(R.id.drawerLayout);
        navView_profile = findViewById(R.id.nav_viewright);
        navView_menu = findViewById(R.id.nav_viewleft);
        request = findViewById(R.id.request);
        requesttext = findViewById(R.id.requesttext);
        menu_nav = findViewById(R.id.menu_nav);
        prof_img = findViewById(R.id.prof_img);
        signout = findViewById(R.id.signout);

        //firebase
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        //bottom nav
        homeBtnHome = findViewById(R.id.homeBtnHome);
        homeBtnPost = findViewById(R.id.homeBtnPost);
        homeBtnLearn = findViewById(R.id.homeBtnLearn);

        //forum
        homeRecycler = findViewById(R.id.homeRecycler);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        //search
        postSearch = findViewById(R.id.postSearch);
        titlePage = findViewById(R.id.titlePage);
        search_btn = findViewById(R.id.search_btn);

        //layouts
        activityHome = findViewById(R.id.activityHome);
        emptyView = findViewById(R.id.emptyView);

        //search
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postSearch.setVisibility(View.VISIBLE);
                titlePage.setVisibility(View.GONE);
                search_btn.setVisibility(View.GONE);
                postSearch.requestFocus();
            }
        });
        postSearch.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    postSearch.setVisibility(View.GONE);
                    titlePage.setVisibility(View.VISIBLE);
                    search_btn.setVisibility(View.VISIBLE);
                }
            }
        });

        postSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                querysearch = fStore.collection("Post").whereGreaterThanOrEqualTo("homeTitle",newText)
                        .whereLessThanOrEqualTo("homeTitle",newText + "\uf8ff");

                if(newText.isEmpty()){
                    FirestorePagingOptions<HomeModel> options = new FirestorePagingOptions.Builder<HomeModel>()
                            .setLifecycleOwner(Home.this)
                            .setQuery(query, config, HomeModel.class)
                            .build();
                    adapter.updateOptions(options);
                    homeRecycler.setAdapter(null);
                    homeRecycler.setAdapter(adapter);
                    search_btn.getDrawable().setTint(ContextCompat.getColor(Home.this, R.color.darkgray));
                }else{
                    FirestorePagingOptions<HomeModel> options2 = new FirestorePagingOptions.Builder<HomeModel>()
                            .setLifecycleOwner(Home.this)
                            .setQuery(querysearch, config, HomeModel.class)
                            .build();
                    adapter.updateOptions(options2);
                    homeRecycler.setAdapter(null);
                    homeRecycler.setAdapter(adapter);
                    search_btn.getDrawable().setTint(ContextCompat.getColor(Home.this, R.color.green1));
                }
                return false;
            }
        });


        //bottomNav btn
        homeBtnHome.setOnClickListener(v -> refresh());
        if (user != null && !user.isAnonymous()) {
            homeBtnPost.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Post.class)));
        } else{
            homeBtnPost.setOnClickListener(v -> ShowPopup());
        }
        homeBtnLearn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Learn.class));
        });

        //Guest dialog
        guestDialog = new Dialog(this);

        //drawer
        menu_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        prof_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        //transparent inset
        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout, (v, windowInsets) -> {
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

        //side drawers
        if (user != null && !user.isAnonymous()) {
            View hView = navView_profile.inflateHeaderView(R.layout.nav_header);
            TextView prof_username_header = (TextView) hView.findViewById(R.id.prof_username);
            TextView prof_email_header = (TextView) hView.findViewById(R.id.prof_email);
            TextView prof_bio_header = (TextView) hView.findViewById(R.id.prof_bio);
            ImageView prof_img_header = (ImageView) hView.findViewById(R.id.prof_img);
            ImageView partnerBadge_header = (ImageView) hView.findViewById(R.id.partnerBadge);

            DocumentReference documentReference = fStore.collection("Users").document(user.getUid());
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                    prof_username_header.setText(documentSnapShot.getString("Username"));
                    prof_email_header.setText(documentSnapShot.getString("Email"));
                    prof_bio_header.setText(documentSnapShot.getString("Bio"));

                    if(!prof_bio_header.getText().toString().equals("")){
                        prof_bio_header.setTextColor(Color.parseColor("#000000"));
                        prof_bio_header.setVisibility(View.VISIBLE);
                    }else{
                        prof_bio_header.setVisibility(View.GONE);
                    }
                    if(Objects.equals(documentSnapShot.getString("Partner"), "1")){
                        partnerBadge_header.setVisibility(View.VISIBLE);
                        navView_menu.getMenu().findItem(R.id.adddisposalpg).setVisible(true);
                    } else if (Objects.equals(documentSnapShot.getString("Partner"), "0")){
                        partnerBadge_header.setVisibility(View.GONE);
                    } else{
                        navView_profile.getMenu().findItem(R.id.notificationpg).setVisible(true);
                    }
                    StorageReference profileRef = storageReference.child("ProfileImage/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(prof_img_header);
                        }
                    });

                }
            });
            StorageReference profileRef = storageReference.child("ProfileImage/"+user.getUid()+"/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(prof_img);
                }
            });
        }else {
            View hView = navView_profile.inflateHeaderView(R.layout.guest_header);
        }

        if (user != null && !user.isAnonymous()) {
            navView_profile.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId())
                    {
                        case R.id.profilepg:
                        {
                            if (user != null && !user.isAnonymous()) {
                                startActivity(new Intent(Home.this, UserProfilePage.class));
                            } else{
                                ShowPopup();
                            }
                            break;
                        }
                        case R.id.notificationpg:
                        {
                            if (user != null && !user.isAnonymous()) {
                                startActivity(new Intent(Home.this, RequestTab.class));
                            } else{
                                ShowPopup();
                            }
                            break;
                        }
                    }
                    return false;
                }
            });

            signout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }
            });
        } else{
            navView_profile.getMenu().clear();
            navView_profile.inflateMenu(R.menu.nav_guestmenu);
            navView_profile.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId())
                    {
                        case R.id.loginpg:
                        {
                            startActivity(new Intent(Home.this, Login.class));
                            break;
                        }
                        case R.id.registerpg:
                        {
                            startActivity(new Intent(Home.this, Register.class));
                            break;
                        }
                    }
                    return false;
                }
            });
            signout.setVisibility(View.GONE);
        }
        navView_menu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.disposal:
                    {
                        startActivity(new Intent(Home.this, DisposalLocation.class));
                        break;
                    }
                    case R.id.adddisposalpg:
                    {
                        startActivity(new Intent(Home.this, AddDisposal.class));
                        break;
                    }
                    case R.id.donate:
                    {
                        startActivity(new Intent(Home.this, Donate.class));
                        break;
                    }
                }
                return false;
            }
        });
        //drawer end

        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        homeRecycler.setLayoutManager(linearLayoutManager);

        homeRecycler.setItemAnimator(null);

        //forums
        query = fStore.collection("Post")
                .orderBy("homePostDate", Query.Direction.DESCENDING)
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

        homeRecycler.setAdapter(adapter);

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
    }

    //forum
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView author, title, body, authorUid, docId, timestamp, upvotecount, downvotecount, urltext;
        Button addcoment;
        ToggleButton upvote, downvote;
        ImageView prof_img, partnerBadge, postImg;
        HomeModel model;
        FrameLayout urllink;

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

            if (body.getText().length() == 0){
                body.setVisibility(View.GONE);
            }else{
                body.setVisibility(View.VISIBLE);
            }

            if(urltext.getText() != null && Patterns.WEB_URL.matcher(urltext.getText().toString()).matches()){
                urltext.setVisibility(View.VISIBLE);
                RichLinkViewTwitter richLinkView = (RichLinkViewTwitter) View.inflate(Home.this, R.layout.links, null);
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
            usernameReference.addSnapshotListener(Home.this, new EventListener<DocumentSnapshot>() {
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
                            documentReference.addSnapshotListener(Home.this, new EventListener<DocumentSnapshot>() {
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

    @Override
    protected void onStart() {
        showProgressDialog();
        if (isNetworkAvailable()){
            //profile details
            adapter.startListening();
            if (user != null && !user.isAnonymous()) {
                StorageReference profileRef = storageReference.child("ProfileImage/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(prof_img);
                        hideProgressDialog();
                        hideEmptyView();
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressDialog();
                    }
                });

                DocumentReference usernameReference = fStore.collection("Users").document(user.getUid());
                usernameReference.addSnapshotListener(Home.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                        if(Objects.equals(documentSnapShot.getString("Partner"), "0")){
                            request.setVisibility(View.VISIBLE);
                            requesttext.setVisibility(View.VISIBLE);
                            if (Objects.equals(documentSnapShot.getBoolean("isReq"), true)){
                                request.setEnabled(false);
                                requesttext.setText("Partnership request submitted. Wait for approval.");
                            } else if (Objects.equals(documentSnapShot.getBoolean("isReq"), false)){
                                request.setEnabled(true);
                                requesttext.setText("Partnership request denied. Try again");
                            }
                        } else{
                            request.setVisibility(View.GONE);
                            requesttext.setVisibility(View.GONE);
                        }

                    }
                });

            } else {
                signInAnonymously();
            }
        }else {
            hideProgressDialog();
            showEmptyView();
        }
        super.onStart();

    }

    @Override
    protected void onStop() {
        if (adapter != null) {
            adapter.stopListening();
        }
        super.onStop();
    }



    private void signInAnonymously() {
        fAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        adapter.startListening();
                        hideProgressDialog();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        showEmptyView();
                    }
                });
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
            }
        });
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Register.class));
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
        emptyView.setVisibility(View.VISIBLE);
        activityHome.setVisibility(View.GONE);
    }
    public void hideEmptyView(){
        emptyView.setVisibility(View.GONE);
        activityHome.setVisibility(View.VISIBLE);
    }

    //check connection
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void refresh(){
        swipeRefresh.setRefreshing(true);
        adapter.stopListening();
        homeRecycler.setAdapter(null);
        if (user != null && !user.isAnonymous()) {
            StorageReference profileRef = storageReference.child("ProfileImage/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    View hView = navView_profile.getHeaderView(0);
                    ImageView prof_img_header = (ImageView) hView.findViewById(R.id.prof_img);
                    Picasso.get().load(uri).into(prof_img);
                    Picasso.get().load(uri).into(prof_img_header);
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgressDialog();
                }
            });
        }
        homeRecycler.setAdapter(adapter);
        adapter.startListening();
        adapter.refresh();
        swipeRefresh.setRefreshing(false);
    }
}