package com.capstone.e_waste_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

import com.capstone.e_waste_manager.model.DisposalModel;

public class GoogleMapView extends AppCompatActivity {

    WebView googlemaps;
    Double latitude1 = 0.0, longitude1 = 0.0, latitude2 = 0.0, longitude2 = 0.0;
    ImageButton closeup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map_view);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        googlemaps = findViewById(R.id.googlemaps);
        closeup = findViewById(R.id.closeup);
        googlemaps.getSettings().setJavaScriptEnabled(true);

        WebSettings settings = googlemaps.getSettings();
        settings.setDomStorageEnabled(true);

        Intent intent = getIntent();
        latitude1 = intent.getDoubleExtra("latitude1", 0);
        longitude1 = intent.getDoubleExtra("longitude1", 0);
        latitude2 = intent.getDoubleExtra("latitude2", 0);
        longitude2 = intent.getDoubleExtra("longitude2", 0);


        googlemaps.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (URLUtil.isNetworkUrl(url)){
                    return false;
                }
                if (appInstalledOrNot(url)){
                    Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }

                return true;

            }
        });
        googlemaps.loadUrl("https://www.google.com/maps/dir/"+latitude1+','+longitude1+'/'+latitude2+','+longitude2);

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

}