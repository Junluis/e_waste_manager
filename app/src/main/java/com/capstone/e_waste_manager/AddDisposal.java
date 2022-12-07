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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphFloatingActionButton;

public class AddDisposal extends AppCompatActivity implements LocationListener{

    MotionLayout disposallocationpg;

    ActivityResultLauncher<String[]> mPermissionResultLauncher;
    private boolean isReadPermissionGranted = false;
    private boolean isWritePermissionGranted = false;
    private boolean isFineLocationPermissionGranted = false;
    private boolean isInternetPermissionGranted = false;
    private boolean isNetAccessPermissionGranted = false;

    TextInputLayout tilAddress, tilBarangay, tilBusinessName, tilAcceptedEwaste;
    EditText regAddress, regBusinessName, regAcceptedEwaste;
    AutoCompleteTextView regBarangay;
    ImageView edit_prof_img, prof_img;
    ImageButton closead, imageButton;
    Button submitButton;
    CardView updateIcon;
    TextView coordinates;
    ChipGroup chipGroup;
    NestedScrollView edit_profile;

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

    Uri profileImageUri;

    //firebase
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    String userID;

    com.google.firebase.firestore.GeoPoint firebasegeoPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {

                if(result.get(Manifest.permission.ACCESS_FINE_LOCATION) != null){
                    isFineLocationPermissionGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                }
                if(result.get(Manifest.permission.INTERNET) != null){
                    isInternetPermissionGranted = result.get(Manifest.permission.INTERNET);
                }
                if(result.get(Manifest.permission.ACCESS_NETWORK_STATE) != null){
                    isNetAccessPermissionGranted = result.get(Manifest.permission.ACCESS_NETWORK_STATE);
                }
                if(result.get(Manifest.permission.READ_EXTERNAL_STORAGE) != null){
                    isReadPermissionGranted = result.get(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                if(result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != null){
                    isWritePermissionGranted = result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

            }
        });
        setContentView(R.layout.activity_add_disposal);

        tilAddress = findViewById(R.id.tilAddress);
        tilBarangay = findViewById(R.id.tilBarangay);
        tilBusinessName = findViewById(R.id.tilBusinessName);
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
        tilAcceptedEwaste = findViewById(R.id.tilAcceptedEwaste);
        regAcceptedEwaste = findViewById(R.id.regAcceptedEwaste);
        chipGroup = findViewById(R.id.i_flex_box);
        edit_profile = findViewById(R.id.edit_profile);
        disposallocationpg = findViewById(R.id.disposallocationpg);

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
                disposallocationpg.transitionToEnd();
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

        regAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    disposallocationpg.transitionToEnd();
                }
            }
        });
        regBarangay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    disposallocationpg.transitionToEnd();
                }
            }
        });
        regBusinessName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    disposallocationpg.transitionToEnd();
                }
            }
        });

        regAcceptedEwaste.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    if (regAcceptedEwaste.getText().toString().equals(" ")){
                        regAcceptedEwaste.getText().clear();
                        disposallocationpg.transitionToEnd();
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
                } else if (!s.toString().isEmpty()) {
                    for (int z = 0; z <= barangay.size() - 1; z++) {
                        if (!barangay.contains(s.toString())) {
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
                    disposallocationpg.transitionToStart();
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

                            fStore.collection("DisposalLocations").add(doc).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    Toast.makeText(AddDisposal.this, "New disposal location has been added.", Toast.LENGTH_SHORT).show();
                                    //upload Image to Firebase
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

    }

    protected void addOverlays() {
        overlay = new DirectedLocationOverlay(this);
        overlay.setShowAccuracy(true);
        map.getOverlays().add(overlay);

        MapEventsOverlay events = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
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
        newChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chipGroup.removeView(newChip);
            }
        });
        chipGroup.addView(newChip);
    }
}