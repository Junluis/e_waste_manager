package com.capstone.e_waste_manager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.capstone.e_waste_manager.model.DisposalModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Donate extends AppCompatActivity {

    ImageButton closedd;
    Button regNext;
    AutoCompleteTextView regDeviceType;
    EditText regBrand, regModel, regCondition, regDeviceAge;
    TextInputLayout tilBrand, tilModel, tilCondition, tilDeviceAge, tilDeviceType;
    TextView textView15;

    Spinner spinner;
    String[] calendrical = {"Years","Months","Days"};

    ArrayAdapter<String> deviceList;
    List<String> devicetypes = new ArrayList<>();

    SnapHelper snapHelper;
    FirestoreRecyclerOptions<DisposalModel> options;
    RecyclerView disposalRecycler;
    LinearLayoutManager linearLayoutManager;
    Query query, querytags;

    //firebase
    FirebaseFirestore fStore;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirestoreRecyclerAdapter adapter;
    FirebaseUser user;

    private static final float MILLISECONDS_PER_INCH = 50f; //default is 25f (bigger = slower)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        closedd = findViewById(R.id.closedr);
        regNext = findViewById(R.id.regNext);

        regDeviceType =findViewById(R.id.regDeviceType);
        textView15 = findViewById(R.id.textView15);

        //firebase
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        regBrand = findViewById(R.id.regBrand);
        regModel = findViewById(R.id.regModel);
        regCondition = findViewById(R.id.regCondition);
        tilBrand = findViewById(R.id.tilBrand);
        tilModel = findViewById(R.id.tilModel);
        tilCondition = findViewById(R.id.tilCondition);
        tilDeviceAge = findViewById(R.id.tilDeviceAge);
        tilDeviceType = findViewById(R.id.tilDeviceType);

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> calendricaladapter = new ArrayAdapter<String>(Donate.this, android.R.layout.simple_spinner_dropdown_item, calendrical);
        calendricaladapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(calendricaladapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = adapterView.getItemAtPosition(i).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        regDeviceAge = findViewById(R.id.regDeviceAge);

        closedd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //disposal locations
        disposalRecycler = findViewById(R.id.disposalRecycler);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        disposalRecycler.setLayoutManager(linearLayoutManager);

        disposalRecycler.setItemAnimator(null);

        query = fStore.collection("DisposalLocations")
                .orderBy("barangay", Query.Direction.ASCENDING);

        options = new FirestoreRecyclerOptions.Builder<DisposalModel>()
                .setQuery(query, DisposalModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<DisposalModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull DisposalModel model) {
                holder.bind(model);
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.disposal_each, group,false);
                return new ViewHolder(view);
            }
        };

        disposalRecycler.setAdapter(adapter);
        disposalRecycler.setPadding(100,0,100,0);

        snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(disposalRecycler);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    RecyclerView.ViewHolder viewHolder = disposalRecycler.findViewHolderForAdapterPosition(0);
                    RelativeLayout rrl= viewHolder.itemView.findViewById(R.id.disposalcard);
                    rrl.animate().scaleX(1).scaleY(1).setDuration(350).setInterpolator(new AccelerateInterpolator()).start();
                }catch (Exception ignored){

                }
            }
        }, 1000);

        disposalRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                View v = snapHelper.findSnapView(linearLayoutManager);
                int pos = linearLayoutManager.getPosition(v);

                RecyclerView.ViewHolder viewHolder = disposalRecycler.findViewHolderForAdapterPosition(pos);
                RelativeLayout rrl = viewHolder.itemView.findViewById(R.id.disposalcard);
                ToggleButton showmore = viewHolder.itemView.findViewById(R.id.showmore);
                ConstraintLayout details = viewHolder.itemView.findViewById(R.id.details);
                ScrollView cardscroll = viewHolder.itemView.findViewById(R.id.cardscroll);


                showmore.setChecked(false);
                details.setVisibility(View.GONE);

                cardscroll.setOnTouchListener(new View.OnTouchListener() {
                    // Setting on Touch Listener for handling the touch inside ScrollView
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // Disallow the touch request for parent scroll on touch of child view
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return false;
                    }
                });

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    rrl.animate().setDuration(350).scaleX(1).scaleY(1).setInterpolator(new AccelerateInterpolator()).start();
                }else{
                    rrl.animate().setDuration(350).scaleX(0.75f).scaleY(0.75f).setInterpolator(new AccelerateInterpolator()).start();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        regDeviceType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                querytags = fStore.collection("DisposalLocations")
                        .orderBy("barangay", Query.Direction.ASCENDING).whereArrayContains("donationtags", regDeviceType.getText().toString());

                FirestoreRecyclerOptions<DisposalModel> options2 = new FirestoreRecyclerOptions.Builder<DisposalModel>()
                        .setQuery(querytags, DisposalModel.class)
                        .build();
                adapter.updateOptions(options2);
                disposalRecycler.setAdapter(null);
                disposalRecycler.setAdapter(adapter);
                adapter.startListening();
                disposalRecycler.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(disposalRecycler.getContext()) {
                            @Override
                            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                            }
                        };

                        linearSmoothScroller.setTargetPosition(0);
                        linearLayoutManager.startSmoothScroll(linearSmoothScroller);
                    }
                },500);

                textView15.setVisibility(View.VISIBLE);
            }
        });

        DocumentReference docRef = fStore.collection("Miscellaneous").document("donationtypes");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                devicetypes = (List<String>) documentSnapshot.get("donationtags");
//                for(String log : barangay)
//                {
//                    Log.e("Tag",log);
//                }
                if (devicetypes != null) {
                    Collections.sort(devicetypes, String.CASE_INSENSITIVE_ORDER);
                    deviceList = new ArrayAdapter<String>(Donate.this, R.layout.dropdown_barangay, devicetypes);
                    regDeviceType.setAdapter(deviceList);
                }
            }
        });

        //checkField error
        regDeviceType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilDeviceType.getChildCount() == 2)
                        tilDeviceType.getChildAt(1).setVisibility(View.VISIBLE);
                    tilDeviceType.setError("required*");
                }else{
                    tilDeviceType.setError(null);
                    if (tilDeviceType.getChildCount() == 2)
                        tilDeviceType.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        regBrand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilBrand.getChildCount() == 2)
                        tilBrand.getChildAt(1).setVisibility(View.VISIBLE);
                    tilBrand.setError("required*");
                }else{
                    tilBrand.setError(null);
                    if (tilBrand.getChildCount() == 2)
                        tilBrand.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        regModel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilModel.getChildCount() == 2)
                        tilModel.getChildAt(1).setVisibility(View.VISIBLE);
                    tilModel.setError("required*");
                }else{
                    tilModel.setError(null);
                    if (tilModel.getChildCount() == 2)
                        tilModel.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        regCondition.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    tilCondition.setError("required*");
                }else{
                    tilCondition.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        regDeviceAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilDeviceAge.getChildCount() == 2)
                        tilDeviceAge.getChildAt(1).setVisibility(View.VISIBLE);
                    tilDeviceAge.setError("required*");
                    spinner.setVisibility(View.GONE);
                }else{
                    tilDeviceAge.setError(null);
                    spinner.setVisibility(View.VISIBLE);
                    if (tilDeviceAge.getChildCount() == 2)
                        tilDeviceAge.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        regDeviceAge.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    spinner.setVisibility(View.VISIBLE);
                    tilDeviceAge.setError(null);
                    if (tilDeviceAge.getChildCount() == 2)
                        tilDeviceAge.getChildAt(1).setVisibility(View.GONE);
                }else if (!regDeviceAge.getText().toString().isEmpty()){
                    spinner.setVisibility(View.VISIBLE);
                }else{
                    spinner.setVisibility(View.GONE);
                }
            }
        });

        regNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(regDeviceType.getText().toString().length() == 0 || !TextUtils.isEmpty(tilDeviceType.getError())){
                    if(regDeviceType.getText().toString().length() == 0)
                        regDeviceType.setText("");
                    regDeviceType.requestFocus();
                } else if(regBrand.getText().toString().length() == 0 || !TextUtils.isEmpty(tilBrand.getError())){
                    if(regBrand.getText().toString().length() == 0)
                        regBrand.setText("");
                    regBrand.requestFocus();
                } else if(regModel.getText().toString().length() == 0 || !TextUtils.isEmpty(tilModel.getError())){
                    if(regModel.getText().toString().length() == 0)
                        regModel.setText("");
                    regModel.requestFocus();
                } else if(regCondition.getText().toString().length() == 0 || !TextUtils.isEmpty(tilCondition.getError())){
                    if(regCondition.getText().toString().length() == 0)
                        regCondition.setText("");
                    regCondition.requestFocus();
                } else if(regDeviceAge.getText().toString().length() == 0 || !TextUtils.isEmpty(tilDeviceAge.getError())){
                    if(regDeviceAge.getText().toString().length() == 0)
                        regDeviceAge.setText("");
                    regDeviceAge.requestFocus();
                } else {
                    startActivity(new Intent(getApplicationContext(), ConfirmDonation.class));
                }
            }
        });

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView disposalname, disposaladdress, latitude, longitude, disposaldesc, donationdesc, ewasteabout, donationabout, textView20, textView21;
        ImageView disposalicon;
        MaterialButton track;
        ToggleButton zoom, showmore;
        ConstraintLayout details;
        HorizontalScrollView acceptedewaste, accepteddonation;
        Chip collectdonation, collectewaste;
        ChipGroup acceptedewastechipgroup, accepteddonationchipgroup;
        ScrollView cardscroll;

        DisposalModel model;

        List<String> acceptedewastetags = new ArrayList<>();
        List<String> accepteddonationtags = new ArrayList<>();

        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            disposalname = itemView.findViewById(R.id.disposalname);
            disposaladdress = itemView.findViewById(R.id.disposaladdress);
            latitude = itemView.findViewById(R.id.latitude);
            longitude = itemView.findViewById(R.id.longitude);
            disposalicon = itemView.findViewById(R.id.disposalicon);
            track = itemView.findViewById(R.id.track);
            zoom = itemView.findViewById(R.id.zoom);
            showmore = itemView.findViewById(R.id.showmore);
            details = itemView.findViewById(R.id.details);
            acceptedewaste = itemView.findViewById(R.id.scrollView4);
            accepteddonation = itemView.findViewById(R.id.scrollView5);
            collectewaste = itemView.findViewById(R.id.collectewaste);
            collectdonation = itemView.findViewById(R.id.collectdonation);
            disposaldesc = itemView.findViewById(R.id.disposaldesc);
            donationdesc = itemView.findViewById(R.id.donationdesc);
            acceptedewastechipgroup = itemView.findViewById(R.id.acceptedewastechipgroup);
            accepteddonationchipgroup = itemView.findViewById(R.id.accepteddonationchipgroup);
            ewasteabout = itemView.findViewById(R.id.ewasteabout);
            donationabout = itemView.findViewById(R.id.donationabout);
            textView20 = itemView.findViewById(R.id.textView20);
            textView21 = itemView.findViewById(R.id.textView21);
            cardscroll = itemView.findViewById(R.id.cardscroll);

            zoom.setVisibility(View.GONE);

            track.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), GoogleMapView.class);
                    intent.putExtra("latitude1", Double.parseDouble(latitude.getText().toString()));
                    intent.putExtra("longitude1", Double.parseDouble(longitude.getText().toString()));
                    itemView.getContext().startActivity(intent);
                }
            });

            showmore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (showmore.isChecked()){
                        details.setVisibility(View.VISIBLE);


                        cardscroll.setOnTouchListener(new View.OnTouchListener() {
                            // Setting on Touch Listener for handling the touch inside ScrollView
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                // Disallow the touch request for parent scroll on touch of child view
                                v.getParent().requestDisallowInterceptTouchEvent(true);
                                return false;
                            }
                        });
                    }else{
                        details.setVisibility(View.GONE);


                        cardscroll.setOnTouchListener(new View.OnTouchListener() {
                            // Setting on Touch Listener for handling the touch inside ScrollView
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                // Disallow the touch request for parent scroll on touch of child view
                                v.getParent().requestDisallowInterceptTouchEvent(false);
                                return false;
                            }
                        });
                    }
                }
            });

            acceptedewaste.setOnTouchListener(new View.OnTouchListener() {
                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
            accepteddonation.setOnTouchListener(new View.OnTouchListener() {
                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
        }

        public void bind(DisposalModel disposalModel){
            model = disposalModel;
            disposalname.setText(disposalModel.markername);
            disposaladdress.setText(disposalModel.getAddress() + ", " + disposalModel.barangay);
            latitude.setText(""+disposalModel.maplocation.getLatitude());
            longitude.setText(""+disposalModel.maplocation.getLongitude());
            disposaldesc.setText(disposalModel.disposaldesc);
            donationdesc.setText(disposalModel.donationdesc);

//           image per markers
            if (disposalModel.hasImage != null && disposalModel.hasImage){
                StorageReference profileRef = storageReference.child("MapMarkers/"+ disposalModel.docId +"/marker.jpg");
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(disposalicon);
                    }
                });
            }
            if(disposalModel.collect_ewaste != null){
                if(disposalModel.collect_ewaste){
                    collectewaste.setVisibility(View.VISIBLE);
                }
            }else{
                disposaldesc.setVisibility(View.GONE);
                acceptedewastechipgroup.setVisibility(View.GONE);
                ewasteabout.setVisibility(View.GONE);
                textView21.setVisibility(View.GONE);
            }
            if(disposalModel.collect_donation != null){
                if (disposalModel.collect_donation){
                    collectdonation.setVisibility(View.VISIBLE);
                }
            }else{
                donationdesc.setVisibility(View.GONE);
                accepteddonationchipgroup.setVisibility(View.GONE);
                donationabout.setVisibility(View.GONE);
                textView20.setVisibility(View.GONE);
            }

            DocumentReference acceptedewaste = fStore.collection("DisposalLocations").document(disposalModel.docId);
            acceptedewaste.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    acceptedewastetags = (List<String>) documentSnapshot.get("ewastetypes");
//                for(String log : disposaltags)
//                {
//                    Log.e("Tag",log);
//                }
                    if (acceptedewastetags != null) {
                        Collections.sort(acceptedewastetags, String.CASE_INSENSITIVE_ORDER);
                        for (String chipText: acceptedewastetags){
                            acceptedchips(chipText, acceptedewastechipgroup);
                        }
                    }
                }
            });

            DocumentReference accepteddonation = fStore.collection("DisposalLocations").document(disposalModel.docId);
            accepteddonation.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    accepteddonationtags = (List<String>) documentSnapshot.get("donationtags");
//                for(String log : disposaltags)
//                {
//                    Log.e("Tag",log);
//                }
                    if (accepteddonationtags != null) {
                        Collections.sort(accepteddonationtags, String.CASE_INSENSITIVE_ORDER);
                        for (String chipText: accepteddonationtags){
                            acceptedchips(chipText, accepteddonationchipgroup);
                        }
                    }
                }
            });
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    private void acceptedchips(String text, ChipGroup group) {
        Chip existingChip = (Chip) LayoutInflater.from(this).inflate(R.layout.chip_item, group, false);
        existingChip.setId(ViewCompat.generateViewId());
        existingChip.setText(text);
        existingChip.setClickable(false);
        group.addView(existingChip);
    }


}