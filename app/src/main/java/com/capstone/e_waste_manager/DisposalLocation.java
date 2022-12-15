package com.capstone.e_waste_manager;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.system.ErrnoException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.airbnb.lottie.utils.LogcatLogger;
import com.capstone.e_waste_manager.model.DisposalModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.bing.BingMapTileSource;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.DirectedLocationOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphFloatingActionButton;

public class DisposalLocation extends AppCompatActivity implements LocationListener{

    ActivityResultLauncher<String[]> mPermissionResultLauncher;
    private boolean isReadPermissionGranted = false;
    private boolean isWritePermissionGranted = false;
    private boolean isFineLocationPermissionGranted = false;
    private boolean isInternetPermissionGranted = false;
    private boolean isNetAccessPermissionGranted = false;

    MapView map = null;
    NeumorphFloatingActionButton myLocation, zoomin, zoomout, back, filter;
    private DirectedLocationOverlay overlay;
    private boolean hasFix = false;
    private Location currentLocation = null;
    private ScaleBarOverlay mScaleBarOverlay;

    RecyclerView disposalRecycler;
    LinearLayoutManager linearLayoutManager;

    //firebase
    FirebaseFirestore fStore;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirestoreRecyclerAdapter adapter;
    FirebaseUser user;

    List<String> disposaltags = new ArrayList<>();
    List<String> donationtags = new ArrayList<>();
    List<String> filtertags = new ArrayList<>();
    List<String> filterdonationtags = new ArrayList<>();
    Dialog filterdialog;

    Query query, querytags;

    ImageView defaultmap, aerialmap, bingmap;

    SnapHelper snapHelper;

    FirestoreRecyclerOptions<DisposalModel> options;

    android.app.AlertDialog dialog;

    ConstraintLayout controllers;

    private final List<Marker> mClicked = new ArrayList<>();
    private final List<String> listcomparator = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
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

        //filter dialog
        filterdialog = new Dialog(this);

        //firebase
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        //transparent status
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

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
        setContentView(R.layout.activity_disposal_location);

        myLocation = findViewById(R.id.myLocation);
        map = (MapView) findViewById(R.id.map);
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
        //        bing.setStyle(BingMapTileSource.IMAGERYSET_AERIALWITHLABELS);
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

        zoomin = findViewById(R.id.zoomin);
        zoomout = findViewById(R.id.zoomout);
        back = findViewById(R.id.back);
        controllers = findViewById(R.id.controllers);

        zoomin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.getController().zoomIn(100L);
            }
        });

        zoomout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.getController().zoomOut(100L);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                View v = snapHelper.findSnapView(linearLayoutManager);
                int pos = linearLayoutManager.getPosition(v);

                RecyclerView.ViewHolder viewHolder = disposalRecycler.findViewHolderForAdapterPosition(pos);
                RelativeLayout rrl = viewHolder.itemView.findViewById(R.id.disposalcard);
                ToggleButton zoom = viewHolder.itemView.findViewById(R.id.zoom);
                ToggleButton showmore = viewHolder.itemView.findViewById(R.id.showmore);
                TextView lat = viewHolder.itemView.findViewById(R.id.latitude);
                TextView lon = viewHolder.itemView.findViewById(R.id.longitude);
                ConstraintLayout details = viewHolder.itemView.findViewById(R.id.details);

                zoom.setChecked(false);
                showmore.setChecked(false);
                details.setVisibility(View.GONE);
                double latitude = Double.parseDouble(lat.getText().toString());
                double longitude = Double.parseDouble(lon.getText().toString());

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    rrl.animate().setDuration(350).scaleX(1).scaleY(1).setInterpolator(new AccelerateInterpolator()).start();
                    map.getController().animateTo(new GeoPoint(latitude, longitude),14.0, 2000L);
                    controllers.animate().translationX(0f).setDuration(500);
                }else{
                    rrl.animate().setDuration(350).scaleX(0.75f).scaleY(0.75f).setInterpolator(new AccelerateInterpolator()).start();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });


        //transparent inset
        ViewCompat.setOnApplyWindowInsetsListener(disposalRecycler, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply the insets as a margin to the view. Here the system is setting
            // only the bottom, left, and right dimensions, but apply whichever insets are
            // appropriate to your layout. You can also update the view padding
            // if that's more appropriate.
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.leftMargin = insets.left;
            mlp.bottomMargin = insets.bottom;
            mlp.rightMargin = insets.right;
            v.setLayoutParams(mlp);

            // Return CONSUMED if you don't want want the window insets to keep being
            // passed down to descendant views.
            return WindowInsetsCompat.CONSUMED;
        });

        filter = findViewById(R.id.filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Showfilter();
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

            track.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), GoogleMapView.class);
                    intent.putExtra("latitude1", Double.parseDouble(latitude.getText().toString()));
                    intent.putExtra("longitude1", Double.parseDouble(longitude.getText().toString()));
                    intent.putExtra("latitude2", Double.parseDouble(currentLocation.getLatitude()+""));
                    intent.putExtra("longitude2", Double.parseDouble(currentLocation.getLongitude()+""));
                    itemView.getContext().startActivity(intent);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    map.getController().animateTo(new GeoPoint(model.maplocation.getLatitude(), model.maplocation.getLongitude()));
                }
            });

            zoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (zoom.isChecked()){
                        map.getController().animateTo(new GeoPoint(model.maplocation.getLatitude(), model.maplocation.getLongitude()), 18.0, 2000L);
                    } else {
                        map.getController().animateTo(new GeoPoint(model.maplocation.getLatitude(), model.maplocation.getLongitude()), 12.0, 2000L);
                    }
                }
            });

            showmore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (showmore.isChecked()){
                        details.setVisibility(View.VISIBLE);
                        controllers.animate().translationX(3000f).setDuration(1000);
                    }else{
                        details.setVisibility(View.GONE);
                        controllers.animate().translationX(0f).setDuration(500);
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

    static private class MyMarker extends Marker {
        MyMarker(MapView mapView) {
            super(mapView);
        }

        @Override
        public boolean onMarkerClickDefault(Marker marker, MapView mapView) { // made public
            return super.onMarkerClickDefault(marker, mapView);
        }
    }

    protected void addOverlays() {
        overlay = new DirectedLocationOverlay(this);
        overlay.setShowAccuracy(true);
        map.getOverlays().add(overlay);

        final DisplayMetrics dm = this.getResources().getDisplayMetrics();
        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setScaleBarOffset(0, (int) (40 * dm.density));
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 150);
        map.getOverlays().add(mScaleBarOverlay);

        map.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {

            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if (mClicked.size() == 0) {
                    return false;
                }
                if (mClicked.size() == 1) {
                    message(mClicked.get(0));
                    mClicked.clear();
                    return true;
                }
                final String[] titles = new String[mClicked.size()];
                final Marker[] items = new Marker[titles.length];
                int i = 0;
                for (final Marker item : mClicked) {
                    titles[i] = item.getTitle();
                    items[i] = item;
                    i++;
                }
                new AlertDialog.Builder(DisposalLocation.this)
                        .setItems(titles, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                message(items[i]);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                mClicked.clear();
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        }));

    }

    private static final float MILLISECONDS_PER_INCH = 50f; //default is 25f (bigger = slower)

    private void message(final Marker pMarker) {
        ((MyMarker) pMarker).onMarkerClickDefault(pMarker, map);

        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(disposalRecycler.getContext()) {
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            }
        };

        String geodata = "GeoPoint { latitude="+pMarker.getPosition().getLatitude()+", "+ "longitude="+pMarker.getPosition().getLongitude()+" }";

        for (int x=0; x < listcomparator.size(); x++){
            if (listcomparator.get(x).equals(geodata)){
                linearSmoothScroller.setTargetPosition(x);
                linearLayoutManager.startSmoothScroll(linearSmoothScroller);
            }
        }
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
        for(int i=0; i < map.getOverlays().size();i++){
            Overlay overlay=map.getOverlays().get(i);
            if(overlay instanceof Marker&&((Marker) overlay).getId().equals("marker")){
                map.getOverlays().remove(overlay);
                i = 0;
            }
        }

        drawmarkers(query);
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

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        filterdialog.dismiss();
        filter.getDrawable().setTint(ContextCompat.getColor(DisposalLocation.this, R.color.darkgray));
        if (adapter != null) {
            adapter.stopListening();
        }
        finish();
    }

    public void onLocationChanged(Location location) {
        //after the first fix, schedule the task to change the icon
        if (!hasFix) {
            TimerTask changeIcon = new TimerTask() {
                @Override
                public void run() {
                    Activity act = DisposalLocation.this;
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

    ArrayAdapter<String> barangayList;
    List<String> barangay = new ArrayList<>();
    ChipGroup existingtags, existingdonationtags;
    String selectedbarangay;
    public void Showfilter(){
        NeumorphFloatingActionButton close_popup;
        TextInputLayout tilBarangay;
        AutoCompleteTextView regBarangay;
        Button applyfilter, clearfilter;
        ImageButton imageButton2;
        filterdialog.setContentView(R.layout.custom_popup_tagfilter);
        LinearLayout filterewaste,filterdonation;
        TabLayout filtertab;

        close_popup = (NeumorphFloatingActionButton) filterdialog.findViewById(R.id.close_popup);
        existingtags = (ChipGroup) filterdialog.findViewById(R.id.existingtags);
        existingdonationtags = (ChipGroup) filterdialog.findViewById(R.id.existingdonationtags);
        tilBarangay = (TextInputLayout) filterdialog.findViewById(R.id.tilBarangay);
        regBarangay = (AutoCompleteTextView) filterdialog.findViewById(R.id.regBarangay);
        applyfilter = (Button) filterdialog.findViewById(R.id.applyfilter);
        clearfilter = (Button) filterdialog.findViewById(R.id.clearfilter);
        imageButton2 = (ImageButton) filterdialog.findViewById(R.id.imageButton2);
        filterewaste = (LinearLayout) filterdialog.findViewById(R.id.filterewaste);
        filterdonation = (LinearLayout) filterdialog.findViewById(R.id.filterdonation);
        filtertab = (TabLayout) filterdialog.findViewById(R.id.filtertab);

        filtertab.addTab(filtertab.newTab().setText("E-waste Filter"));
        filtertab.addTab(filtertab.newTab().setText("Donation Filter"));

        filtertab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    filterewaste.setVisibility(View.VISIBLE);
                    filterdonation.setVisibility(View.GONE);
                }else{
                    filterewaste.setVisibility(View.GONE);
                    filterdonation.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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
                if (disposaltags != null) {
                    Collections.sort(disposaltags, String.CASE_INSENSITIVE_ORDER);
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
//                for(String log : disposaltags)
//                {
//                    Log.e("Tag",log);
//                }
                if (donationtags != null) {
                    Collections.sort(donationtags, String.CASE_INSENSITIVE_ORDER);
                    for (String chipText: donationtags){
                        addExistingDonationChips(chipText);
                    }
                }
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
                    barangayList = new ArrayAdapter<String>(filterdialog.getContext(), R.layout.dropdown_barangay, barangay);
                    regBarangay.setDropDownAnchor(tilBarangay.getId());
                    regBarangay.setAdapter(barangayList);
                }
            }
        });

        if (selectedbarangay != null && !selectedbarangay.trim().isEmpty()){
            regBarangay.setText(selectedbarangay);
        }

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regBarangay.setText("");
            }
        });

        applyfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog();

                if(filtertab.getSelectedTabPosition() == 0){
                    List<Integer> ids = existingtags.getCheckedChipIds();
                    for (Integer id:ids){
                        Chip chip = existingtags.findViewById(id);
                        filtertags.add(chip.getText().toString());
                    }

                    if (!filtertags.isEmpty()){
                        for(String log : filtertags)
                        {
                            Log.e("Tag",log);
                        }

                        if (regBarangay.getText().toString().isEmpty()){
                            querytags = fStore.collection("DisposalLocations")
                                    .whereArrayContainsAny("ewastetypes", filtertags);
                        }else{
                            querytags = fStore.collection("DisposalLocations")
                                    .whereEqualTo("barangay", regBarangay.getText().toString()).whereArrayContainsAny("ewastetypes", filtertags);
                        }

                        FirestoreRecyclerOptions<DisposalModel> options2 = new FirestoreRecyclerOptions.Builder<DisposalModel>()
                                .setQuery(querytags, DisposalModel.class)
                                .build();
                        adapter.updateOptions(options2);
                        drawmarkers(querytags);
                        disposalRecycler.setAdapter(null);
                        disposalRecycler.setAdapter(adapter);

                        filterdonationtags.clear();
                        existingdonationtags.clearCheck();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(adapter.getItemCount()==0){
                                    hideProgressDialog();
                                    selectedbarangay = regBarangay.getText().toString();
                                    Toast.makeText(DisposalLocation.this, "No results found.", Toast.LENGTH_LONG).show();
                                    filter.getDrawable().setTint(ContextCompat.getColor(DisposalLocation.this, R.color.green1));
                                }else{
                                    if (regBarangay.getText().toString() != null && !regBarangay.getText().toString().trim().isEmpty()){
                                        selectedbarangay = regBarangay.getText().toString();
                                    }
                                    hideProgressDialog();
                                    close_popup.performClick();
                                    filter.getDrawable().setTint(ContextCompat.getColor(DisposalLocation.this, R.color.green1));
                                }
                            }
                        }, 1000);
                    }else if (!regBarangay.getText().toString().isEmpty()){
                        querytags = fStore.collection("DisposalLocations")
                                .whereEqualTo("barangay", regBarangay.getText().toString());

                        FirestoreRecyclerOptions<DisposalModel> options2 = new FirestoreRecyclerOptions.Builder<DisposalModel>()
                                .setQuery(querytags, DisposalModel.class)
                                .build();
                        adapter.updateOptions(options2);
                        drawmarkers(querytags);
                        disposalRecycler.setAdapter(null);
                        disposalRecycler.setAdapter(adapter);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(adapter.getItemCount()==0){
                                    hideProgressDialog();
                                    selectedbarangay = regBarangay.getText().toString();
                                    Toast.makeText(DisposalLocation.this, "No results found.", Toast.LENGTH_LONG).show();
                                    filter.getDrawable().setTint(ContextCompat.getColor(DisposalLocation.this, R.color.green1));
                                }else{
                                    if (regBarangay.getText().toString() != null && !regBarangay.getText().toString().trim().isEmpty()){
                                        selectedbarangay = regBarangay.getText().toString();
                                    }
                                    hideProgressDialog();
                                    close_popup.performClick();
                                    filter.getDrawable().setTint(ContextCompat.getColor(DisposalLocation.this, R.color.green1));
                                }
                            }
                        }, 1000);
                    }else{
                        hideProgressDialog();
                        clearfilter.performClick();
                    }
                }else{
                    List<Integer> ids = existingdonationtags.getCheckedChipIds();
                    for (Integer id:ids){
                        Chip chip = existingdonationtags.findViewById(id);
                        filterdonationtags.add(chip.getText().toString());
                    }

                    if (!filterdonationtags.isEmpty()){
                        for(String log : filterdonationtags)
                        {
                            Log.e("Tag",log);
                        }

                        if (regBarangay.getText().toString().isEmpty()){
                            querytags = fStore.collection("DisposalLocations")
                                    .whereArrayContainsAny("ewastetypes", filterdonationtags);
                        }else{
                            querytags = fStore.collection("DisposalLocations")
                                    .whereEqualTo("barangay", regBarangay.getText().toString()).whereArrayContainsAny("ewastetypes", filterdonationtags);
                        }

                        FirestoreRecyclerOptions<DisposalModel> options2 = new FirestoreRecyclerOptions.Builder<DisposalModel>()
                                .setQuery(querytags, DisposalModel.class)
                                .build();
                        adapter.updateOptions(options2);
                        drawmarkers(querytags);
                        disposalRecycler.setAdapter(null);
                        disposalRecycler.setAdapter(adapter);

                        filtertags.clear();
                        existingtags.clearCheck();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(adapter.getItemCount()==0){
                                    hideProgressDialog();
                                    selectedbarangay = regBarangay.getText().toString();
                                    Toast.makeText(DisposalLocation.this, "No results found.", Toast.LENGTH_LONG).show();
                                    filter.getDrawable().setTint(ContextCompat.getColor(DisposalLocation.this, R.color.green1));
                                }else{
                                    if (regBarangay.getText().toString() != null && !regBarangay.getText().toString().trim().isEmpty()){
                                        selectedbarangay = regBarangay.getText().toString();
                                    }
                                    hideProgressDialog();
                                    close_popup.performClick();
                                    filter.getDrawable().setTint(ContextCompat.getColor(DisposalLocation.this, R.color.green1));
                                }
                            }
                        }, 1000);
                    }else if (!regBarangay.getText().toString().isEmpty()){
                        querytags = fStore.collection("DisposalLocations")
                                .whereEqualTo("barangay", regBarangay.getText().toString());

                        FirestoreRecyclerOptions<DisposalModel> options2 = new FirestoreRecyclerOptions.Builder<DisposalModel>()
                                .setQuery(querytags, DisposalModel.class)
                                .build();
                        adapter.updateOptions(options2);
                        drawmarkers(querytags);
                        disposalRecycler.setAdapter(null);
                        disposalRecycler.setAdapter(adapter);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(adapter.getItemCount()==0){
                                    hideProgressDialog();
                                    selectedbarangay = regBarangay.getText().toString();
                                    Toast.makeText(DisposalLocation.this, "No results found.", Toast.LENGTH_LONG).show();
                                    filter.getDrawable().setTint(ContextCompat.getColor(DisposalLocation.this, R.color.green1));
                                }else{
                                    if (regBarangay.getText().toString() != null && !regBarangay.getText().toString().trim().isEmpty()){
                                        selectedbarangay = regBarangay.getText().toString();
                                    }
                                    hideProgressDialog();
                                    close_popup.performClick();
                                    filter.getDrawable().setTint(ContextCompat.getColor(DisposalLocation.this, R.color.green1));
                                }
                            }
                        }, 1000);
                    }else{
                        hideProgressDialog();
                        clearfilter.performClick();
                    }
                }

            }
        });

        clearfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog();
                filtertags.clear();
                filterdonationtags.clear();
                existingtags.clearCheck();
                existingdonationtags.clearCheck();
                regBarangay.setText("");
                selectedbarangay = null;

                filter.getDrawable().setTint(ContextCompat.getColor(DisposalLocation.this, R.color.darkgray));
                adapter.updateOptions(options);
                drawmarkers(query);
                disposalRecycler.setAdapter(null);
                disposalRecycler.setAdapter(adapter);
                hideProgressDialog();
            }
        });

        close_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterdialog.dismiss();
            }
        });

        filterdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        filterdialog.show();
    }

    private void addExistingChips(String text) {
        Chip existingChip = (Chip) LayoutInflater.from(this).inflate(R.layout.chip_item, existingtags, false);
        existingChip.setId(ViewCompat.generateViewId());
        existingChip.setText(text);
        existingChip.setCheckedIconVisible(true);
        List<String> tagstrings = filtertags;
        for (String id:tagstrings){
            if (id.equals(text)){
                existingChip.setChecked(true);
            }
        }
        existingChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                List<Integer> ids = existingtags.getCheckedChipIds();
                if (ids.size() > 10) {
                    existingChip.setChecked(false);
                }
                if(!b){
                    filtertags.remove(text);
                }
            }
        });
        existingtags.addView(existingChip);
    }

    private void addExistingDonationChips(String text) {
        Chip existingChip = (Chip) LayoutInflater.from(this).inflate(R.layout.chip_item, existingdonationtags, false);
        existingChip.setId(ViewCompat.generateViewId());
        existingChip.setText(text);
        existingChip.setCheckedIconVisible(true);
        List<String> tagstrings = filterdonationtags;
        for (String id:tagstrings){
            if (id.equals(text)){
                existingChip.setChecked(true);
            }
        }
        existingChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                List<Integer> ids = existingdonationtags.getCheckedChipIds();
                if (ids.size() > 10) {
                    existingChip.setChecked(false);
                }
                if(!b){
                    filterdonationtags.remove(text);
                }
            }
        });
        existingdonationtags.addView(existingChip);
    }

    private void acceptedchips(String text, ChipGroup group) {
        Chip existingChip = (Chip) LayoutInflater.from(this).inflate(R.layout.chip_item, group, false);
        existingChip.setId(ViewCompat.generateViewId());
        existingChip.setText(text);
        existingChip.setClickable(false);
        group.addView(existingChip);
    }

    private void drawmarkers(Query query){
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {

                for (int i = 0; i < map.getOverlays().size(); i++) {
                    Overlay overlay = map.getOverlays().get(i);
                    if (overlay instanceof Marker && ((Marker) overlay).getId().equals("marker")) {
                        map.getOverlays().remove(overlay);
                        i = 0;
                    }
                }

                listcomparator.clear();
                if (error != null) {
                    Log.d(TAG, "Error:" + error.getMessage());
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            for (DocumentSnapshot document : snapshots.getDocuments()) {
                                try {
                                    final Drawable drawable = getResources().getDrawable(R.drawable.disposal);
                                    final Marker marker = new MyMarker(map);
                                    marker.setPosition(new GeoPoint(document.getGeoPoint("maplocation").getLatitude()
                                            , document.getGeoPoint("maplocation").getLongitude()));
                                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                    marker.setIcon(drawable);
                                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                    marker.setId("marker");
                                    marker.setTitle(document.getString("markername"));
                                    marker.setSnippet(document.getString("address") + ", " + document.getString("barangay"));

                                    listcomparator.add(document.getGeoPoint("maplocation").toString());

                                    marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker, MapView mapView) {
                                            mClicked.add(marker);
                                            for (String member : listcomparator) {
                                                Log.i("geopoints: ", member);
                                            }
                                            return false;
                                        }
                                    });
                                    map.getOverlays().add(marker);
                                    map.invalidate();
                                } catch (Exception ignore) {
                                    listcomparator.clear();
                                }

                            }
                        }
                    }, 500);
                }
            }
        });
    }

    public void showProgressDialog(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.progress_layout, null,false);
        builder.setView(v);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    public void hideProgressDialog(){
        dialog.dismiss();
    }
}