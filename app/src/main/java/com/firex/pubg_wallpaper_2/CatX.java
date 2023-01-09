package com.firex.pubg_wallpaper_2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firex.pubg_wallpaper_2.Adapters.CatImageAdapter;
import com.firex.pubg_wallpaper_2.Models.WallpaperModel;
import com.firex.pubg_wallpaper_2.Utilities.AdsController;
import com.firex.pubg_wallpaper_2.Utilities.DataTransfer;
import com.firex.pubg_wallpaper_2.Utilities.Utils;
import com.firex.pubg_wallpaper_2.databinding.ActivityCategoryXBinding;
import com.ironsource.mediationsdk.IronSource;

import java.util.ArrayList;

public class CatX extends AppCompatActivity {
    private static final String TAG = "CategoryXTAG";
    ActivityCategoryXBinding binding;
    private String title;
    boolean isFormMore = false;
//    private IronSourceBannerLayout banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryXBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        title = getIntent().getStringExtra("title");
        isFormMore = getIntent().getBooleanExtra("isFromMore", false);
        setTitle(title);

        binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());
//        getCategoryXData(Source.CACHE);
        getCategoryXData(getIntent());


//        loadBanner();
    }

    private void getCategoryXData(Intent intent) {
//        if (source == Source.CACHE) {
//            Log.i(TAG, "getCategoryXData: Getting Data from caches!");
//        } else {
//            Log.i(TAG, "getCategoryXData: Getting Data from server!");
//        }
//
//        db.collection("Categories_101")
//                .document(title)
//                .collection("Wallpapers")
//                .get(source).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
////                List<Wallpaper> data = task.getResult().toObjects(Wallpaper.class);
////                ArrayList<Wallpaper> listWallpapers = new ArrayList<>(data);
//
//                new Thread() {
//                    @Override
//                    public void run() {
//                        ArrayList<Wallpaper> list = new ArrayList<>();
//
//                        int count = 0;
//
//                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
//                            count++;
//                            Wallpaper wallpaper = new Wallpaper(
//                                    snapshot.getString("sUrl")
//                                    , snapshot.getString("mUrl"));
//                            list.add(wallpaper);
//                        }
//
//                        final ArrayList<Wallpaper> finalList= Utility.randomizeArraylist(list);
//
//                        final int finalCount = count;
//                        runOnUiThread(() -> {
//                            if (finalCount == 0) {
//                                Log.i(TAG, "run: unable to get CategoryX data:  ");
//                                getCategoryXData(Source.SERVER);
//                                return;
//                            } else {
//                                Log.i(TAG, "run: got CategoryX data: " + finalCount);
//                            }
//
//                            String subtitle = finalList.size() + " wallpapers";
//                            binding.toolbar.setSubtitle(subtitle);
//
//                            CatImagesAdapter adapter = new CatImagesAdapter(CategoryX.this, finalList);
//                            StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2,
//                                    StaggeredGridLayoutManager.VERTICAL);
//                            binding.RVCategoryX.setLayoutManager(manager);
//                            binding.PBCategoryX.setVisibility(View.INVISIBLE);
//                            binding.RVCategoryX.setAdapter(adapter);
//                        });
//
//                    }
//                }.start();
//
//            }
//        });

        new Thread() {
            @Override
            public void run() {
                ArrayList<String> keysLists = DataTransfer.getsList();
                ArrayList<String> valuesList = DataTransfer.getMainList();

                if (keysLists.size() < 1 || valuesList.size() < 1) {
                    runOnUiThread(() -> onBackPressed());
                    return;
                }

                ArrayList<WallpaperModel> list = new ArrayList<>();

                for (int i = 0; i < keysLists.size(); i++) {
                    keysLists.size();
                    if (valuesList.size() < 1) {
                        runOnUiThread(() -> onBackPressed());
                        return;
                    }
                    String key = keysLists.get(i);
                    String value = valuesList.get(i);
                    list.add(new WallpaperModel(key, value));
                }

                boolean listedOnDB = true;
                ArrayList<WallpaperModel> randomizedArraylist = Utils.randomizeArraylist(list);
                ArrayList<WallpaperModel> finalList = new ArrayList<>();

                if (isFormMore) {
                    listedOnDB = false;
                    if (randomizedArraylist.size() > 250) {
                        for (int i = 0; i < 200; i++) {
                            finalList.add(randomizedArraylist.get(i));
                        }
                    } else {
                        finalList = randomizedArraylist;
                    }
                } else {
                    finalList = randomizedArraylist;
                }


                boolean finalListedOnDB = listedOnDB;
                ArrayList<WallpaperModel> finalList1 = finalList;
                runOnUiThread(() -> {
                    String subtitle = finalList1.size() + " wallpaper";
                    binding.toolbar.setSubtitle(subtitle);

                    CatImageAdapter adapter = new CatImageAdapter(CatX.this, finalList1, finalListedOnDB);
                    StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2,
                            StaggeredGridLayoutManager.VERTICAL);
                    binding.RVCategoryX.setLayoutManager(manager);
                    binding.PBCategoryX.setVisibility(View.INVISIBLE);
                    binding.RVCategoryX.setAdapter(adapter);
                });

            }
        }.start();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        if (banner != null)
//            IronSource.destroyBanner(banner);
        finish();
    }

//    private void loadBanner() {
//        banner = IronSource.createBanner(this, ISBannerSize.BANNER);
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT);
//        binding.bannerContainer.addView(banner, 0, layoutParams);
//        banner.setBannerListener(new BannerListener() {
//            @Override
//            public void onBannerAdLoaded() {
//                Log.i(TAG, "onBannerAdLoaded: ");
//                // Called after a banner ad has been successfully loaded
//            }
//
//            @Override
//            public void onBannerAdLoadFailed(IronSourceError error) {
//                Log.i(TAG, "onBannerAdLoadFailed: " + error.getErrorMessage());
//                Log.i(TAG, "onBannerAdLoadFailed: " + error.toString());
//                // Called after a banner has attempted to load an ad but failed.
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        binding.bannerContainer.removeAllViews();
//                    }
//                });
//            }
//
//            @Override
//            public void onBannerAdClicked() {
//                Log.i(TAG, "onBannerAdClicked: ");
//                // Called after a banner has been clicked.
//            }
//
//            @Override
//            public void onBannerAdScreenPresented() {
//                Log.i(TAG, "onBannerAdScreenPresented: ");
//                // Called when a banner is about to present a full screen content.
//            }
//
//            @Override
//            public void onBannerAdScreenDismissed() {
//                Log.i(TAG, "onBannerAdScreenDismissed: ");
//                // Called after a full screen content has been dismissed
//            }
//
//            @Override
//            public void onBannerAdLeftApplication() {
//                Log.i(TAG, "onBannerAdLeftApplication: ");
//                // Called when a user would be taken out of the application context.
//            }
//        });
//
//        IronSource.loadBanner(banner);
//
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (banner != null)
//            IronSource.destroyBanner(banner);
    }

    protected void onResume() {
        super.onResume();
        IronSource.onResume(this);
        AdsController.loadBannerAd(this, binding.bannerContainerCategoryX);
    }

    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }

}