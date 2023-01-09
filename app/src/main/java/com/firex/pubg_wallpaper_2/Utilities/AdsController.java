package com.firex.pubg_wallpaper_2.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.firex.pubg_wallpaper_2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;

import java.util.concurrent.Executor;

public class AdsController {
    public static int adCounter = 1;
    public static int adDisplayCounter = 7;
    private static Intent intentToOpen;
    private static int requestCodeToOpen;
    private static final Activity[] contextsToOpen = new Activity[1];
    private static final IronSourceBannerLayout[] banner = new IronSourceBannerLayout[1];
    private static boolean isActivated = true;

    public static void initAd(Context context) {
        final boolean[] responseReceived = {false};

        long sentTime = System.currentTimeMillis();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("showAds");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                responseReceived[0] = true;
                long timeTaken = System.currentTimeMillis() - sentTime;
                if (timeTaken < 3000) {
                    if (snapshot.getValue() != null && snapshot.getValue().equals(false)) {
                        Log.i("isActivated", "onDataChange: No ads!");
                        isActivated = false;
                    } else {
                        Log.i("isActivated", "onDataChange: Null/True Response!");
                        IronSource.init((Activity) context, context.getString(R.string.iron_source_app_key), IronSource.AD_UNIT.OFFERWALL, IronSource.AD_UNIT.INTERSTITIAL, IronSource.AD_UNIT.REWARDED_VIDEO, IronSource.AD_UNIT.BANNER);
                        AdsController.loadInterAd(context);
                    }
                } else {
                    Log.i("isActivated", "onDataChange: Late Response! Do Nothing");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("isActivated", "onDataChange: Error Occurred in getting value!");
                IronSource.init((Activity) context, context.getString(R.string.iron_source_app_key), IronSource.AD_UNIT.OFFERWALL, IronSource.AD_UNIT.INTERSTITIAL, IronSource.AD_UNIT.REWARDED_VIDEO, IronSource.AD_UNIT.BANNER);
                AdsController.loadInterAd(context);
            }
        });

        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!responseReceived[0]){
                    Log.i("isActivated", "run: Time out!");
                    Executor main = ContextCompat.getMainExecutor(context);
                    main.execute(() -> {
                        IronSource.init((Activity) context, context.getString(R.string.iron_source_app_key), IronSource.AD_UNIT.OFFERWALL, IronSource.AD_UNIT.INTERSTITIAL, IronSource.AD_UNIT.REWARDED_VIDEO, IronSource.AD_UNIT.BANNER);
                        AdsController.loadInterAd(context);
                    });
                }

            }
        }.start();
    }

    public static void loadBannerAd(Activity context, FrameLayout adContainer) {
//        if (!isActivated) {
//            Log.i("Activated", "Not Activated");
//            return;
//        }
//
//        Log.i("Ads", "loadBannerAd: started loading ");
//        IronSource.destroyBanner(banner[0]);
//
//        banner[0] = IronSource.createBanner(context, ISBannerSize.SMART);
//
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT);
//        adContainer.addView(banner[0], 0, layoutParams);
//
//
//        banner[0].setBannerListener(new BannerListener() {
//            @Override
//            public void onBannerAdLoaded() {
//                // Called after a banner ad has been successfully loaded
//                Log.i("Ads", "onBannerAdLoaded: ");
//            }
//
//            @Override
//            public void onBannerAdLoadFailed(IronSourceError error) {
//                // Called after a banner has attempted to load an ad but failed.
//                Log.i("Ads", "onBannerAdLoadFailed: " + error.getErrorMessage());
//
//                context.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        adContainer.removeAllViews();
//                    }
//                });
//            }
//
//            @Override
//            public void onBannerAdClicked() {
//                // Called after a banner has been clicked.
//                Log.i("Ads", "onBannerAdClicked: ");
//            }
//
//            @Override
//            public void onBannerAdScreenPresented() {
//                // Called when a banner is about to present a full screen content.
//                Log.i("Ads", "onBannerAdScreenPresented: ");
//            }
//
//            @Override
//            public void onBannerAdScreenDismissed() {
//                // Called after a full screen content has been dismissed
//                Log.i("Ads", "onBannerAdScreenDismissed: ");
//            }
//
//            @Override
//            public void onBannerAdLeftApplication() {
//                // Called when a user would be taken out of the application context.
//                Log.i("Ads", "onBannerAdLeftApplication: ");
//            }
//        });
//
//
//        IronSource.loadBanner(banner[0]);
    }

    public static void loadInterAd(Context context) {
        if (!isActivated) {
            Log.i("Activated", "Not Activated");
            return;
        }

        Log.i("Ads", "loadInterAd: loading inter");
        IronSource.setInterstitialListener(new InterstitialListener() {
            /**
             * Invoked when Interstitial Ad is ready to be shown after load function was called.
             */
            @Override
            public void onInterstitialAdReady() {
                Log.i("Ads", "onInterstitialAdReady: ");
            }

            /**
             * invoked when there is no Interstitial Ad available after calling load function.
             */
            @Override
            public void onInterstitialAdLoadFailed(IronSourceError error) {
                Log.i("Ads", "onInterstitialAdLoadFailed: " + error.getErrorMessage());
            }

            /**
             * Invoked when the Interstitial Ad Unit is opened
             */
            @Override
            public void onInterstitialAdOpened() {
                Log.i("Ads", "onInterstitialAdOpened: ");
            }

            /*
             * Invoked when the ad is closed and the user is about to return to the application.
             */
            @Override
            public void onInterstitialAdClosed() {
                Log.i("Ads", "onInterstitialAdClosed: ");
                openActivity();
                IronSource.loadInterstitial();
            }

            /**
             * Invoked when Interstitial ad failed to show.
             * @param error - An object which represents the reason of showInterstitial failure.
             */
            @Override
            public void onInterstitialAdShowFailed(IronSourceError error) {
                Log.i("Ads", "onInterstitialAdShowFailed: ");
                openActivity();
                IronSource.loadInterstitial();
            }

            /*
             * Invoked when the end user clicked on the interstitial ad, for supported networks only.
             */
            @Override
            public void onInterstitialAdClicked() {
                Log.i("Ads", "onInterstitialAdClicked: ");
            }

            /** Invoked right before the Interstitial screen is about to open.
             *  NOTE - This event is available only for some of the networks.
             *  You should NOT treat this event as an interstitial impression, but rather use InterstitialAdOpenedEvent
             */
            @Override
            public void onInterstitialAdShowSucceeded() {
                Log.i("Ads", "onInterstitialAdShowSucceeded: ");
            }
        });

        IronSource.loadInterstitial();
    }

    private static void openActivity() {
        startActivity(contextsToOpen[0], intentToOpen, requestCodeToOpen);
    }

    public static void showInterAd(final Activity context, final Intent intent, final int requstCode) {
        if (adCounter == adDisplayCounter && IronSource.isInterstitialReady()) {
            adCounter = 1;

            contextsToOpen[0] = context;
            requestCodeToOpen = requstCode;
            intentToOpen = intent;

            IronSource.showInterstitial();
        } else {
            if (adCounter == adDisplayCounter) {
                adCounter = 1;
            }
            startActivity(context, intent, requstCode);
        }
    }

    static void startActivity(Activity context, Intent intent, int requestCode) {
        if (intent != null) {
            context.startActivityForResult(intent, requestCode);
        }
    }

    public static void showInterAd(final Fragment context, final Intent intent, final int requstCode) {
        if (adCounter == adDisplayCounter && IronSource.isInterstitialReady()) {
            adCounter = 1;

            contextsToOpen[0] = context.getActivity();
            requestCodeToOpen = requstCode;
            intentToOpen = intent;

            IronSource.showInterstitial();
        } else {
            if (adCounter == adDisplayCounter) {
                adCounter = 1;
            }
            startActivity(context, intent, requstCode);
        }
    }

    static void startActivity(Fragment context, Intent intent, int requestCode) {
        if (intent != null) {
            context.startActivityForResult(intent, requestCode);
        }
    }

}

