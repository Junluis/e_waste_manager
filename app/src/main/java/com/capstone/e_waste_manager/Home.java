package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.widget.ImageButton;
import android.widget.TextView;

import com.capstone.e_waste_manager.Class.TimeAgo2;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class Home extends AppCompatActivity{

    FirebaseFirestore fStore;

    DrawerLayout drawerLayout;
    SearchView postSearch;
    RecyclerView homeRecycler;
    ImageButton homeBtnHome, homeBtnPost, homeBtnLearn;
    MaterialButton request;
    ImageView menu_nav, profile_nav;
    NavigationView navView_profile, navView_menu;
    SwipeRefreshLayout swipeRefresh;

    FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    StorageReference storageReference;
    FirebaseAuth fAuth;

    TextView signout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //drawer start
        drawerLayout = findViewById(R.id.drawerLayout);
        navView_profile = findViewById(R.id.nav_viewright);
        navView_menu = findViewById(R.id.nav_viewleft);
        request = findViewById(R.id.request);
        menu_nav = findViewById(R.id.menu_nav);
        profile_nav = findViewById(R.id.profile_nav);
        postSearch = findViewById(R.id.postSearch);

        menu_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        profile_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });
        //drawer end

        //drawer buttons start
        navView_profile.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.profilepg:
                    {
                        startActivity(new Intent(Home.this, UserProfilePage.class));
                        break;
                    }
                    case R.id.notificationpg:
                    {
                        startActivity(new Intent(Home.this, Notification.class));
                        break;
                    }
                }
                return false;
            }
        });
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
                    case R.id.donate:
                    {
                        startActivity(new Intent(Home.this, Donate.class));
                        break;
                    }
                }
                return false;
            }
        });
        //drawer buttons end

        homeRecycler = findViewById(R.id.homeRecycler);
        homeBtnHome = findViewById(R.id.homeBtnHome);
        homeBtnPost = findViewById(R.id.homeBtnPost);
        homeBtnLearn = findViewById(R.id.homeBtnLearn);
        signout = findViewById(R.id.signout);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        homeBtnHome.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Home.class)));
        homeBtnPost.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Post.class)));
        homeBtnLearn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Learn.class)));

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();

        homeRecycler = findViewById(R.id.homeRecycler);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        homeRecycler.setLayoutManager(linearLayoutManager);

        homeRecycler.setItemAnimator(null);

        Query query = fStore.collection("Post")
                .orderBy("homePostDate", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<HomeModel> options = new FirestoreRecyclerOptions.Builder<HomeModel>()
                .setQuery(query, HomeModel.class)
                .build();

        //swipe up to refresh.. refresh deleted posts or see changed time
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
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

        homeRecycler.setAdapter(adapter);

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
        TextView author, title, body, authorUid, docId, timestamp;
        ImageView prof_img;
        HomeModel model;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.homeAuthor);
            title = itemView.findViewById(R.id.homeTitle);
            body = itemView.findViewById(R.id.homeBody);
            docId = itemView.findViewById(R.id.docId);
            authorUid = itemView.findViewById(R.id.homeAuthorUid);
            timestamp = itemView.findViewById(R.id.timestamp);
            prof_img = itemView.findViewById(R.id.prof_img);



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

            StorageReference profileRef = storageReference.child("ProfileImage/"+homeModel.homeAuthorUid+"/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(prof_img);
                }
            });

        }
    }

}