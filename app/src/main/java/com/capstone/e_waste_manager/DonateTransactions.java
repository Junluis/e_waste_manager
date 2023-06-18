package com.capstone.e_waste_manager;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import com.capstone.e_waste_manager.adapter.DonationAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DonateTransactions extends AppCompatActivity {

    TabLayout donationtabLayout;
    ViewPager2 donationPager;
    DonationAdapter adapter;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    String userID;

    ImageButton closedr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_transactions);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        donationtabLayout = findViewById(R.id.donationtabLayout);
        donationPager = findViewById(R.id.donationPager);

        closedr = findViewById(R.id.closedr);

        userID = fAuth.getCurrentUser().getUid();

        closedr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new DonationAdapter(fragmentManager, getLifecycle());
        donationPager.setAdapter(adapter);
        donationtabLayout.addTab(donationtabLayout.newTab().setText("All Donations"));
        donationtabLayout.addTab(donationtabLayout.newTab().setText("in Progress Donations"));
        donationtabLayout.addTab(donationtabLayout.newTab().setText("Finished Donations"));

        donationtabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                donationPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        donationPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                donationtabLayout.selectTab(donationtabLayout.getTabAt(position));
            }
        });

    }

}