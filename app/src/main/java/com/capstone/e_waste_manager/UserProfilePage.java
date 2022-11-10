package com.capstone.e_waste_manager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.e_waste_manager.adapter.ProfileAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class UserProfilePage extends AppCompatActivity {
    ImageButton closeup;
    Button editProfilebtn;
    TabLayout profiletabLayout;
    ViewPager2 profilePager;
    ProfileAdapter adapter;
    TextView prof_username, prof_email, prof_bio;
    ImageView partnerBadge;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);

        //update profile card
        prof_username = findViewById(R.id.prof_username);
        prof_email = findViewById(R.id.prof_email);
        partnerBadge = findViewById(R.id.partnerBadge);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                prof_username.setText(documentSnapShot.getString("Username"));
                prof_email.setText(documentSnapShot.getString("Email"));

                if(Objects.equals(documentSnapShot.getString("Partner"), "0")){
                    partnerBadge.setVisibility(View.GONE);
                }
            }
        });

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