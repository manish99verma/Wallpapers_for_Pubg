package com.firex.pubg_wallpaper_2.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firex.pubg_wallpaper_2.BuildConfig;
import com.firex.pubg_wallpaper_2.R;
import com.firex.pubg_wallpaper_2.Utilities.Utils;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;

import java.util.UUID;

public class RateDialog extends Dialog {
    private final String TAG = "RatingDialogTAGG";
    private SharedPreferences prefs;
    private ReviewInfo reviewInf;
    private ReviewManager reviewManager;
    private Activity mActivity;
    private RatingBar rateBar;
    private ImageView crossBtn;
    private boolean opensGooglePlay = true;
    private Context mContext;

    public void shouldOpenInAppReview() {
        opensGooglePlay = false;
    }

    public void showIfNotRated() {
        boolean isAlreadyRated = prefs.getBoolean("isAlreadyRated", false);
        if (!isAlreadyRated)
            show();
    }

    private void activateReviewInfo() {
        reviewManager = ReviewManagerFactory.create(mContext);
        Task<ReviewInfo> managerInfoTask = reviewManager.requestReviewFlow();

        long sentTime = System.currentTimeMillis();
        managerInfoTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                reviewInf = task.getResult();
                Log.i(TAG, "activateReviewInfo: time taken: " + (System.currentTimeMillis() - sentTime));
            } else {
                Log.i(TAG, "activateReviewInfo: failed to start");
            }
        });
    }

    public void startReviewFlow() {
        if (reviewInf != null) {
            Task<Void> flow = reviewManager.launchReviewFlow(mActivity, reviewInf);

            flow.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    prefs.edit().putBoolean("isAlreadyRated", true).apply();

                    if (!task.isSuccessful()) {
                        Log.i(TAG, "onComplete: failed to launch in app review!");
                        playStore(mContext);
                    } else {
                        Log.i(TAG, "onComplete: opened in app review!");
                    }
                }
            });

        }
    }

    public RateDialog(@NonNull Context context) {
        super(context);

        this.mContext = context;
        this.mActivity = (Activity) context;

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(params);

        prefs = context.getSharedPreferences("RatingDialog", Context.MODE_PRIVATE);

        setTitle(null);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(R.layout.rate_us_dialog, null);

        rateBar = view.findViewById(R.id.ratingBar);
        crossBtn = view.findViewById(R.id.cut_btn);

        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.ImageClickEffect(view);
                cancel();
            }
        });

        rateBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                cancel();
                Log.i(TAG, "onRatingChanged: " + v);
                openPlayStore();
            }
        });

        activateReviewInfo();

        setContentView(view);
    }

    public void openPlayStore() {
        if (reviewInf == null)
            Log.i(TAG, "openPlayStore: null review flow");
        if (!opensGooglePlay)
            Log.i(TAG, "openPlayStore: shold open in app review!");

        if (!opensGooglePlay && reviewInf != null) {
            startReviewFlow();
        } else {
            playStore(mContext);
            prefs.edit().putBoolean("isAlreadyRated", true).apply();
        }
    }

    public static void playStore(Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
        } catch (android.content.ActivityNotFoundException anfe) {
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            Log.i("More Apps Error", anfe.getLocalizedMessage());
            anfe.printStackTrace();
        }
    }

    public void openFeedback() {
        UserReviewDialog dialog = new UserReviewDialog(mContext);
        dialog.setDeviceid(getDeviceId());
        dialog.show();
    }

    public String getDeviceId() {
        String deviceId = prefs.getString("deviceId", null);
        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString();
            prefs.edit().putString("deviceId", deviceId).apply();
        }
        return "Device:" + deviceId;
    }

}