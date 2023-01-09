package com.firex.pubg_wallpaper_2.Utilities;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.firex.pubg_wallpaper_2.BuildConfig;
import com.firex.pubg_wallpaper_2.Models.WallpaperModel;
import com.firex.pubg_wallpaper_2.R;

import java.util.ArrayList;
import java.util.Random;

public class Utils {

    public static void clickEffect(View view) {
        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);
        view.startAnimation(buttonClick);
    }

    public static void ImageClickEffect(View view) {
        view.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(() -> view.setVisibility(View.VISIBLE), 75);
    }

    public static void changeFragment(FragmentActivity fragmentActivity, Fragment fragment, int continer_Id) {
        FragmentTransaction fragmentTransaction =
                fragmentActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(continer_Id, fragment);
        fragmentTransaction.commit();
    }

    public static boolean isNetworkAvailable(Application application) {
        ConnectivityManager connectivityManager = (ConnectivityManager) application
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);

            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));

        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void setOnTouchListener(View view) {
        view.setOnTouchListener((v, event) -> {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    view.setAlpha(0.7f);
                    break;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: {
                    view.setAlpha(1);
                    break;
                }
            }

            return false;
        });
    }

    public static ArrayList<WallpaperModel> randomizeArraylist(ArrayList<WallpaperModel> list) {
        long startTime = System.currentTimeMillis();
        int listSize = list.size();
        if (listSize == 0) {
            return list;
        }

        ArrayList<WallpaperModel> resultList = new ArrayList<>();
        Random random = new Random();

        while (resultList.size() != listSize) {
            //Get a Random Wallpaper
            int randomNumber = random.nextInt(list.size());

            WallpaperModel randomWallpaper = list.get(randomNumber);
            resultList.add(randomWallpaper);
            list.remove(randomWallpaper);

        }

        return resultList;

    }

    public static void moreApps(Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Dreamx")));
        } catch (android.content.ActivityNotFoundException anfe) {
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            Log.i("More Apps Error", anfe.getLocalizedMessage());
            anfe.printStackTrace();
        }
    }

    public static void shareApp(Context context) {
        try {
            String msg = "Get Hundreds of Cool " + context.getResources().getString(R.string.app_name);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, Configuration.appFolderName);
            String shareMessage = "\n" + msg + "\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
