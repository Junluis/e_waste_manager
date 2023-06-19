package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RewardsCatalog extends AppCompatActivity {

    //firebase
    FirebaseFirestore fStore;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseUser user;

    TextView rewarddetails, title, username, email, rewardpoints, userpoints;
    TextInputLayout tilError;
    EditText regError;

    Button redeem;
    ImageButton closedr;

    Double points;

    RewardsModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards_catalog);

        //firebase
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        title = findViewById(R.id.title);
        rewarddetails = findViewById(R.id.rewarddetails);
        rewardpoints = findViewById(R.id.rewardpoints);

        userpoints = findViewById(R.id.userpoints);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);

        tilError = findViewById(R.id.tilError);
        regError = findViewById(R.id.regError);

        closedr = findViewById(R.id.closedr);
        redeem = findViewById(R.id.redeem);

        closedr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //model
        model = (RewardsModel) getIntent().getSerializableExtra("model");

        title.setText(model.title);
        rewarddetails.setText(model.details);
        rewardpoints.setText(model.points+"pts");

        DocumentReference documentReference = fStore.collection("Users").document(user.getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                assert documentSnapShot != null;
                points = documentSnapShot.getDouble("EMPoints");


                if (points != null) {
                    if (points > 1){
                        userpoints.setText(points.longValue() +"pts");
                    }else{
                        userpoints.setText(points.longValue() +"pt");
                    }
                }else{
                    userpoints.setText("0pt");
                }

                username.setText(documentSnapShot.getString("Username"));
                email.setText(documentSnapShot.getString("Email"));

                if(points < model.points){
                    tilError.setError("Insufficient points");
                    tilError.setErrorIconDrawable(0);
                    redeem.setEnabled(false);
                } else{
                    tilError.setError(null);
                    redeem.setEnabled(true);
                }
            }
        });

        redeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(points >= model.points){
                    uploadData(model.docId, model.points, model.prefix);
                }else{
                    Toast.makeText(RewardsCatalog.this, "Insufficient points.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private static final String ALLOWED_CHARACTERS ="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private void uploadData(String docid, Integer pointval, String prefix) {

        String code = prefix+getRandomString(6);

        Query totalcount = fStore.collection("Vouchers").whereEqualTo("rewardId", docid);
        AggregateQuery counttotal = totalcount.count();
        counttotal.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();

                    if (snapshot.getCount() < 1947792){
                        Query query = fStore.collection("Vouchers").whereEqualTo("code", code);
                        AggregateQuery countQuery = query.count();
                        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    // Count fetched successfully
                                    AggregateQuerySnapshot snapshot = task.getResult();

                                    if(snapshot.getCount() == 0){
                                        Map<String, Object> doc = new HashMap<>();
                                        doc.put("rewardUid", user.getUid());
                                        doc.put("rewardId", docid);
                                        doc.put("code", code);
                                        doc.put("revealed", false);
                                        doc.put("date", FieldValue.serverTimestamp());


                                        fStore.collection("Vouchers").add(doc).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                DocumentReference status = fStore.collection("Users").document(user.getUid());
                                                Map<String, Object> edited = new HashMap<>();

                                                edited.put("EMPoints", FieldValue.increment(-pointval));

                                                status.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(RewardsCatalog.this, "You've Successfully Redeemed Your Reward.", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(RewardsCatalog.this, RewardReceipt.class);
                                                        intent.putExtra("model", model);
                                                        RewardsCatalog.this.startActivity(intent);
                                                        finish();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(RewardsCatalog.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(RewardsCatalog.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                                                onBackPressed();
                                            }
                                        });
                                    } else{
                                        uploadData(docid, pointval, prefix);
                                    }
                                }else{
                                    Toast.makeText(RewardsCatalog.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else {
                        Toast.makeText(RewardsCatalog.this, "Sorry, This Voucher is Currently Unavailable. Please try again later.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RewardsCatalog.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
}