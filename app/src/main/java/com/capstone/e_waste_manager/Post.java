package com.capstone.e_waste_manager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Post extends AppCompatActivity {

    ImageView morebtn, linkbtn, postbodybtn, galleryuploadbtn, uploadimgbtn;
    ImageButton closepg;
    Button postButton;
    TextInputEditText postTitle, postBody, postlink;
    BottomSheetBehavior bottomSheetBehavior;
    ActivityResultLauncher<String> galleryOpen;

    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    String userID;

    Uri profileImageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

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

        //upload photo
        galleryOpen = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri imageUri) {
                if (imageUri != null && !imageUri.equals(Uri.EMPTY)){
                    profileImageUri = imageUri;
                    galleryuploadbtn.setImageURI(imageUri);
                }
            }
        });
        galleryuploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open gallery
                galleryOpen.launch("image/");

            }
        });

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

        postTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    postTitle.setError("required*");
                }else{
                    postTitle.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        postlink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(!Patterns.WEB_URL.matcher(s).matches()){
                    postlink.setError("Invalid link.");
                }else{
                    postlink.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("https:/")){
                    postlink.setText("");
                    postlink.setSelection(postlink.length());
                } else if (!s.toString().contains("https://") && s.length()!=0) {
                    postlink.setText("https://" + s);
                    postlink.setSelection(postlink.length());
                }
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(postTitle.getText().toString().length() == 0 || !TextUtils.isEmpty(postTitle.getError())){
                    if(postTitle.getText().toString().length() == 0)
                        postTitle.setText("");
                    postTitle.requestFocus();
                } else if(postlink.getText().toString().length() == 0 || !TextUtils.isEmpty(postlink.getError())){
                    if(postlink.getText().toString().length() == 0)
                        postlink.setText("");
                    postlink.requestFocus();
                }else{
                    uploadData();
                }
            }
        });
    }

    private void uploadData() {
        fStore.collection("Users").document(userID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null){
                String username = task.getResult().getString("Username");
                Map<String, Object> doc = new HashMap<>();
                doc.put("homeTitle", postTitle.getText().toString().trim());
                doc.put("homeBody", postBody.getText().toString().trim());
                doc.put("homeAuthor", username);
                doc.put("homeAuthorUid", fAuth.getCurrentUser().getUid());
                doc.put("homePostDate", FieldValue.serverTimestamp());
                if(postlink.getVisibility() == View.VISIBLE){
                    if (postlink.getText().length()!= 0 ){
                        doc.put("url", postlink.getText().toString().trim());
                    }
                }
                if(galleryuploadbtn.getVisibility() == View.VISIBLE){
                    if (profileImageUri != null && !profileImageUri.equals(Uri.EMPTY)){
                        doc.put("hasImage", true);
                    }else{
                        doc.put("hasImage", false);
                    }
                }else {
                    doc.put("hasImage", false);
                }

                fStore.collection("Post").add(doc).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        postBody.setText("");
                        postTitle.setText("");
                        if (profileImageUri != null && !profileImageUri.equals(Uri.EMPTY)){
                            uploadImageToFirebase(profileImageUri, task.getResult().getId());
                        }
                        Toast.makeText(Post.this, "Post Successful", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Post.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
            }else{
                Toast.makeText(Post.this, "Error Getting User Data", Toast.LENGTH_SHORT).show();
                finish();
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

    private void uploadImageToFirebase(Uri imageUri, String docid){

        //upload Image to Firebase storage
        StorageReference fileRef = storageReference.child("ForumPost/"+docid+"/postimg.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(galleryuploadbtn);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Post.this, "Failed to upload picture.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}