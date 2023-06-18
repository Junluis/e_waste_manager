package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.e_waste_manager.adapter.DonationAdapter;
import com.capstone.e_waste_manager.adapter.RewardAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Rewards extends AppCompatActivity {

    ImageView closedr;
    TextView empoints;

    TabLayout rewardsTabLayout;
    ViewPager2 rewardPager;
    RewardAdapter adapter;

    //firebase
    FirebaseFirestore fStore;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        //firebase
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        closedr = findViewById(R.id.closedr);
        empoints = findViewById(R.id.empoints);

        rewardsTabLayout = findViewById(R.id.rewardsTabLayout);
        rewardPager = findViewById(R.id.rewardPager);

        closedr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        DocumentReference documentReference = fStore.collection("Users").document(user.getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                Double points = documentSnapShot.getDouble("EMPoints");

                if (points != null) {
                    empoints.setText(points.longValue()+"");
                }else{
                    empoints.setText("0");
                }
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new RewardAdapter(fragmentManager, getLifecycle());
        rewardPager.setAdapter(adapter);
        rewardsTabLayout.addTab(rewardsTabLayout.newTab().setText("Available Rewards"));
        rewardsTabLayout.addTab(rewardsTabLayout.newTab().setText("My Rewards"));

        rewardsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                rewardPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        rewardPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                rewardsTabLayout.selectTab(rewardsTabLayout.getTabAt(position));
            }
        });
    }
}