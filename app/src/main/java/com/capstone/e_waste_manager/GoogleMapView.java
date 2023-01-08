package com.capstone.e_waste_manager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

import com.capstone.e_waste_manager.model.DisposalModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GoogleMapView extends AppCompatActivity implements LocationListener {

    ActivityResultLauncher<String[]> mPermissionResultLauncher;
    private boolean isFineLocationPermissionGranted = false;
    private boolean isInternetPermissionGranted = false;
    private boolean isNetAccessPermissionGranted = false;

    private Location currentLocation = null;

    WebView googlemaps;
    Double latitude1 = 0.0, longitude1 = 0.0, latitude2, longitude2;
    ImageButton closeup;

    boolean isloaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map_view);

        mPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {

                if(result.get(Manifest.permission.ACCESS_FINE_LOCATION) != null){
                    isFineLocationPermissionGranted = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION));
                }
                if(result.get(Manifest.permission.INTERNET) != null){
                    isInternetPermissionGranted = Boolean.TRUE.equals(result.get(Manifest.permission.INTERNET));
                }
                if(result.get(Manifest.permission.ACCESS_NETWORK_STATE) != null){
                    isNetAccessPermissionGranted = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_NETWORK_STATE));
                }

            }
        });

        googlemaps = findViewById(R.id.googlemaps);
        closeup = findViewById(R.id.closeup);
        googlemaps.getSettings().setJavaScriptEnabled(true);

        WebSettings settings = googlemaps.getSettings();
        settings.setDomStorageEnabled(true);

        Intent intent = getIntent();
        latitude1 = intent.getDoubleExtra("latitude1", 0);
        longitude1 = intent.getDoubleExtra("longitude1", 0);


        closeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    boolean appInstalledOrNot(String url){
        PackageManager pm = getPackageManager();
        try{
            pm.getPackageInfo(url,PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
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

        if (!permissionRequest.isEmpty()){
            mPermissionResultLauncher.launch(permissionRequest.toArray(new String[0]));
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        currentLocation = location;

        latitude2 = currentLocation.getLatitude();
        longitude2 = currentLocation.getLongitude();

        if (!isloaded){
            googlemaps.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (URLUtil.isNetworkUrl(url)){
                        return false;
                    }
                    if (appInstalledOrNot(url)){
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }

                    return true;

                }
            });

            googlemaps.loadUrl("https://www.google.com/maps/dir/"+latitude1+','+longitude1+'/'+latitude2+','+longitude2);
        }

        isloaded = true;
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
    }

    public void onPause(){
        super.onPause();
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        lm.removeUpdates(this);
    }

}