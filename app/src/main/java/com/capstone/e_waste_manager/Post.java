package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Post extends AppCompatActivity {

    ImageView morebtn, linkbtn, postbodybtn, galleryuploadbtn, uploadimgbtn;
    ImageButton closepg;
    Button postButton;
    EditText postTitle, postBody, postlink;
    BottomSheetBehavior bottomSheetBehavior;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        closepg = findViewById(R.id.closepg);
        postButton = findViewById(R.id.postButton);
        postTitle = findViewById(R.id.postTitle);
        postBody = findViewById(R.id.postBody);
        postlink = findViewById(R.id.postlink);

        //footer buttons
        morebtn = findViewById(R.id.morebtn);
        linkbtn = findViewById(R.id.linkbtn);
        postbodybtn = findViewById(R.id.postbodybtn);
        uploadimgbtn = findViewById(R.id.uploadimgbtn);
        galleryuploadbtn = findViewById(R.id.galleryuploadbtn);


        //post buttons

        LinearLayout bottomSheetLayout = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        LinearLayout uploadimgbtn2 = bottomSheetLayout.findViewById(R.id.uploadimgbtn);
        LinearLayout postbodybtn2 = bottomSheetLayout.findViewById(R.id.postbodybtn);
        LinearLayout linkbtn2 = bottomSheetLayout.findViewById(R.id.linkbtn);

        //bottom sheet
        morebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        uploadimgbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadimgbtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.green1));
                postbodybtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.darkgray));
                linkbtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.darkgray));
                galleryuploadbtn.setVisibility(View.VISIBLE);
                postlink.setVisibility(View.GONE);
                postBody.requestFocus();
            }
        });
        postbodybtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadimgbtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.darkgray));
                postbodybtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.green1));
                linkbtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.darkgray));
                galleryuploadbtn.setVisibility(View.GONE);
                postlink.setVisibility(View.GONE);
                postBody.requestFocus();
            }
        });
        linkbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadimgbtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.darkgray));
                postbodybtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.darkgray));
                linkbtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.green1));
                galleryuploadbtn.setVisibility(View.GONE);
                postlink.setVisibility(View.VISIBLE);
                postlink.requestFocus();
            }
        });

        postTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });
        postBody.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });
        postlink.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });
        postTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        postBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        postlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        closepg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        uploadimgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadimgbtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.green1));
                postbodybtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.darkgray));
                linkbtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.darkgray));
                galleryuploadbtn.setVisibility(View.VISIBLE);
                postlink.setVisibility(View.GONE);
                postBody.requestFocus();
            }
        });
        postbodybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadimgbtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.darkgray));
                postbodybtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.green1));
                linkbtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.darkgray));
                galleryuploadbtn.setVisibility(View.GONE);
                postlink.setVisibility(View.GONE);
                postBody.requestFocus();
            }
        });
        linkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadimgbtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.darkgray));
                postbodybtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.darkgray));
                linkbtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.green1));
                galleryuploadbtn.setVisibility(View.GONE);
                postlink.setVisibility(View.VISIBLE);
                postlink.requestFocus();
            }
        });
        String name = user.getDisplayName();

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fAuth.getCurrentUser()!=null){

                    uploadData();
                    Toast.makeText(Post.this, "Logged in "+name, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Post.this, "Not Logged in", Toast.LENGTH_SHORT).show();
                }
                finish();
            }

            private void uploadData() {

                fStore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null){
                                String username = task.getResult().getString("Username");
                                Map<String, Object> doc = new HashMap<>();
                                doc.put("homeTitle", postTitle.getText().toString().trim());
                                doc.put("homeBody", postBody.getText().toString().trim());
                                doc.put("homeAuthor", username);

                                fStore.collection("Post").add(doc).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        postBody.setText("");
                                        postTitle.setText("");
                                        Toast.makeText(Post.this, "Post Successful", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                Toast.makeText(Post.this, "Error Getting User Data", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
            }
        });
    }

    private void showDialog(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        LinearLayout bottomSheetLayout = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);

        new CountDownTimer(500, 1000) {

            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }.start();

    }





}