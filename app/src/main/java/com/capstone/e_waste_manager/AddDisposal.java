package com.capstone.e_waste_manager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.e_waste_manager.Class.TimeAgo2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.bing.BingMapTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.mylocation.DirectedLocationOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphFloatingActionButton;

public class AddDisposal extends AppCompatActivity implements LocationListener{

    ActivityResultLauncher<String[]> mPermissionResultLauncher;
    private boolean isReadPermissionGranted = false;
    private boolean isWritePermissionGranted = false;
    private boolean isFineLocationPermissionGranted = false;
    private boolean isInternetPermissionGranted = false;
    private boolean isNetAccessPermissionGranted = false;

    TextInputLayout tilAddress, tilBarangay, tilBusinessName, tilDonatedesc, tilAcceptedEwaste, tilAcceptedDonations, tilDisposaldesc;
    EditText regAddress, regBusinessName, regAcceptedEwaste, regDonatedesc, regAcceptedDonations, regDisposaldesc;
    AutoCompleteTextView regBarangay;
    ImageView edit_prof_img, prof_img;
    ImageButton closead, imageButton;
    Button submitButton;
    CardView updateIcon;
    TextView coordinates;
    ChipGroup chipGroup, existingtags;
    ChipGroup donationchipGroup, donationexistingtags;

    ScrollView disposallocationpg, disposalchipscroll;
    NestedScrollView constraintLayout4;

    MotionLayout constraintLayout3;

    SwitchMaterial donationswitch, disposalswitch;
    FrameLayout donationview, disposalview;

    //maps
    MapView map = null;
    NeumorphFloatingActionButton myLocation;
    private DirectedLocationOverlay overlay;
    private boolean hasFix = false;
    private Location currentLocation = null;
    private ScaleBarOverlay mScaleBarOverlay;
    ImageView defaultmap, aerialmap, bingmap;

    ArrayAdapter<String> barangayList;
    List<String> barangay = new ArrayList<>();
    ActivityResultLauncher<String> galleryOpen;
    List<String> disposaltags = new ArrayList<>();
    List<String> donationtags = new ArrayList<>();

    Uri profileImageUri;

    //firebase
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    String userID;

    com.google.firebase.firestore.GeoPoint firebasegeoPoint;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {

                if(result.get(Manifest.permission.ACCESS_FINE_LOCATION) != null){
                    isFineLocationPermissionGranted = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION));
                }else {
                    onBackPressed();
                }
                if(result.get(Manifest.permission.INTERNET) != null){
                    isInternetPermissionGranted = Boolean.TRUE.equals(result.get(Manifest.permission.INTERNET));
                }else {
                    onBackPressed();
                }
                if(result.get(Manifest.permission.ACCESS_NETWORK_STATE) != null){
                    isNetAccessPermissionGranted = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_NETWORK_STATE));
                }else {
                    onBackPressed();
                }
                if(result.get(Manifest.permission.READ_EXTERNAL_STORAGE) != null){
                    isReadPermissionGranted = Boolean.TRUE.equals(result.get(Manifest.permission.READ_EXTERNAL_STORAGE));
                }else {
                    onBackPressed();
                }
                if(result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != null){
                    isWritePermissionGranted = Boolean.TRUE.equals(result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE));
                }else {
                    onBackPressed();
                }
            }
        });

        setContentView(R.layout.activity_add_disposal);

        tilAddress = findViewById(R.id.tilAddress);
        tilBarangay = findViewById(R.id.tilBarangay);
        tilBusinessName = findViewById(R.id.tilBusinessName);
        tilAcceptedEwaste = findViewById(R.id.tilAcceptedEwaste);
        regBusinessName = findViewById(R.id.regBusinessName);
        regAddress = findViewById(R.id.regAddress);
        regBarangay = findViewById(R.id.regBarangay);
        edit_prof_img = findViewById(R.id.edit_prof_img);
        prof_img = findViewById(R.id.prof_img);
        closead = findViewById(R.id.closead);
        submitButton = findViewById(R.id.submitButton);
        updateIcon = findViewById(R.id.updateIcon);
        myLocation = findViewById(R.id.myLocation);
        imageButton = findViewById(R.id.imageButton);
        regAcceptedEwaste = findViewById(R.id.regAcceptedEwaste);
        chipGroup = findViewById(R.id.i_flex_box);
        disposallocationpg = findViewById(R.id.disposallocationpg);
        constraintLayout3 = findViewById(R.id.constraintLayout3);
        constraintLayout4 = findViewById(R.id.constraintLayout4);
        existingtags = findViewById(R.id.existingtags);
        disposalchipscroll = findViewById(R.id.disposalchipscroll);
        disposalview = findViewById(R.id.disposalview);
        disposalswitch = findViewById(R.id.disposalswitch);

        tilDisposaldesc = findViewById(R.id.tilDisposaldesc);
        regDisposaldesc = findViewById(R.id.regDisposaldesc);

        tilDonatedesc = findViewById(R.id.tilDonatedesc);
        tilAcceptedDonations = findViewById(R.id.tilAcceptedDonations);
        regDonatedesc = findViewById(R.id.regDonatedesc);
        donationswitch = findViewById(R.id.donationswitch);
        donationview = findViewById(R.id.donationview);

        donationchipGroup = findViewById(R.id.i_flex_box2);
        donationexistingtags = findViewById(R.id.donationexistingtags);
        regAcceptedDonations = findViewById(R.id.regAcceptedDonations);

        coordinates = findViewById(R.id.textView11);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

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
                    barangayList = new ArrayAdapter<String>(AddDisposal.this, R.layout.dropdown_barangay, barangay);
                    regBarangay.setAdapter(barangayList);
                }
            }
        });

        DocumentReference disposaltypes = fStore.collection("Miscellaneous").document("disposaltypes");
        disposaltypes.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                disposaltags = (List<String>) documentSnapshot.get("ewastetypes");
//                for(String log : disposaltags)
//                {
//                    Log.e("Tag",log);
//                }
                Collections.sort(disposaltags, String.CASE_INSENSITIVE_ORDER);
                if (disposaltags != null) {
                    for (String chipText: disposaltags){
                        addExistingChips(chipText);
                    }
                }
            }
        });

        DocumentReference donationtypes = fStore.collection("Miscellaneous").document("donationtypes");
        donationtypes.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                donationtags = (List<String>) documentSnapshot.get("donationtags");
//                for(String log : donationtags)
//                {
//                    Log.e("Tag",log);
//                }
                Collections.sort(donationtags, String.CASE_INSENSITIVE_ORDER);
                if (donationtags != null) {
                    for (String chipText: donationtags){
                        addExistingDonationChips(chipText);
                    }
                }
            }
        });

        closead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
        edit_prof_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open gallery
                galleryOpen.launch("image/");
                constraintLayout3.transitionToEnd();
            }
        });

        //maps
        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //inflate and create the map

        myLocation = findViewById(R.id.myLocation);
        map = (MapView) findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(12.0);
        map.setMinZoomLevel(4.0);
        map.setMaxZoomLevel(20.0);
        GeoPoint startPoint = new GeoPoint(15.4858, 120.9664);
        mapController.setCenter(startPoint);
        map.setTilesScaledToDpi(true);

        //map types
        defaultmap = findViewById(R.id.defaultmap);
        aerialmap = findViewById(R.id.aerialmap);
        bingmap = findViewById(R.id.bingmap);



        bingmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                org.osmdroid.tileprovider.tilesource.bing.BingMapTileSource.setBingKey("Au_xRaQhcq-BkvxslYHS4B8Zpigts1CbdxoBDbo0o70i9AbiOvsx0NxYQKpffaDO");
                org.osmdroid.tileprovider.tilesource.bing.BingMapTileSource bing=new org.osmdroid.tileprovider.tilesource.bing.BingMapTileSource(null);
//                  bing.setStyle(BingMapTileSource.IMAGERYSET_AERIALWITHLABELS);
                bing.setStyle(BingMapTileSource.IMAGERYSET_ROAD);
                map.setTileSource(bing);
                map.setMaxZoomLevel(19.5);
                defaultmap.setVisibility(View.GONE);
                bingmap.setVisibility(View.GONE);
                aerialmap.setVisibility(View.VISIBLE);
            }
        });

        aerialmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                org.osmdroid.tileprovider.tilesource.bing.BingMapTileSource.setBingKey("Au_xRaQhcq-BkvxslYHS4B8Zpigts1CbdxoBDbo0o70i9AbiOvsx0NxYQKpffaDO");
                org.osmdroid.tileprovider.tilesource.bing.BingMapTileSource bing=new org.osmdroid.tileprovider.tilesource.bing.BingMapTileSource(null);
                bing.setStyle(BingMapTileSource.IMAGERYSET_AERIALWITHLABELS);
//                bing.setStyle(BingMapTileSource.IMAGERYSET_ROAD);
                map.setTileSource(bing);
                map.setMaxZoomLevel(19.5);
                defaultmap.setVisibility(View.VISIBLE);
                bingmap.setVisibility(View.GONE);
                aerialmap.setVisibility(View.GONE);
            }
        });

        defaultmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.setTileSource(TileSourceFactory.MAPNIK);
                map.setMaxZoomLevel(20.0);
                defaultmap.setVisibility(View.GONE);
                bingmap.setVisibility(View.VISIBLE);
                aerialmap.setVisibility(View.GONE);
            }
        });

        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLocation!=null){
                    GeoPoint myPosition = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                    map.getController().animateTo(myPosition, 12.0,2000L);
                }
            }
        });

        if (map != null) {
            addOverlays();
        }

        regAcceptedEwaste.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    if (regAcceptedEwaste.getText().toString().equals(" ")){
                        regAcceptedEwaste.getText().clear();
                    }
                } else {
                    if (regAcceptedEwaste.getText().toString().equals("") && chipGroup.getChildCount() > 0) {
                        regAcceptedEwaste.setText(" ");
                    }
                }
            }
        });
        regAcceptedEwaste.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                String text = regAcceptedEwaste.getText().toString();
                if (text.contains(" ")){
                    regAcceptedEwaste.setText(text.replace(" ", "-"));
                    regAcceptedEwaste.setSelection(regAcceptedEwaste.length());
                }
                if (text.contains("--")){
                    regAcceptedEwaste.setText(text.replace("--", "-"));
                    regAcceptedEwaste.setSelection(regAcceptedEwaste.length());
                }
                if (text.startsWith(" ")){
                    regAcceptedEwaste.setText(text.replace(" ", ""));
                    regAcceptedEwaste.setSelection(regAcceptedEwaste.length());
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (!text.isEmpty() && text.endsWith(",")) {
                    if(!text.trim().equals(",")){
                        if (text.endsWith("-,")){
                            text = text.substring(0, text.length() - 2);
                        }
                        addNewChip(text.replace(",", "").toLowerCase());
                    }
                    tilAcceptedEwaste.setError(null);
                    editable.clear();
                }
            }
        });

        //donation tags
        regAcceptedDonations.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    if (regAcceptedEwaste.getText().toString().equals(" ")){
                        regAcceptedEwaste.getText().clear();
                    }
                } else {
                    if (regAcceptedEwaste.getText().toString().equals("") && chipGroup.getChildCount() > 0) {
                        regAcceptedEwaste.setText(" ");
                    }
                }
            }
        });
        regAcceptedDonations.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    if (regAcceptedDonations.getText().toString().equals(" ")){
                        regAcceptedDonations.getText().clear();
                    }
                } else {
                    if (regAcceptedDonations.getText().toString().equals("") && donationchipGroup.getChildCount() > 0) {
                        regAcceptedDonations.setText(" ");
                    }
                }
            }
        });
        regAcceptedDonations.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                String text = regAcceptedDonations.getText().toString();
                if (text.contains(" ")){
                    regAcceptedDonations.setText(text.replace(" ", "-"));
                    regAcceptedDonations.setSelection(regAcceptedDonations.length());
                }
                if (text.contains("--")){
                    regAcceptedDonations.setText(text.replace("--", "-"));
                    regAcceptedDonations.setSelection(regAcceptedDonations.length());
                }
                if (text.startsWith(" ")){
                    regAcceptedDonations.setText(text.replace(" ", ""));
                    regAcceptedDonations.setSelection(regAcceptedDonations.length());
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (!text.isEmpty() && text.endsWith(",")) {
                    if(!text.trim().equals(",")){
                        if (text.endsWith("-,")){
                            text = text.substring(0, text.length() - 2);
                        }
                        addNewDonationChip(text.replace(",", "").toLowerCase());
                    }
                    tilAcceptedDonations.setError(null);
                    editable.clear();
                }
            }
        });

        regAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilAddress.getChildCount() == 2)
                        tilAddress.getChildAt(1).setVisibility(View.VISIBLE);
                    tilAddress.setError("required*");
                }else{
                    tilAddress.setError(null);
                    if (tilAddress.getChildCount() == 2)
                        tilAddress.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        regBusinessName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilBusinessName.getChildCount() == 2)
                        tilBusinessName.getChildAt(1).setVisibility(View.VISIBLE);
                    tilBusinessName.setError("required*");
                }else{
                    tilBusinessName.setError(null);
                    if (tilBusinessName.getChildCount() == 2)
                        tilBusinessName.getChildAt(1).setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        regAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilAddress.getChildCount() == 2)
                        tilAddress.getChildAt(1).setVisibility(View.VISIBLE);
                    tilAddress.setError("required*");
                }else{
                    tilAddress.setError(null);
                    if (tilAddress.getChildCount() == 2)
                        tilAddress.getChildAt(1).setVisibility(View.GONE);
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
        regDonatedesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilDonatedesc.getChildCount() == 2)
                        tilDonatedesc.getChildAt(1).setVisibility(View.VISIBLE);
                    tilDonatedesc.setError("required*");
                } else if(!(s.toString().length() <= 280)){
                    if (tilDonatedesc.getChildCount() == 2)
                        tilDonatedesc.getChildAt(1).setVisibility(View.VISIBLE);
                    tilDonatedesc.setError("Oops! You run out of characters.");
                }else{
                    tilDonatedesc.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        regDisposaldesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().isEmpty()){
                    if (tilDisposaldesc.getChildCount() == 2)
                        tilDisposaldesc.getChildAt(1).setVisibility(View.VISIBLE);
                    tilDisposaldesc.setError("required*");
                } else if(!(s.toString().length() <= 280)){
                    if (tilDisposaldesc.getChildCount() == 2)
                        tilDisposaldesc.getChildAt(1).setVisibility(View.VISIBLE);
                    tilDisposaldesc.setError("Oops! You run out of characters.");
                }else{
                    tilDisposaldesc.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0; i < map.getOverlays().size();i++){
                    Overlay overlay=map.getOverlays().get(i);
                    if(overlay instanceof Marker&&((Marker) overlay).getId().equals("newmarker")){
                        double latitude = Double.parseDouble(String.valueOf(((Marker) overlay).getPosition().getLatitude()));
                        double longitude = Double.parseDouble(String.valueOf(((Marker) overlay).getPosition().getLongitude()));
                        firebasegeoPoint = new com.google.firebase.firestore.GeoPoint(latitude,longitude);
                    }
                }

                if(firebasegeoPoint == null){
                    Toast.makeText(ctx, "Please add marker on the map.", Toast.LENGTH_LONG).show();
                    imageButton.performClick();
                } else if(regBusinessName.getText().toString().length() == 0 || !TextUtils.isEmpty(tilBusinessName.getError())){
                    if(regBusinessName.getText().toString().length() == 0)
                        regBusinessName.setText("");
                    regBusinessName.requestFocus();
                }else if(regAddress.getText().toString().length() == 0 || !TextUtils.isEmpty(tilAddress.getError())){
                    if(regAddress.getText().toString().length() == 0)
                        regAddress.setText("");
                    regAddress.requestFocus();
                }else if(regBarangay.getText().toString().length() == 0 || !TextUtils.isEmpty(tilBarangay.getError())){
                    if(regBarangay.getText().toString().length() == 0)
                        regBarangay.setText("");
                    regBarangay.requestFocus();
                }else if(!donationswitch.isChecked() && !disposalswitch.isChecked()){
                    Toast.makeText(ctx, "Please select what your organization collects.", Toast.LENGTH_LONG).show();
                }else if((regDisposaldesc.getText().toString().length() == 0 || !TextUtils.isEmpty(tilDisposaldesc.getError())) && disposalswitch.isChecked()){
                    if(regDisposaldesc.getText().toString().length() == 0)
                        regDisposaldesc.setText("");
                    regDisposaldesc.requestFocus();
                }else if(chipGroup.getChildCount() == 0 && disposalswitch.isChecked()){
                    if (tilAcceptedEwaste.getChildCount() == 2)
                        tilAcceptedEwaste.getChildAt(1).setVisibility(View.VISIBLE);
                    tilAcceptedEwaste.setError("place a COMMA to add a tag. required*");
                    regAcceptedEwaste.requestFocus();
                }else if((regDonatedesc.getText().toString().length() == 0 || !TextUtils.isEmpty(tilDonatedesc.getError())) && donationswitch.isChecked()){
                    if(regDonatedesc.getText().toString().length() == 0)
                        regDonatedesc.setText("");
                    regDonatedesc.requestFocus();
                }else if(donationchipGroup.getChildCount() == 0 && donationswitch.isChecked()){
                    if (tilAcceptedDonations.getChildCount() == 2)
                        tilAcceptedDonations.getChildAt(1).setVisibility(View.VISIBLE);
                    tilAcceptedDonations.setError("place a COMMA to add a tag. required*");
                    regAcceptedDonations.requestFocus();
                }else{
                    fStore.collection("Users").document(userID).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null){
                            Map<String, Object> doc = new HashMap<>();
                            doc.put("markername", regBusinessName.getText().toString().trim());
                            doc.put("address", regAddress.getText().toString().trim());
                            doc.put("barangay", regBarangay.getText().toString().trim());
                            doc.put("maplocation", firebasegeoPoint);
                            doc.put("markerUid", fAuth.getCurrentUser().getUid());
                            if (profileImageUri != null && !profileImageUri.equals(Uri.EMPTY)){
                                doc.put("hasImage", true);
                            }else{
                                doc.put("hasImage", false);
                            }
                            if (disposalswitch.isChecked()){
                                doc.put("disposaldesc", regDisposaldesc.getText().toString().trim());
                                doc.put("ewastetypes", Collections.emptyList());
                                doc.put("collect_ewaste", true);
                            }
                            if (donationswitch.isChecked()){
                                doc.put("donationdesc", regDonatedesc.getText().toString().trim());
                                doc.put("donationtags", Collections.emptyList());
                                doc.put("collect_donation", true);
                            }

                            fStore.collection("DisposalLocations").add(doc).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    Toast.makeText(AddDisposal.this, "New disposal location has been added.", Toast.LENGTH_SHORT).show();
                                    //upload Image to Firebase
                                    DocumentReference disposalEtypes = fStore.collection("DisposalLocations").document(task.getResult().getId());
                                    DocumentReference ewastetag = fStore.collection("Miscellaneous").document("disposaltypes");
                                    List<Integer> ids = chipGroup.getCheckedChipIds();
                                    for (Integer id:ids){
                                        Chip chip = chipGroup.findViewById(id);
                                        disposalEtypes.update("ewastetypes", FieldValue.arrayUnion(chip.getText()));
                                        ewastetag.update("ewastetypes", FieldValue.arrayUnion(chip.getText()));
                                    }

                                    if (donationswitch.isChecked()){
                                        DocumentReference donatetypes = fStore.collection("DisposalLocations").document(task.getResult().getId());
                                        DocumentReference donatetag = fStore.collection("Miscellaneous").document("donationtypes");
                                        List<Integer> donationids = donationchipGroup.getCheckedChipIds();
                                        for (Integer id:donationids){
                                            Chip chip = donationchipGroup.findViewById(id);
                                            donatetypes.update("donationtags", FieldValue.arrayUnion(chip.getText()));
                                            donatetag.update("donationtags", FieldValue.arrayUnion(chip.getText()));
                                        }
                                    }

                                    if (profileImageUri != null && !profileImageUri.equals(Uri.EMPTY)){
                                        uploadImageToFirebase(profileImageUri, task.getResult().getId());
                                    }
                                    onBackPressed();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddDisposal.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            });
                        }else{
                            Toast.makeText(AddDisposal.this, "Error Getting User Data", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageButton.performLongClick();
            }
        });

        map.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        disposalchipscroll.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        regDonatedesc.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        donationswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    donationview.setVisibility(View.VISIBLE);
                } else{
                    donationview.setVisibility(View.GONE);
                }
            }
        });

        disposalswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    disposalview.setVisibility(View.VISIBLE);
                } else{
                    disposalview.setVisibility(View.GONE);
                }
            }
        });

    }

    protected void addOverlays() {
        overlay = new DirectedLocationOverlay(this);
        overlay.setShowAccuracy(true);
        map.getOverlays().add(overlay);

        MapEventsOverlay events = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if (constraintLayout3.getProgress() == 0.0f){
                    constraintLayout3.transitionToEnd();
                }else{
                    constraintLayout3.transitionToStart();
                }
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                showDialog(p);
                return true;
            }
        });
        map.getOverlayManager().add(events);
    }

    boolean hasmarker = false;
    private void showDialog(GeoPoint p) {
        if (hasmarker){
            for(int i=0; i < map.getOverlays().size();i++){
                Overlay overlay=map.getOverlays().get(i);
                if(overlay instanceof Marker&&((Marker) overlay).getId().equals("newmarker")){
                    ((Marker) overlay).setPosition(new GeoPoint(p.getLatitude(), p.getLongitude()));
                    map.getController().animateTo(p, 19.5,2000L);
                    coordinates.setText(p.toString());
                    map.invalidate();
                    hasmarker = true;
                }
            }
        }else{
            final Drawable drawable = getResources().getDrawable(R.drawable.disposal);
            Marker m = new Marker(map);
            m.setPosition(new GeoPoint(p.getLatitude(), p.getLongitude()));
            m.setDraggable(true);
            m.setId("newmarker");
            m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            m.setInfoWindow(null);
            m.setIcon(drawable);
            m.setOnMarkerDragListener(new OnMarkerDragListenerDrawer());
            map.getOverlayManager().add(m);
            map.getController().animateTo(p, 19.5,2000L);
            coordinates.setText(p.toString());
            map.invalidate();
            hasmarker = true;
        }
    }
    class OnMarkerDragListenerDrawer implements Marker.OnMarkerDragListener {

        @Override
        public void onMarkerDrag(Marker marker) {

        }

        @Override
        public void onMarkerDragEnd(Marker marker) {
            coordinates.setText(marker.getPosition().toString());
            map.getController().animateTo(marker.getPosition(), 19.5,2000L);
            map.invalidate();
        }

        @Override
        public void onMarkerDragStart(Marker marker) {

        }
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onResume() {
        super.onResume();
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            //on API15 AVDs,network provider fails. no idea why
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                AccessPermission();
                return;
            }
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) this);
        } catch (Exception ex) {
        }

        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        lm.removeUpdates(this);

        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onLocationChanged(Location location) {
        //after the first fix, schedule the task to change the icon
        if (!hasFix) {
            TimerTask changeIcon = new TimerTask() {
                @Override
                public void run() {
                    Activity act = AddDisposal.this;
                    if (act != null)
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.mylocation);
                                    overlay.setDirectionArrow(drawable.getBitmap());
                                    map.getController().animateTo(new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude()));
                                } catch (Throwable t) {
                                    //insultates against crashing when the user rapidly switches fragments/activities
                                }
                            }
                        });

                }
            };
            Timer timer = new Timer();
            timer.schedule(changeIcon, 100);
        }

        currentLocation = location;
        hasFix = true;
        overlay.setBearing(location.getBearing());
        overlay.setAccuracy((int) location.getAccuracy());
        overlay.setLocation(new GeoPoint(location.getLatitude(), location.getLongitude()));
        map.invalidate();
    }

    private void uploadImageToFirebase(Uri imageUri, String docid){

        //upload Image to Firebase storage
        StorageReference fileRef = storageReference.child("MapMarkers/"+docid+"/marker.jpg");
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
                Toast.makeText(AddDisposal.this, "Failed to upload picture.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void AccessPermission() {

        isFineLocationPermissionGranted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        isInternetPermissionGranted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED;

        isNetAccessPermissionGranted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_NETWORK_STATE)
                == PackageManager.PERMISSION_GRANTED;

        isReadPermissionGranted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        isWritePermissionGranted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        List<String> permissionRequest = new ArrayList<String>();

        if (!isFineLocationPermissionGranted){
            permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!isInternetPermissionGranted){
            permissionRequest.add(Manifest.permission.INTERNET);
        }
        if (!isNetAccessPermissionGranted){
            permissionRequest.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if (!isReadPermissionGranted){
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!isWritePermissionGranted){
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionRequest.isEmpty()){
            mPermissionResultLauncher.launch(permissionRequest.toArray(new String[0]));
        }
    }

    private void addNewChip(String text) {
        Chip newChip = (Chip) LayoutInflater.from(this).inflate(R.layout.chip_item, chipGroup, false);
        newChip.setId(ViewCompat.generateViewId());
        newChip.setText(text);
        newChip.setCheckedIconVisible(false);
        newChip.setChecked(true);
        newChip.setCloseIconVisible(true);
        newChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Integer> ids = existingtags.getCheckedChipIds();
                for (Integer id:ids){
                    Chip chip = existingtags.findViewById(id);
                    if (chip.getText() == text){
                        chip.setChecked(false);
                    }
                }
                chipGroup.removeView(newChip);
            }
        });
        newChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b)
                    newChip.performCloseIconClick();
            }
        });
        chipGroup.addView(newChip);
    }

    private void addNewDonationChip(String text) {
        Chip newChip = (Chip) LayoutInflater.from(this).inflate(R.layout.chip_item, donationchipGroup, false);
        newChip.setId(ViewCompat.generateViewId());
        newChip.setText(text);
        newChip.setCheckedIconVisible(false);
        newChip.setChecked(true);
        newChip.setCloseIconVisible(true);
        newChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Integer> ids = donationexistingtags.getCheckedChipIds();
                for (Integer id:ids){
                    Chip chip = donationexistingtags.findViewById(id);
                    if (chip.getText() == text){
                        chip.setChecked(false);
                    }
                }
                donationchipGroup.removeView(newChip);
            }
        });
        newChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b)
                    newChip.performCloseIconClick();
            }
        });
        donationchipGroup.addView(newChip);
    }

    private void addExistingChips(String text) {
        Chip existingChip = (Chip) LayoutInflater.from(this).inflate(R.layout.chip_item, existingtags, false);
        existingChip.setId(ViewCompat.generateViewId());
        existingChip.setText(text);
        existingChip.setCheckedIconVisible(true);
        existingChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    addNewChip(existingChip.getText().toString());
                    tilAcceptedEwaste.setError(null);
                }
                else{
                    List<Integer> ids = chipGroup.getCheckedChipIds();
                    for (Integer id:ids){
                        Chip chip = chipGroup.findViewById(id);
                        if(chip.getText().toString() == text){
                            chip.performCloseIconClick();
                        }
                    }
                }
            }
        });
        existingtags.addView(existingChip);
    }

    private void addExistingDonationChips(String text) {
        Chip existingChip = (Chip) LayoutInflater.from(this).inflate(R.layout.chip_item, donationexistingtags, false);
        existingChip.setId(ViewCompat.generateViewId());
        existingChip.setText(text);
        existingChip.setCheckedIconVisible(true);
        existingChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    addNewDonationChip(existingChip.getText().toString());
                    tilAcceptedDonations.setError(null);
                }
                else{
                    List<Integer> ids = donationchipGroup.getCheckedChipIds();
                    for (Integer id:ids){
                        Chip chip = donationchipGroup.findViewById(id);
                        if(chip.getText().toString() == text){
                            chip.performCloseIconClick();
                        }
                    }
                }
            }
        });
        donationexistingtags.addView(existingChip);
    }
    private static final String TAG = "MyActivity";
}