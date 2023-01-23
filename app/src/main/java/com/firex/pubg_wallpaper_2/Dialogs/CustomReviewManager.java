package com.firex.pubg_wallpaper_2.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firex.pubg_wallpaper_2.BuildConfig;
import com.firex.pubg_wallpaper_2.R;
import com.firex.pubg_wallpaper_2.Utilities.PrefManager;


public class CustomReviewManager extends Dialog {
    private final String TAG = "ReviewManager";
    private final PrefManager pref;
    private static boolean alreadyStarted = false;

    public CustomReviewManager(Context context) {
        super(context);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(params);

        setTitle(null);
        setOnCancelListener(null);
        pref = PrefManager.getInstance(context);
        View view = LayoutInflater.from(context).inflate(R.layout.rating_dialog, null);

        RelativeLayout rateUsBtn = view.findViewById(R.id.rate_btn);
        rateUsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                pref.putBoolean("isAlreadyRated", true);
                playStore(context);
            }
        });

        setContentView(view);
    }

    public void showIfNotRated() {
        if (alreadyStarted){
            Log.i(TAG, "showIfNotRated: Already Started!");
            return;
        }

        alreadyStarted = true;


        int session = pref.getInt("rate_session");

        pref.putInt("rate_session", ++session);

        boolean isAlreadyRated = pref.getBoolean("isAlreadyRated", false);

        if (isAlreadyRated) {
            Log.i(TAG, "showIfNotRated: Already Rated!");
            return;
        }

        Log.i(TAG, "showIfNotRated: session: " + session);

        if (session % 2 == 0) {
            show();
        }

    }

    public void playStore(Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
        } catch (android.content.ActivityNotFoundException anfe) {
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            Log.i("More Apps Error", anfe.getLocalizedMessage());
            anfe.printStackTrace();
        }
    }

}
