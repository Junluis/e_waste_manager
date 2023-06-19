package com.capstone.e_waste_manager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RewardReceipt extends AppCompatActivity {

    LottieAnimationView confettianim;
    TextView title, rewardpoints, userpoints, username;

    //firebase
    FirebaseFirestore fStore;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseUser user;

    Double points;

    RewardsModel model;

    MaterialButton done;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_receipt);

        //model
        model = (RewardsModel) getIntent().getSerializableExtra("model");

        //firebase
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        confettianim = findViewById(R.id.confettianim);
        title = findViewById(R.id.title);
        rewardpoints = findViewById(R.id.rewardpoints);
        userpoints = findViewById(R.id.userpoints);
        username = findViewById(R.id.username);

        done = findViewById(R.id.done);

        confettianim.animate()
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        confettianim.setVisibility(View.GONE);
                    }
                });

        title.setText(model.title);
        rewardpoints.setText(model.points + " pts");

        DocumentReference documentReference = fStore.collection("Users").document(user.getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                assert documentSnapShot != null;

                points = documentSnapShot.getDouble("EMPoints");

                if (points != null) {
                    if (points > 1){
                        userpoints.setText(points.longValue() +" pts");
                    }else{
                        userpoints.setText(points.longValue() +" pt");
                    }
                }else{
                    userpoints.setText("0 pt");
                }

                username.setText(documentSnapShot.getString("Username"));
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}