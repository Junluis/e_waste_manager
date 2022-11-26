package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.e_waste_manager.adapter.ProfileAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UserProfilePage extends AppCompatActivity {
    ImageButton closeup;
    Button editProfilebtn;
    TabLayout profiletabLayout;
    ViewPager2 profilePager;
    ProfileAdapter adapter;
    TextView prof_username, prof_email, prof_bio;
    ImageView partnerBadge, prof_img;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);

        //transparent status
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        //update profile card
        prof_username = findViewById(R.id.prof_username);
        prof_email = findViewById(R.id.prof_email);
        partnerBadge = findViewById(R.id.partnerBadge);
        prof_img = findViewById(R.id.prof_img);
        prof_bio = findViewById(R.id.prof_bio);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        closeup = findViewById(R.id.closeup);
        editProfilebtn = findViewById(R.id.editProfilebtn);

        profiletabLayout = findViewById(R.id.profiletabLayout);
        profilePager = findViewById(R.id.profilePager);

        userID = fAuth.getCurrentUser().getUid();

        //transparent inset
        ViewCompat.setOnApplyWindowInsetsListener(profilePager, (v, windowInsets) -> {
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

        //get profile info
        DocumentReference documentReference = fStore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                prof_username.setText(documentSnapShot.getString("Username"));
                prof_email.setText(documentSnapShot.getString("Email"));
                prof_bio.setText(documentSnapShot.getString("Bio"));

                if(prof_bio.getText().toString().equals("")){
                    prof_bio.setVisibility(View.GONE);
                }

                if(Objects.equals(documentSnapShot.getString("Partner"), "1")){
                    partnerBadge.setVisibility(View.VISIBLE);
                } else{
                    partnerBadge.setVisibility(View.GONE);
                }
            }
        });
        updateProf();

        closeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        editProfilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), EditProfile.class);
                startActivity(i);
            }
        });

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

    public void updateProf(){
        //get profile pic
        StorageReference profileRef = storageReference.child("ProfileImage/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(prof_img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}