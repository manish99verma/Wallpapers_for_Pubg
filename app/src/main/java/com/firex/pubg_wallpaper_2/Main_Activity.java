package com.firex.pubg_wallpaper_2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.firex.pubg_wallpaper_2.Dialogs.UpdateDialog;
import com.firex.pubg_wallpaper_2.Fragments.DownloadFragment;
import com.firex.pubg_wallpaper_2.Fragments.MainFragment;
import com.firex.pubg_wallpaper_2.Utilities.AdsController;
import com.firex.pubg_wallpaper_2.Utilities.Utils;
import com.firex.pubg_wallpaper_2.databinding.ActivityMainBinding;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ironsource.mediationsdk.IronSource;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Main_Activity extends AppCompatActivity {
    private static final int STORAGE_REQ_X = 11132;
    boolean isinhome;
    private SharedPreferences prefs;
    private MainFragment mainFragment;

    ActivityMainBinding binding;
    private final String TAG = "MainActivityTTT";
    Activity activity;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Initializations
        activity = this;
        context = this;

        prefs = getSharedPreferences("MainActivityPref", MODE_PRIVATE);

        binding.icHome.setOnClickListener(view -> {
            Utils.clickEffect(view);
            changeFragment(true);
        });

        binding.icDownload.setOnClickListener(view -> {
            Utils.clickEffect(view);
            changeFragment(false);
        });

        changeFragment(true);

        //Clear temp files
        new Thread(this::deleteTempFiles).start();

        //Uploading data
//        DatabaseManager databaseManager = new DatabaseManager(this);

        checkForUpdates();

        //Ads
//        IronSource.init(this, getResources().getString(R.string.iron_source_app_key), IronSource.AD_UNIT.INTERSTITIAL);
//        IronSource.init(this, getResources().getString(R.string.iron_source_app_key), IronSource.AD_UNIT.BANNER);
        AdsController.initAd(this);
    }

    private void checkForUpdates() {
        int ses = prefs.getInt("ses", 0);
        prefs.edit().putInt("ses", ++ses).apply();
        String currentVer = BuildConfig.VERSION_NAME;

        if (ses % 2 != 0)
            return;

        try {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("playStoreVersion");
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() == null || snapshot.getValue().toString().isEmpty()) {
                        Log.i(TAG, "onDataChange: LatestVersion not found!");
                    } else if (!snapshot.getValue().toString().equals(currentVer)) {

                        Log.i(TAG, "onDataChange: A newer version found!");

                        new UpdateDialog(Main_Activity.this).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.i(TAG, "onDataChange: LatestVersion not found!");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void deleteTempFiles() {
        try {
            //Deleting before crop
            File myTempFolder = new File(getCacheDir().getAbsolutePath() + "/MyTempFolder/");
            File[] files = myTempFolder.listFiles();

            if (files != null) {
                for (File aFile : files) {
                    //noinspection ResultOfMethodCallIgnored
                    aFile.delete();
                }
            }

            //Deleting after crop
            File cachesFolder = getCacheDir();
            File[] filesInCaches = cachesFolder.listFiles();
            if (filesInCaches != null) {
                for (File aFile : filesInCaches) {
                    String name = aFile.getName();
                    if (name.startsWith("cropped")) {
                        //noinspection ResultOfMethodCallIgnored
                        aFile.delete();
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void changeFragment(boolean isHome) {
        if (isHome) {
            if (Utils.isNetworkAvailable(getApplication())) {
                mainFragment = new MainFragment();
                Utils.changeFragment(this, mainFragment, R.id.fragmentContainerMainActivity);
                changeNavigationIcon(true);
            } else if (givenStoragePermissions()) {
                Toast.makeText(this, "No Internet!", Toast.LENGTH_SHORT).show();
                Utils.changeFragment(this, new DownloadFragment(), R.id.fragmentContainerMainActivity);
                changeNavigationIcon(false);
            } else {
                requestPermissions();
            }
        } else if (givenStoragePermissions()) {
            Utils.changeFragment(this, new DownloadFragment(), R.id.fragmentContainerMainActivity);
            changeNavigationIcon(false);
        } else {
            requestPermissions();
        }
    }

    private void changeNavigationIcon(boolean isHome) {
        isinhome = isHome;
        if (isHome) {
            binding.IVHome.setImageResource(R.drawable.home_selected);
            binding.IVDownload.setImageResource(R.drawable.downloads);
        } else {
            binding.IVHome.setImageResource(R.drawable.home);
            binding.IVDownload.setImageResource(R.drawable.downloads_selected);
        }
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT < 23)
            return;

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT > 28)
            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

        ActivityCompat.requestPermissions(activity, permissions, STORAGE_REQ_X);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (givenStoragePermissions()) {
            changeFragment(false);
        }
    }

    private boolean givenStoragePermissions() {
        if (Build.VERSION.SDK_INT < 23)
            return true;

        if (Build.VERSION.SDK_INT <= 28) {
            return (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    && (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        }

        return (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

    }

    @Override
    public void onBackPressed() {
        if (isinhome) {
            super.onBackPressed();
        } else {
            changeFragment(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IronSource.onResume(this);

        Log.i("Resume", "onResume: Activity");

        if (mainFragment != null && mainFragment.context == null) {
            Log.i("Resume", "onResume: Restarting the Home fragment!");
            changeFragment(true);
        }
    }

    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }

}