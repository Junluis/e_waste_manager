package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.google.android.material.navigation.NavigationView;

public class Home extends AppCompatActivity {

    DrawerLayout drawerLayout;

    RecyclerView homeRecycler;
    ImageButton homeBtnHome, homeBtnPost, homeBtnLearn;
    
    ImageView menu_nav, profile_nav;
    NavigationView navView_profile, navView_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //drawer start
        drawerLayout = findViewById(R.id.drawerLayout);
        navView_profile = findViewById(R.id.nav_viewright);
        navView_menu = findViewById(R.id.nav_viewleft);

        menu_nav = findViewById(R.id.menu_nav);
        profile_nav = findViewById(R.id.profile_nav);

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

        homeBtnHome.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Home.class)));

        homeBtnPost.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Post.class)));

        homeBtnLearn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Learn.class)));
    }
}