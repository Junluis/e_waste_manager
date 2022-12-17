package com.capstone.e_waste_manager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LearnPost extends AppCompatActivity {

    ImageView morebtn, linkbtn, postbodybtn, galleryuploadbtn, uploadimgbtn;
    ImageButton closepg;
    Button LearnPostButton, UploadPDF;
    TextInputEditText LearnPostTitle, LearnSubTitle, LearnPostBody, postlink;

    BottomSheetBehavior bottomSheetBehavior;
    ActivityResultLauncher<String> galleryOpen;
    ActivityResultLauncher<String> PDFOpen;

    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    String userID;

    Uri profileImageUri, postPDFUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_post);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        closepg = findViewById(R.id.closepg);
        LearnPostButton = findViewById(R.id.LearnPostButton);
        LearnPostTitle = findViewById(R.id.LearnPostTitle);
        LearnSubTitle = findViewById(R.id.LearnSubTitle);
        LearnPostBody = findViewById(R.id.LearnPostBody);
        postlink = findViewById(R.id.postlink);
        UploadPDF = findViewById(R.id.UploadPDF);

        //footer buttons
        morebtn = findViewById(R.id.morebtn);
        linkbtn = findViewById(R.id.linkbtn);
        postbodybtn = findViewById(R.id.postbodybtn);
        uploadimgbtn = findViewById(R.id.uploadimgbtn);
        galleryuploadbtn = findViewById(R.id.LearnPostCover);

        //post buttons
        LinearLayout bottomSheetLayout = findViewById(R.id.bottom_sheet2);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        LinearLayout uploadimgbtn2 = bottomSheetLayout.findViewById(R.id.uploadimgbtn);
        LinearLayout postbodybtn2 = bottomSheetLayout.findViewById(R.id.postbodybtn);
        LinearLayout linkbtn2 = bottomSheetLayout.findViewById(R.id.linkbtn);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        //upload photo
        galleryOpen = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri imageUri) {
                if (imageUri != null && !imageUri.equals(Uri.EMPTY)){
                    profileImageUri = imageUri;
                    galleryuploadbtn.setImageURI(imageUri);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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

        PDFOpen = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri pdfUri) {
                if (pdfUri != null && !pdfUri.equals(Uri.EMPTY)){
                    postPDFUri = pdfUri;
                    @SuppressLint("Recycle")
                    Cursor returnCursor = getContentResolver().query(postPDFUri, null, null, null, null);
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    returnCursor.moveToFirst();
                    String file_name = returnCursor.getString(nameIndex);
                    UploadPDF.setText(file_name);
                    UploadPDF.setBackgroundTintList(AppCompatResources.getColorStateList(LearnPost.this, R.color.green1));
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });
        UploadPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PDFOpen.launch("application/pdf");
            }
        });

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
                postlink.setVisibility(View.GONE);
                UploadPDF.setVisibility(View.VISIBLE);
                LearnPostBody.setHint("Description");
                LearnPostBody.requestFocus();
            }
        });
        postbodybtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadimgbtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.darkgray));
                postbodybtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.green1));
                linkbtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.darkgray));
                UploadPDF.setVisibility(View.GONE);
                postlink.setVisibility(View.GONE);
                LearnPostBody.setHint("Write body");
                LearnPostBody.requestFocus();
            }
        });
        linkbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadimgbtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.darkgray));
                postbodybtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.darkgray));
                linkbtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.green1));
                UploadPDF.setVisibility(View.GONE);
                postlink.setVisibility(View.VISIBLE);
                LearnPostBody.setHint("Description");
                postlink.requestFocus();
            }
        });

        LearnPostTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });
        LearnPostBody.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
        LearnSubTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });
        LearnPostTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        LearnPostBody.setOnClickListener(new View.OnClickListener() {
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
        LearnSubTitle.setOnClickListener(new View.OnClickListener() {
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
                UploadPDF.setVisibility(View.VISIBLE);
                postlink.setVisibility(View.GONE);
                LearnPostBody.setHint("Description");
                LearnPostBody.requestFocus();
            }
        });
        postbodybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadimgbtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.darkgray));
                postbodybtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.green1));
                linkbtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.darkgray));
                UploadPDF.setVisibility(View.GONE);
                postlink.setVisibility(View.GONE);
                LearnPostBody.setHint("Write body");
                LearnPostBody.requestFocus();
            }
        });
        linkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadimgbtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.darkgray));
                postbodybtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.darkgray));
                linkbtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.green1));
                UploadPDF.setVisibility(View.GONE);
                postlink.setVisibility(View.VISIBLE);
                LearnPostBody.setHint("Description");
                postlink.requestFocus();
            }
        });

        LearnPostTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    LearnPostTitle.setError("required*");
                }else{
                    LearnPostTitle.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        LearnPostBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    LearnPostBody.setError("required*");
                }else{
                    LearnPostBody.setError(null);
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

        LearnPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LearnPostTitle.getText().toString().length() == 0 || !TextUtils.isEmpty(LearnPostTitle.getError())){
                    if(LearnPostTitle.getText().toString().length() == 0)
                        LearnPostTitle.setText("");
                    LearnPostTitle.requestFocus();
                } else if(profileImageUri == null || profileImageUri.equals(Uri.EMPTY)){
                    Toast.makeText(LearnPost.this, "Please upload Cover image", Toast.LENGTH_SHORT).show();
                } else if((postPDFUri == null && postPDFUri.equals(Uri.EMPTY)) && UploadPDF.getVisibility() == View.VISIBLE){
                    Toast.makeText(LearnPost.this, "Please upload PDF file", Toast.LENGTH_SHORT).show();
                } else if((postlink.getText().toString().length() == 0 || !TextUtils.isEmpty(postlink.getError())) && postlink.getVisibility() == View.VISIBLE){
                    if(postlink.getText().toString().length() == 0)
                        postlink.setText("");
                    postlink.requestFocus();
                } else if(LearnPostBody.getText().toString().length() == 0 || !TextUtils.isEmpty(LearnPostBody.getError())){
                    if(LearnPostBody.getText().toString().length() == 0)
                        LearnPostBody.setText("");
                    LearnPostBody.requestFocus();
                } else{
                    uploadData();
                }
            }
        });

    }

    private void uploadData() {
        fStore.collection("Users").document(userID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null){
                String id = task.getResult().getString("Partner");
                if(Objects.equals(id, "1")) {
                    String username = task.getResult().getString("Username");
                    Map<String, Object> doc = new HashMap<>();
                    doc.put("learnTitle", LearnPostTitle.getText().toString().trim());
                    doc.put("learnSubTitle", LearnSubTitle.getText().toString().trim());
                    doc.put("learnBody", LearnPostBody.getText().toString().trim());
                    doc.put("learnAuthor", userID);

                    if (postlink.getVisibility() == View.VISIBLE) {
                        if (postlink.getText().length() != 0) {
                            doc.put("url", postlink.getText().toString().trim());
                        }
                    }

                    if (UploadPDF.getVisibility() == View.VISIBLE) {
                        if (postPDFUri != null && !postPDFUri.equals(Uri.EMPTY)) {
                            doc.put("filepdf", UploadPDF.getText().toString());
                        }
                    }

                    fStore.collection("LearningMaterial").add(doc).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (profileImageUri != null && !profileImageUri.equals(Uri.EMPTY)) {
                                uploadImageToFirebase(profileImageUri, task.getResult().getId());
                            }
                            if (postPDFUri != null && !postPDFUri.equals(Uri.EMPTY)) {
                                uploadPDFToFirebase(postPDFUri, task.getResult().getId(), UploadPDF.getText().toString());
                            }
                            Toast.makeText(LearnPost.this, "Post Successful", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LearnPost.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    });
                }
            }else{
                Toast.makeText(LearnPost.this, "Error Getting User Data", Toast.LENGTH_SHORT).show();
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

        LinearLayout bottomSheetLayout = findViewById(R.id.bottom_sheet2);
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
        StorageReference fileRef = storageReference.child("LearningMaterial/"+docid+"/coverimg.jpg");
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
                Toast.makeText(LearnPost.this, "Failed to upload picture.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadPDFToFirebase(Uri pdfUri, String docid, String pdftitle){

        //upload Image to Firebase storage
        StorageReference fileRef = storageReference.child("LearningMaterial/"+docid+"/"+pdftitle);
        fileRef.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LearnPost.this, "Failed to upload PDF.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}