package com.capstone.e_waste_manager;

import static com.google.firebase.firestore.DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class Home extends AppCompatActivity implements HomeInterface{

    ArrayList<HomeModel> homeModelsArrayList;
    HomeAdapter homeAdapter;
    ProgressDialog pd;

    FirebaseFirestore fStore;
    String search;
    DrawerLayout drawerLayout;
    EditText postSearch;
    RecyclerView homeRecycler;
    ImageButton homeBtnHome, homeBtnPost, homeBtnLearn;
    SearchView homeSearch;
    MaterialButton request;
    ImageView menu_nav, profile_nav;
    NavigationView navView_profile, navView_menu;
    SwipeRefreshLayout swipeRefresh;
    Query qSearch;

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
        homeSearch = findViewById(R.id.homeSearch);

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

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        homeRecycler.setLayoutManager(mLayoutManager);

        EventChangeListener();

        //swipe up to refresh.. not really needed firebase is already in real time
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                homeAdapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }
        });
        //not really needed

    }

    private void EventChangeListener() {
        fStore.collection("Post").orderBy("homePostDate").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                DocumentSnapshot.ServerTimestampBehavior behavior = ESTIMATE;
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
        intent.putExtra("docId", homeModelsArrayList.get(position).getDocId());
        intent.putExtra("homeAuthorUid", homeModelsArrayList.get(position).getHomeAuthorUid());
//        intent.putExtra("homePostDate", homeModelsArrayList.get(position).getHomePostDate());

        startActivity(intent);
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_action);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                homeAdapter.getFilter().filter(newText);

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}