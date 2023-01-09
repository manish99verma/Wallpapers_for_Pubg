package com.firex.pubg_wallpaper_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.firex.pubg_wallpaper_2.Fragments.WelFragment1;
import com.firex.pubg_wallpaper_2.Utilities.Utils;

public class Wel_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Utils.changeFragment(this,new WelFragment1(),R.id.fragContainerWelcome);
    }



}