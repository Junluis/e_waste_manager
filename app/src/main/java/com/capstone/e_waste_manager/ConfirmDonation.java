package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConfirmDonation extends AppCompatActivity {

    ImageButton closedr;
    CardView pickupcard, dropoffcard;
    RadioButton pickup, dropoff;
    Button submit;

    TextInputLayout tilBarangay, tilMobileNumber, tilAddressHouse;
    EditText regMobileNumber, regAddressHouse;
    TextView brandandmodel, condition, deviceage, markername, address, email, markeruid, textView28;
    Chip devicetype;

    AutoCompleteTextView regBarangay;
    ArrayAdapter<String> barangayList;
    List<String> barangay = new ArrayList<>();

    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    String userID, indevicetype, inbrand, inmodel, incondition, indeviceage, docid;

    ImageView imageView6;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_donation);

        closedr = findViewById(R.id.closedr);
        pickupcard = findViewById(R.id.pickupcard);
        dropoffcard = findViewById(R.id.dropoffcard);
        pickup = findViewById(R.id.pickup);
        dropoff = findViewById(R.id.dropoff);
        submit = findViewById(R.id.submit);

        regBarangay = findViewById(R.id.regBarangay);
        regMobileNumber = findViewById(R.id.regMobileNumber);
        regAddressHouse = findViewById(R.id.regAddressHouse);
        tilBarangay = findViewById(R.id.tilBarangay);
        tilMobileNumber = findViewById(R.id.tilMobileNumber);
        tilAddressHouse = findViewById(R.id.tilAddressHouse);
        imageView6 = findViewById(R.id.imageView6);

        textView28 = findViewById(R.id.textView28);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        indevicetype = getIntent().getStringExtra("devicetype");
        inbrand = getIntent().getStringExtra("brand");
        inmodel = getIntent().getStringExtra("model");
        incondition = getIntent().getStringExtra("condition");
        indeviceage = getIntent().getStringExtra("deviceage");
        docid = getIntent().getStringExtra("docid");

        bitmap = (Bitmap)this.getIntent().getParcelableExtra("Bitmap");
        imageView6.setImageBitmap(bitmap);

        brandandmodel = findViewById(R.id.brandandmodel);
        condition = findViewById(R.id.condition);
        devicetype = findViewById(R.id.devicetype);
        deviceage = findViewById(R.id.deviceage);

        brandandmodel.setText(inbrand+" "+inmodel);
        condition.setText(incondition);
        devicetype.setText(indevicetype);
        deviceage.setText(indeviceage);

        markername = findViewById(R.id.markername);
        address = findViewById(R.id.address);
        email = findViewById(R.id.email);
        markeruid = findViewById(R.id.markeruid);

        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showViewer(imageView6);
            }
        });

        DocumentReference orgReference = fStore.collection("DisposalLocations").document(docid);
        orgReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                markername.setText(documentSnapShot.getString("markername"));
                String docaddress = documentSnapShot.getString("address");
                String docbarangay = documentSnapShot.getString("barangay");
                markeruid.setText(documentSnapShot.getString("markerUid"));
                address.setText(docaddress+ ", "+ docbarangay);
            }
        });

        closedr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        pickup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    dropoff.setChecked(false);
                    tilAddressHouse.setVisibility(View.VISIBLE);
                    tilBarangay.setVisibility(View.VISIBLE);
                    textView28.setVisibility(View.VISIBLE);
                }
            }
        });
        dropoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    pickup.setChecked(false);
                    textView28.setVisibility(View.GONE);
                    tilAddressHouse.setVisibility(View.GONE);
                    tilBarangay.setVisibility(View.GONE);
                }
            }
        });

        pickupcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickup.setChecked(true);
                dropoff.setChecked(false);
                tilAddressHouse.setVisibility(View.VISIBLE);
                tilBarangay.setVisibility(View.VISIBLE);
                textView28.setVisibility(View.VISIBLE);
            }
        });

        dropoffcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickup.setChecked(false);
                dropoff.setChecked(true);
                textView28.setVisibility(View.GONE);
                tilAddressHouse.setVisibility(View.GONE);
                tilBarangay.setVisibility(View.GONE);
            }
        });

        DocumentReference docRef = fStore.collection("Miscellaneous").document("cvUA8BB7Pk0Ud7kYwxoT");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                barangay = (List<String>) documentSnapshot.get("Barangay");
//                for(String log : barangay)
//                {
//                    Log.e("Tag",log);
//                }
                if (barangay != null) {
                    Collections.sort(barangay, String.CASE_INSENSITIVE_ORDER);
                    barangayList = new ArrayAdapter<String>(ConfirmDonation.this, R.layout.dropdown_barangay, barangay);
                    regBarangay.setAdapter(barangayList);
                }
            }
        });

        DocumentReference documentReference = fStore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapShot, @Nullable FirebaseFirestoreException error) {
                regBarangay.setText(documentSnapShot.getString("Barangay"));
                regAddressHouse.setText(documentSnapShot.getString("HouseAddress"));
                regMobileNumber.setText(documentSnapShot.getString("Number"));
                email.setText(documentSnapShot.getString("FirstName").substring(0, 1).toUpperCase()
                        + documentSnapShot.getString("FirstName").substring(1)+ " " +
                        documentSnapShot.getString("LastName").substring(0, 1).toUpperCase()
                        + documentSnapShot.getString("LastName").substring(1));
            }
        });

        regBarangay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilBarangay.getChildCount() == 2)
                        tilBarangay.getChildAt(1).setVisibility(View.VISIBLE);
                    tilBarangay.setError("required*");
                    tilBarangay.setErrorIconDrawable(0);
                } else if (!s.toString().isEmpty()) {
                    for (int z = 0; z <= barangay.size() - 1; z++) {
                        if (!barangay.contains(s.toString())) {
                            if (tilBarangay.getChildCount() == 2)
                                tilBarangay.getChildAt(1).setVisibility(View.VISIBLE);
                            tilBarangay.setError("Select barangay.");
                            tilBarangay.setErrorIconDrawable(0);
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
        regAddressHouse.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilAddressHouse.getChildCount() == 2)
                        tilAddressHouse.getChildAt(1).setVisibility(View.VISIBLE);
                    tilAddressHouse.setError("required*");
                }else{
                    tilAddressHouse.setError(null);
                    if (tilAddressHouse.getChildCount() == 2)
                        tilAddressHouse.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        regMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilMobileNumber.getChildCount() == 2)
                        tilMobileNumber.getChildAt(1).setVisibility(View.VISIBLE);
                    tilMobileNumber.setError("required*");
                } else if(s.toString().startsWith("0")){
                    regMobileNumber.setText(s.toString().replaceFirst("0",""));
                }else if(s.length() > 10){
                    if (tilMobileNumber.getChildCount() == 2)
                        tilMobileNumber.getChildAt(1).setVisibility(View.VISIBLE);
                    tilMobileNumber.setError("Invalid number.");
                }else{
                    tilMobileNumber.setError(null);
                    if (tilMobileNumber.getChildCount() == 2)
                        tilMobileNumber.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(regMobileNumber.getText().toString().length() != 10 || !TextUtils.isEmpty(tilMobileNumber.getError())){
                    if (tilMobileNumber.getChildCount() == 2)
                        tilMobileNumber.getChildAt(1).setVisibility(View.VISIBLE);
                    tilMobileNumber.setError("Invalid number.");
                    regMobileNumber.requestFocus();
                }else if((regAddressHouse.getText().toString().length() == 0 || !TextUtils.isEmpty(tilAddressHouse.getError())) && tilAddressHouse.getVisibility() == View.VISIBLE){
                    if(regAddressHouse.getText().toString().length() == 0)
                        regAddressHouse.setText("");
                    regAddressHouse.requestFocus();
                }else if((regBarangay.getText().toString().length() == 0 || !TextUtils.isEmpty(tilBarangay.getError())) && tilAddressHouse.getVisibility() == View.VISIBLE){
                    if(regBarangay.getText().toString().length() == 0)
                        regBarangay.setText("");
                    regBarangay.requestFocus();
                }else{
                    uploadData();
                }
            }
        });
    }
    private void uploadData() {
        fStore.collection("Users").document(userID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null){
                Map<String, Object> doc = new HashMap<>();
                doc.put("status", "Pending");
                if(pickup.isChecked()){
                    doc.put("method", "Pickup");
                }else{
                    doc.put("method", "Drop Off");
                }
                doc.put("number", regMobileNumber.getText().toString().trim());

                if(tilAddressHouse.getVisibility() == View.VISIBLE){
                    doc.put("address", regAddressHouse.getText().toString().trim());
                    doc.put("barangay", regBarangay.getText().toString().trim());
                }

                doc.put("brand", inbrand.trim());
                doc.put("model", inmodel.trim());
                doc.put("condition", incondition.trim());
                doc.put("deviceAge", indeviceage.trim());
                doc.put("deviceType", indevicetype.trim());

                doc.put("donationUid", fAuth.getCurrentUser().getUid());
                doc.put("markerUid", markeruid.getText().toString().trim());
                doc.put("donationDocId", docid);

                doc.put("donationDate", FieldValue.serverTimestamp());

                fStore.collection("Donate").add(doc).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        firebaseUploadBitmap(bitmap, task.getResult().getId());

                        Intent i = new Intent(ConfirmDonation.this, Home.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                        Toast.makeText(ConfirmDonation.this, "Donate submitted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ConfirmDonation.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
            }else{
                Toast.makeText(ConfirmDonation.this, "Error Getting User Data", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void firebaseUploadBitmap(Bitmap bitmap, String docid) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();
        StorageReference fileRef = storageReference.child("Donate/"+docid+"/donateimg.jpg");

        Task<Uri> urlTask = fileRef.putBytes(data).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }

            // Continue with the task to get the download URL
            return fileRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                String uri = downloadUri.toString();
                Picasso.get().load(uri).into(imageView6);
            }
        });
    }

    private static final Integer[] RESOURCES = new Integer[]{R.drawable.shape_placeholder};

    private void showViewer(ImageView target) {
        new StfalconImageViewer.Builder<>(this, RESOURCES, new ImageLoader<Integer>() {
            @Override
            public void loadImage(ImageView imageView, Integer drawableRes) {
                imageView.setBackgroundResource(drawableRes);
                imageView.setImageBitmap(bitmap);
            }
        }).withTransitionFrom(target).show();
    }

}