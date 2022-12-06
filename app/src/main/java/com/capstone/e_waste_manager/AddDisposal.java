package com.capstone.e_waste_manager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

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

    TextInputLayout tilAddress, tilBarangay, tilBusinessName;
    EditText regAddress, regBusinessName;
    AutoCompleteTextView regBarangay;
    ImageView edit_prof_img, prof_img;
    ImageButton closead, imageButton;
    Button submitButton;
    CardView updateIcon;
    TextView coordinates;

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
                if (!s.toString().isEmpty()) {
                    for (int z = 0; z <= barangay.size() - 1; z++) {
                        if (regBarangay.getText().length() == 0) {
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
                    Toast.makeText(ctx, "PLease add marker on the map", Toast.LENGTH_LONG).show();
                    imageButton.performClick();
                } else if(regBusinessName.getText().toString().length() == 0 || !TextUtils.isEmpty(tilBusinessName.getError())){
                    if(regBusinessName.getText().toString().length() == 0)
                        regBusinessName.setText("");
                    regBusinessName.requestFocus();
                }else if(regAddress.getText().toString().length() == 0 || !TextUtils.isEmpty(tilAddress.getError())){
                    if(regAddress.getText().toString().length() == 0)
                        regAddress.setText("");
                    regAddress.requestFocus();
                }else if(regBarangay.getText().length() == 0){
                    if (tilBarangay.getChildCount() == 2)
                        tilBarangay.getChildAt(1).setVisibility(View.VISIBLE);
                    tilBarangay.setError("Select barangay.");
                }else{
                    fStore.collection("Users").document(userID).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null){
                            Map<String, Object> doc = new HashMap<>();
                            doc.put("markername", regBusinessName.getText().toString().trim());
                            doc.put("address", regAddress.getText().toString().trim());
                            doc.put("barangay", regBarangay.getText().toString().trim());
                            doc.put("maplocation", firebasegeoPoint);
                            doc.put("markerUid", fAuth.getCurrentUser().getUid());

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    void AccessPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
                checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.INTERNET}, 1002);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
                checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 1003);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1004);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1005);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1001:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted Fine Location", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied Fine Location", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1002:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted INTERNET", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied INTERNET", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1003:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted NETWORK STATE", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied NETWORK STATE", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1004:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted READ EXTERNAL STORAGE", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied Fine Location", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1005:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted WRITE EXTERNAL STORAGE", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied WRITE EXTERNAL STORAGE", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}