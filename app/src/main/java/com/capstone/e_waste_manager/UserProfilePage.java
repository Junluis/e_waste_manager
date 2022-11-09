package com.capstone.e_waste_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class UserProfilePage extends AppCompatActivity {
    ImageButton closeup;
    Button editProfilebtn;
    TabLayout profiletabLayout;
    ViewPager2 profilePager;
    ProfileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);


        closeup = findViewById(R.id.closeup);
        editProfilebtn = findViewById(R.id.editProfilebtn);

        closeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        profiletabLayout = findViewById(R.id.profiletabLayout);
        profilePager = findViewById(R.id.profilePager);

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new ProfileAdapter(fragmentManager, getLifecycle());
        profilePager.setAdapter(adapter);
        profiletabLayout.addTab(profiletabLayout.newTab().setText("Posts"));
        profiletabLayout.addTab(profiletabLayout.newTab().setText("Comments"));
        profiletabLayout.addTab(profiletabLayout.newTab().setText("Account Details"));

        profiletabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                profilePager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        profilePager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                profiletabLayout.selectTab(profiletabLayout.getTabAt(position));
            }
        });

    }
}