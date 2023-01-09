package com.firex.pubg_wallpaper_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class Splash_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            SharedPreferences pref = this.getSharedPreferences("SplashActivity", MODE_PRIVATE);

            boolean welcomeScreenSeen = pref.getBoolean("welcomeScreenSeen", false);

            Intent intent;
            if (!welcomeScreenSeen) {
                intent = new Intent(Splash_Activity.this, Wel_Activity.class);
                pref.edit().putBoolean("welcomeScreenSeen", true).apply();
            } else {
                intent = new Intent(Splash_Activity.this, Main_Activity.class);
            }

            startActivity(intent);

        }, 1000);

    }

}