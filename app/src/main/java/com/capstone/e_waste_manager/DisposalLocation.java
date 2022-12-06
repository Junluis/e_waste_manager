package com.capstone.e_waste_manager;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.airbnb.lottie.utils.LogcatLogger;
import com.capstone.e_waste_manager.model.DisposalModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
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
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphFloatingActionButton;

public class DisposalLocation extends AppCompatActivity implements LocationListener{

    MapView map = null;
    NeumorphFloatingActionButton myLocation;
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

    ImageView defaultmap, aerialmap, bingmap;

    SnapHelper snapHelper;

    private final List<Marker> mClicked = new ArrayList<>();
    private final List<String> listcomparator = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        Query query = fStore.collection("DisposalLocations")
                .orderBy("barangay", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<DisposalModel> options = new FirestoreRecyclerOptions.Builder<DisposalModel>()
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
                }catch (Exception e){
                    Toast.makeText(ctx, "there are currently no disposal areas.", Toast.LENGTH_SHORT).show();
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
                TextView lat = viewHolder.itemView.findViewById(R.id.latitude);
                TextView lon = viewHolder.itemView.findViewById(R.id.longitude);

                zoom.setChecked(false);
                double latitude = Double.parseDouble(lat.getText().toString());
                double longitude = Double.parseDouble(lon.getText().toString());

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    rrl.animate().setDuration(350).scaleX(1).scaleY(1).setInterpolator(new AccelerateInterpolator()).start();
                    map.getController().animateTo(new GeoPoint(latitude, longitude),14.0, 2000L);
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

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {

                for(int i=0; i < map.getOverlays().size();i++){
                    Overlay overlay=map.getOverlays().get(i);
                    if(overlay instanceof Marker&&((Marker) overlay).getId().equals("marker")){
                        map.getOverlays().remove(overlay);
                        i = 0;
                    }
                }

                listcomparator.clear();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            final Drawable drawable = getResources().getDrawable(R.drawable.disposal);
                            final Marker marker = new MyMarker(map);
                            marker.setPosition(new GeoPoint(document.getGeoPoint("maplocation").getLatitude()
                                    , document.getGeoPoint("maplocation").getLongitude()));
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            marker.setIcon(drawable);
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            marker.setId("marker");
                            marker.setTitle("test");
                            marker.setSnippet(document.getString("address") + ", " + document.getString("barangay"));

                            listcomparator.add(document.getGeoPoint("maplocation").toString());

                            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker, MapView mapView) {
                                    mClicked.add(marker);
                                    for (String member : listcomparator){
                                        Log.i("geopoints: ", member);
                                    }
                                    return false;
                                }
                            });
                            map.getOverlays().add(marker);
                            map.invalidate();
                        }
                    }
                },500);
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView disposalname, disposaladdress, latitude, longitude;
        ImageView disposalicon;
        MaterialButton track;
        ToggleButton zoom;

        DisposalModel model;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            disposalname = itemView.findViewById(R.id.disposalname);
            disposaladdress = itemView.findViewById(R.id.disposaladdress);
            latitude = itemView.findViewById(R.id.latitude);
            longitude = itemView.findViewById(R.id.longitude);
            disposalicon = itemView.findViewById(R.id.disposalicon);
            track = itemView.findViewById(R.id.track);
            zoom = itemView.findViewById(R.id.zoom);

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
        }

        public void bind(DisposalModel disposalModel){
            model = disposalModel;
            disposalname.setText(disposalModel.markername);
            disposaladdress.setText(disposalModel.getAddress() + ", " + disposalModel.barangay);
            latitude.setText(""+disposalModel.maplocation.getLatitude());
            longitude.setText(""+disposalModel.maplocation.getLongitude());

//           image per markers
            StorageReference profileRef = storageReference.child("MapMarkers/"+ disposalModel.docId +"/marker.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(disposalicon);
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

        int result = Collections.binarySearch(listcomparator,geodata);
        if (result == -1)
            Toast.makeText(this, "Something went worng. Please try to restart.", Toast.LENGTH_SHORT).show();
        else {
            linearSmoothScroller.setTargetPosition(result);
            linearLayoutManager.startSmoothScroll(linearSmoothScroller);
        }
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
}