package com.capstone.e_waste_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;

import soup.neumorphism.NeumorphCardView;

public class MainActivity extends AppCompatActivity {

    ImageView splashImg;
    LottieAnimationView lottie;
    NeumorphCardView lottieframe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //transparent status
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        splashImg = findViewById(R.id.bg);
        lottie = findViewById(R.id.lottie);
        lottieframe = findViewById(R.id.lottieframe);

        splashImg.animate().translationY(-3000F).setDuration(1000).setStartDelay(1000);
        lottie.animate().translationY(-3000F).setDuration(1000).setStartDelay(3000);
        lottieframe.animate().translationY(-3000F).setDuration(1000).setStartDelay(3000).withEndAction(new Runnable() {
            @Override
            public void run() {
                if (isNetworkAvailable()){
                    startActivity(new Intent(getApplicationContext(), Home.class));
                } else{
                    startActivity(new Intent(getApplicationContext(), NoConnection.class));
                }
                finish();
            }
        });
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}