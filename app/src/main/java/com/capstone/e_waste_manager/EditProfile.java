package com.capstone.e_waste_manager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProfile extends AppCompatActivity {

    ImageButton closeep;
    MaterialButton saveButton;
    CardView updateIcon;
    ImageView prof_img;
    ActivityResultLauncher<String> galleryOpen;

    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    String userID;

    Uri profileImageUri;

    TextInputLayout tilUsername, tilEmail, tilBio, tilFirstName, tilLastName, tilDateOfBirth, tilAddressHouse, tilBarangay;
    EditText regUsername, regEmail, regBio, regFirstName, regLastName, regDateOfBirth, regAddressHouse;


    AutoCompleteTextView regBarangay;
    ArrayAdapter<String> barangayList;


    String Dateval;
    Integer ageInteger = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        prof_img = findViewById(R.id.prof_img);
        updateIcon = findViewById(R.id.updateIcon);
        saveButton = findViewById(R.id.saveButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();


        //user informations
        regUsername = findViewById(R.id.regUsername);
        regEmail = findViewById(R.id.regEmail);
        regBio = findViewById(R.id.regBio);
        regFirstName = findViewById(R.id.regFirstName);
        regLastName = findViewById(R.id.regLastName);
        regDateOfBirth = findViewById(R.id.regDateOfBirth);
        regAddressHouse = findViewById(R.id.regAddressHouse);
        regBarangay = findViewById(R.id.regBarangay);


        tilUsername = findViewById(R.id.tilUsername);
        tilEmail = findViewById(R.id.tilEmail);
        tilBio = findViewById(R.id.tilBio);
        tilFirstName = findViewById(R.id.tilFirstName);
        tilLastName = findViewById(R.id.tilLastName);
        tilDateOfBirth = findViewById(R.id.tilDateOfBirth);
        tilAddressHouse = findViewById(R.id.tilAddressHouse);
        tilBarangay = findViewById(R.id.tilBarangay);


        String[] barangayarray = getResources().getStringArray(R.array.barangay_list);

        barangayList = new ArrayAdapter<String>(this, R.layout.dropdown_barangay, barangayarray);

        regBarangay.setAdapter(barangayList);


        DocumentReference documentReference = fStore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                regUsername.setText(documentSnapShot.getString("Username"));
                regEmail.setText(documentSnapShot.getString("Email"));
                regFirstName.setText(documentSnapShot.getString("FirstName"));
                regLastName.setText(documentSnapShot.getString("LastName"));
                regDateOfBirth.setText(documentSnapShot.getString("DateOfBirth"));

                regBio.setText(documentSnapShot.getString("Bio"));
                regAddressHouse.setText(documentSnapShot.getString("HouseAddress"));
                regBarangay.setText(documentSnapShot.getString("Barangay"));

            }
        });

        StorageReference profileRef = storageReference.child("ProfileImage/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(prof_img);
            }
        });

                //close app
        closeep = findViewById(R.id.closeep);
        closeep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        //update profile icon
        galleryOpen = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri imageUri) {
                if (imageUri != null && !imageUri.equals(Uri.EMPTY)){
                    profileImageUri = imageUri;
                    prof_img.setImageURI(imageUri);
                }
            }
        });
        updateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open gallery
                galleryOpen.launch("image/");

            }
        });

        //date picker start

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        tilDateOfBirth.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EditProfile.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;
                        int daylength = (int)(Math.log10(day)+1);
                        int monthlength = (int)(Math.log10(month)+1);
                        if (daylength < 2){
                            @SuppressLint("DefaultLocale") String daychange = String.format("%02d", day);
                            if(monthlength < 2){
                                @SuppressLint("DefaultLocale") String monthchange = String.format("%02d", month);
                                String date = daychange+"/"+monthchange+"/"+year;
                                regDateOfBirth.setText(date);
                            }else{
                                String date = daychange+"/"+month+"/"+year;
                                regDateOfBirth.setText(date);
                            }

                        }else if(monthlength < 2){
                            @SuppressLint("DefaultLocale") String monthchange = String.format("%02d", month);
                            String date = day+"/"+monthchange+"/"+year;
                            regDateOfBirth.setText(date);
                        }else{
                            String date = day+"/"+month+"/"+year;
                            regDateOfBirth.setText(date);
                        }
                    }
                }, year,month,day);
                datePickerDialog.show();
                regDateOfBirth.requestFocus();
            }
        });
        //date picker end

        //date mask start
        new DateInputMask(regDateOfBirth);
        //date mask end

        //checkField error start
        regUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilUsername.getChildCount() == 2)
                        tilUsername.getChildAt(1).setVisibility(View.VISIBLE);
                    tilUsername.setError("required*");
                }else if(s.length() < 6 || !s.toString().matches("[a-zA-Z0-9._-]+")){
                    if (tilUsername.getChildCount() == 2)
                        tilUsername.getChildAt(1).setVisibility(View.VISIBLE);
                    tilUsername.setError("Must be at least 6 characters and cannot contain spaces and special characters other than \".\" \"-\" \"_\".");
                }else{
                    tilUsername.setError(null);
                    if (tilUsername.getChildCount() == 2)
                        tilUsername.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        regEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilEmail.getChildCount() == 2)
                        tilEmail.getChildAt(1).setVisibility(View.VISIBLE);
                    tilEmail.setError("required*");
                }else if(!Patterns.EMAIL_ADDRESS.matcher(s).matches()){
                    if (tilEmail.getChildCount() == 2)
                        tilEmail.getChildAt(1).setVisibility(View.VISIBLE);
                    tilEmail.setError("Invalid email.");
                }else{
                    tilEmail.setError(null);
                    if (tilEmail.getChildCount() == 2)
                        tilEmail.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        regDateOfBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean hasfocus) {
                if (hasfocus) {
                    regDateOfBirth.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                        }
                        @Override
                        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                            Dateval = s.toString().replaceAll("[^\\d./]", "");
                            if(Dateval.length() < 10){
                                tilDateOfBirth.setError("required*");
                                tilDateOfBirth.setErrorIconDrawable(0);
                            }else if(Dateval.length() > 10){
                                Dateval = Dateval.substring(0, 10);
                            }else{
                                Calendar today = Calendar.getInstance();
                                int cYear = Integer.parseInt(regDateOfBirth.getText().toString().substring(regDateOfBirth.length() - 4, regDateOfBirth.length()));
                                int cMonth = Integer.parseInt(regDateOfBirth.getText().toString().substring(3, 5));
                                int cday = Integer.parseInt(regDateOfBirth.getText().toString().substring(0, 2));

                                if (cYear == today.get(Calendar.YEAR)) {
                                    if (today.get(Calendar.MONTH) + 1 == cMonth) {
                                        if (today.get(Calendar.DAY_OF_MONTH) < cday) {
                                            tilDateOfBirth.setError("Are you a time traveler?");
                                            tilDateOfBirth.setErrorIconDrawable(0);
                                        }else{
                                            tilDateOfBirth.setError("You must be over 13 to register.");
                                            tilDateOfBirth.setErrorIconDrawable(0);
                                        }
                                    }else if(today.get(Calendar.MONTH) + 1 < cMonth){
                                        tilDateOfBirth.setError("Are you a time traveler?");
                                        tilDateOfBirth.setErrorIconDrawable(0);
                                    }else{
                                        tilDateOfBirth.setError("You must be over 13 to register.");
                                        tilDateOfBirth.setErrorIconDrawable(0);
                                    }
                                }else if (cYear > today.get(Calendar.YEAR)) {
                                    tilDateOfBirth.setError("Are you a time traveler?");
                                    tilDateOfBirth.setErrorIconDrawable(0);
                                }else if(cYear < today.get(Calendar.YEAR)){
                                    if ((today.get(Calendar.YEAR) - cYear) == 13 ) {
                                        if (today.get(Calendar.MONTH) + 1 == cMonth) {
                                            if (today.get(Calendar.DAY_OF_MONTH) < cday) {
                                                tilDateOfBirth.setError("You must be over 13 to register.");
                                                tilDateOfBirth.setErrorIconDrawable(0);
                                            }else{
                                                ageInteger = today.get(Calendar.YEAR) - cYear;
                                                if (today.get(Calendar.MONTH) + 1 == cMonth) {
                                                    if (today.get(Calendar.DAY_OF_MONTH) < cday) {
                                                        ageInteger = ageInteger - 1;
                                                    }
                                                } else if (today.get(Calendar.MONTH) < cMonth) {
                                                    ageInteger = ageInteger - 1;
                                                }
                                                tilDateOfBirth.setError(null);
                                            }
                                        }else if (today.get(Calendar.MONTH) + 1 < cMonth) {
                                            tilDateOfBirth.setError("You must be over 13 to register.");
                                            tilDateOfBirth.setErrorIconDrawable(0);
                                        }else{
                                            ageInteger = today.get(Calendar.YEAR) - cYear;
                                            if (today.get(Calendar.MONTH) + 1 == cMonth) {
                                                if (today.get(Calendar.DAY_OF_MONTH) < cday) {
                                                    ageInteger = ageInteger - 1;
                                                }
                                            } else if (today.get(Calendar.MONTH) < cMonth) {
                                                ageInteger = ageInteger - 1;
                                            }

                                            tilDateOfBirth.setError(null);
                                        }
                                    }else if ((today.get(Calendar.YEAR) - cYear) < 13 ){
                                        tilDateOfBirth.setError("You must be over 13 to register.");
                                        tilDateOfBirth.setErrorIconDrawable(0);
                                    }else{
                                        ageInteger = today.get(Calendar.YEAR) - cYear;
                                        if (today.get(Calendar.MONTH) + 1 == cMonth) {
                                            if (today.get(Calendar.DAY_OF_MONTH) < cday) {
                                                ageInteger = ageInteger - 1;
                                            }
                                        } else if (today.get(Calendar.MONTH) < cMonth) {
                                            ageInteger = ageInteger - 1;
                                        }
                                        tilDateOfBirth.setError(null);
                                    }

                                } else {
                                    ageInteger = today.get(Calendar.YEAR) - cYear;
                                    if (today.get(Calendar.MONTH) + 1 == cMonth) {
                                        if (today.get(Calendar.DAY_OF_MONTH) < cday) {
                                            ageInteger = ageInteger - 1;
                                        }
                                    } else if (today.get(Calendar.MONTH) < cMonth) {
                                        ageInteger = ageInteger - 1;
                                    }
                                    tilDateOfBirth.setError(null);
                                }
                            }
                        }
                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });
                } else {
                    regDateOfBirth.setText(Dateval);
                    if (ageInteger < 13) {
                        tilDateOfBirth.setError("You must be over 13 to register.");
                        tilDateOfBirth.setErrorIconDrawable(0);
                    }
                }
            }
        });
        regFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilFirstName.getChildCount() == 2)
                        tilFirstName.getChildAt(1).setVisibility(View.VISIBLE);
                    tilFirstName.setError("required*");
                }else if(!s.toString().matches("[a-zA-Z ]+")){
                    if (tilFirstName.getChildCount() == 2)
                        tilFirstName.getChildAt(1).setVisibility(View.VISIBLE);
                    tilFirstName.setError("Special characters and numeric values are not allowed.");
                }else{
                    tilFirstName.setError(null);
                    if (tilFirstName.getChildCount() == 2)
                        tilFirstName.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        regLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilLastName.getChildCount() == 2)
                        tilLastName.getChildAt(1).setVisibility(View.VISIBLE);
                    tilLastName.setError("required*");
                }else if(!s.toString().matches("[a-zA-Z ]+")){
                    if (tilLastName.getChildCount() == 2)
                        tilLastName.getChildAt(1).setVisibility(View.VISIBLE);
                    tilLastName.setError("Special characters and numeric values are not allowed.");
                }else{
                    tilLastName.setError(null);
                    if (tilLastName.getChildCount() == 2)
                        tilLastName.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        regBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(!(s.toString().length() < 160)){
                    tilBio.setError("Oops! You run out of characters.");
                }else{
                    tilBio.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        regBarangay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().isEmpty()) {
                    for (int z = 0; z <= barangayarray.length - 1; z++) {
                        if (!s.toString().equals(barangayarray[z])) {
                            if (tilBarangay.getChildCount() == 2)
                                tilBarangay.getChildAt(1).setVisibility(View.VISIBLE);
                            tilBarangay.setError("Select barangay.");
                        }else{
                            tilBarangay.setError(null);
                            if (tilBarangay.getChildCount() == 2)
                                tilBarangay.getChildAt(1).setVisibility(View.GONE);
                            break;
                        }
                    }
                }else {
                    tilBarangay.setError(null);
                    if (tilBarangay.getChildCount() == 2)
                        tilBarangay.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //checkField error end



        //save
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(regUsername.getText().toString().length() == 0 || !TextUtils.isEmpty(tilUsername.getError())){
                    if(regUsername.getText().toString().length() == 0)
                        regUsername.setText("");
                    regUsername.requestFocus();
                }else if(regEmail.getText().toString().length() == 0 || !TextUtils.isEmpty(tilEmail.getError())){
                    if(regEmail.getText().toString().length() == 0)
                        regEmail.setText("");
                    regEmail.requestFocus();
                }else if(regDateOfBirth.getText().toString().length() == 0 || !TextUtils.isEmpty(tilDateOfBirth.getError())) {
                    if(regDateOfBirth.getText().toString().length() == 0)
                        regDateOfBirth.setText("");
                    regDateOfBirth.requestFocus();
                }else if(regFirstName.getText().toString().length() == 0 || !TextUtils.isEmpty(tilFirstName.getError())){
                    if(regFirstName.getText().toString().length() == 0)
                        regFirstName.setText("");
                    regFirstName.requestFocus();
                }else if(regLastName.getText().toString().length() == 0 || !TextUtils.isEmpty(tilLastName.getError())){
                    if(regLastName.getText().toString().length() == 0)
                        regLastName.setText("");
                    regLastName.requestFocus();
                }else if(!TextUtils.isEmpty(tilBio.getError())){
                    if(regBio.getText().toString().length() == 0)
                        regBio.setText("");
                    regBio.requestFocus();
                }else if(!TextUtils.isEmpty(tilBarangay.getError())){
                    if(regBarangay.getText().toString().length() == 0)
                        regBio.setText("");
                    regBarangay.requestFocus();
                }else{
                    //upload Image to Firebase
                    if (profileImageUri != null && !profileImageUri.equals(Uri.EMPTY)){
                        uploadImageToFirebase(profileImageUri);
                    }

                    String email = regEmail.getText().toString();

                    user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            DocumentReference docRef = fStore.collection("Users").document(user.getUid());
                            Map<String, Object> edited = new HashMap<>();
                            edited.put("Email", email);
                            edited.put("Username", regUsername.getText().toString());
                            edited.put("FirstName", regFirstName.getText().toString());
                            edited.put("LastName", regLastName.getText().toString());
                            edited.put("DateOfBirth", regDateOfBirth.getText().toString());
                            edited.put("Bio", regBio.getText().toString());
                            edited.put("HouseAddress", regAddressHouse.getText().toString());
                            edited.put("Barangay", regBarangay.getText().toString());

                            docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(EditProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            });

                            Toast.makeText(EditProfile.this, "Email is changed", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    private void uploadImageToFirebase(Uri imageUri){

        //upload Image to Firebase storage
        StorageReference fileRef = storageReference.child("ProfileImage/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(prof_img);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this, "Failed to update profile picture.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}