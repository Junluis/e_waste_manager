package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class Home extends AppCompatActivity implements HomeInterface{

    ArrayList<HomeModel> homeModelsArrayList;
    HomeAdapter homeAdapter;
    ProgressDialog pd;

    FirebaseFirestore fStore;

    DrawerLayout drawerLayout;
    EditText postSearch;
    RecyclerView homeRecycler;
    ImageButton homeBtnHome, homeBtnPost, homeBtnLearn;
    MaterialButton request;
    ImageView menu_nav, profile_nav;
    NavigationView navView_profile, navView_menu;

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

        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Fetching Data...");
        pd.show();

        fStore = FirebaseFirestore.getInstance();
        homeModelsArrayList = new ArrayList<HomeModel>();
        homeAdapter = new HomeAdapter(Home.this, homeModelsArrayList, this);

        homeRecycler = findViewById(R.id.homeRecycler);
        homeRecycler.setHasFixedSize(true);
        homeRecycler.setLayoutManager(new LinearLayoutManager(this));
        homeRecycler.setAdapter(homeAdapter);

        EventChangeListener();

    }

    private void EventChangeListener() {
        fStore.collection("Post").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    if(pd.isShowing())
                        pd.dismiss();
                    Log.e("Firestore error", error.getMessage());
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()){
                    homeModelsArrayList.add(dc.getDocument().toObject(HomeModel.class));
                }
                homeAdapter.notifyDataSetChanged();
                if(pd.isShowing())
                    pd.dismiss();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(Home.this, HomeView.class);

        intent.putExtra("homeTitle", homeModelsArrayList.get(position).getHomeTitle());
        intent.putExtra("homeAuthor", homeModelsArrayList.get(position).getHomeAuthor());
        intent.putExtra("homeBody", homeModelsArrayList.get(position).getHomeBody());

        startActivity(intent);

    }
}