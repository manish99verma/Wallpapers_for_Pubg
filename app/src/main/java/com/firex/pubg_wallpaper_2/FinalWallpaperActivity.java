package com.firex.pubg_wallpaper_2;


import android.Manifest;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.firex.pubg_wallpaper_2.Adapters.FullSizeImageAdapter;
import com.firex.pubg_wallpaper_2.Dialogs.MyProgressDialog;
import com.firex.pubg_wallpaper_2.Utilities.AdsController;
import com.firex.pubg_wallpaper_2.Utilities.Configuration;
import com.firex.pubg_wallpaper_2.Utilities.DataTransfer;
import com.firex.pubg_wallpaper_2.Utilities.Dialog_Item;
import com.firex.pubg_wallpaper_2.Utilities.Utils;
import com.firex.pubg_wallpaper_2.databinding.ActivityFinalImageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ironsource.mediationsdk.IronSource;
import com.theartofdev.edmodo.cropper.CropImage;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class FinalWallpaperActivity extends AppCompatActivity {

    private final ArrayList<String> urlsList = new ArrayList<>();
    private ArrayList<String> sUrlsList = new ArrayList<>();
    private SharedPreferences pref;
    private boolean isFromDownloads;
    private Context context;
    //    private IronSourceBannerLayout banner;
    private boolean firstAdShown = false;
    private int pagesScrolled = 0;
    private boolean listedOnDB = true;

    private static final int STORAGE_REQ = 92384;
    ActivityFinalImageBinding binding;
    private final String TAG = "FinalImageActivityTAG";
    private int whichFeatureSelected = 0; //0-Save, 2-Share
    private int selectedForAd = 0;//0- Share,1-Download,2-Apply,3-first ad
    private final ArrayList<String> listSavedWallpapers = new ArrayList<>();
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFinalImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Setting Views & Status Bar
        transparentStatusAndNavigation();
        setMargins(binding.toolbar, 0, getStatusBarHeight(), 0, 0);
        queue = Volley.newRequestQueue(this);

        //Getting intent data
        Intent intent = getIntent();
        ArrayList<String> mainUrls = DataTransfer.getMainList();
        if (mainUrls.size() < 1) {
            onBackPressed();
            return;
        }
        int position = intent.getIntExtra("pos", 0);
        isFromDownloads = intent.getBooleanExtra("fromDownloads", false);
        listedOnDB = intent.getBooleanExtra("listedOnDB", true);
        Log.i(TAG, "onCreate: listedOnDb: " + listedOnDB);

        if (!isFromDownloads)
            sUrlsList = DataTransfer.getsList();

        //Initializations
        pref = this.getSharedPreferences("FinalImageActivity", MODE_PRIVATE);
        context = this;

        //Setting Adapter
        urlsList.add(mainUrls.get(position));

        FullSizeImageAdapter adapter = new FullSizeImageAdapter(TAG, urlsList, this);
        binding.VPagerFinal.setAdapter(adapter);

        //Move VPager to Clicked Image
        binding.VPagerFinal.post(() -> {
            //Front Urls
            if (position != 0) {
                for (int i = position - 1; i >= 0; i--) {
                    if (mainUrls.size() < 1) {
                        onBackPressed();
                        return;
                    }
                    String url = mainUrls.get(i);
                    urlsList.add(0, url);
                }
            }

            adapter.notifyItemRangeInserted(0, urlsList.size() - 1);

            //Next Urls
            if (position < (mainUrls.size() - 1)) {
                binding.VPagerFinal.post(() -> {
                    int tempSize = urlsList.size();
                    for (int i = position + 1; i < mainUrls.size(); i++) {
                        urlsList.add(mainUrls.get(i));
                    }

                    adapter.notifyItemRangeInserted(position + 1, urlsList.size() - tempSize);
                    mainUrls.clear();
                });
            }

        });

        binding.VPagerFinal.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                Log.i(TAG, "onPageSelected: " + position);
                pagesScrolled++;
                if (pagesScrolled > 2) {
                    AdsController.adCounter++;
                    AdsController.showInterAd(FinalWallpaperActivity.this, null, 0);
                }

            }
        });

        //Toolbar
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());
        setTitle("");

        //Download Wallpaper
        binding.downloadImage.setOnClickListener(view -> {
            Utils.setOnTouchListener(view);
//            if (IronSource.isInterstitialReady()) {
//                selectedForAd = 1;
//                IronSource.showInterstitial();
//                return;
//            }
            if (givenStoragePermissions()) {
                AdsController.adCounter = AdsController.adDisplayCounter;
                AdsController.showInterAd((Activity) context, null, 0);
            }

            String url = urlsList.get(binding.VPagerFinal.getCurrentItem());
            saveToGallery(url);
        });

        //Set Wallpaper
        binding.ApplyImage.setOnClickListener(view -> {
            Utils.setOnTouchListener(view);

//            if (IronSource.isInterstitialReady()) {
//                selectedForAd = 2;
//                IronSource.showInterstitial();
//                return;
//            }
            AdsController.adCounter = AdsController.adDisplayCounter;
            AdsController.showInterAd((Activity) context, null, 0);

            String url = urlsList.get(binding.VPagerFinal.getCurrentItem());

            String sUrl = "";
            if (!isFromDownloads) {
                int indexInMain = urlsList.indexOf(url);
                sUrl = sUrlsList.get(indexInMain);
            }

            onClickApplyButton(url, sUrl);
        });

        //Setting from downloads
        if (isFromDownloads)
            binding.downloadImage.setVisibility(View.GONE);

//        loadBanner();
//        loadInterstitialAd();
        AdsController.loadBannerAd(this, binding.bannerContainerFinalImage);

        subscribePushNotifiaction();
    }

//    private void loadInterstitialAd() {
//        IronSource.setInterstitialListener(new InterstitialListener() {
//            /**
//             * Invoked when Interstitial Ad is ready to be shown after load function was called.
//             */
//            @Override
//            public void onInterstitialAdReady() {
//                Log.i(TAG, "onInterstitialAdReady: ");
//                if (!firstAdShown) {
//                    firstAdShown = true;
//                    selectedForAd = 3;
//                    IronSource.showInterstitial();
//                }
//            }
//
//            /**
//             * invoked when there is no Interstitial Ad available after calling load function.
//             */
//            @Override
//            public void onInterstitialAdLoadFailed(IronSourceError error) {
//                Log.i(TAG, "onInterstitialAdLoadFailed: ");
//            }
//
//            /**
//             * Invoked when the Interstitial Ad Unit is opened
//             */
//            @Override
//            public void onInterstitialAdOpened() {
//                Log.i(TAG, "onInterstitialAdOpened: ");
//            }
//
//            /*
//             * Invoked when the ad is closed and the user is about to return to the application.
//             */
//            @Override
//            public void onInterstitialAdClosed() {
//                onAdClosed();
//            }
//
//            /**
//             * Invoked when Interstitial ad failed to show.
//             * @param error - An object which represents the reason of showInterstitial failure.
//             */
//            @Override
//            public void onInterstitialAdShowFailed(IronSourceError error) {
//                Log.i(TAG, "onInterstitialAdShowFailed: ");
//                onAdClosed();
//            }
//
//            /*
//             * Invoked when the end user clicked on the interstitial ad, for supported networks only.
//             */
//            @Override
//            public void onInterstitialAdClicked() {
//                Log.i(TAG, "onInterstitialAdClicked: ");
//            }
//
//            /** Invoked right before the Interstitial screen is about to open.
//             *  NOTE - This event is available only for some of the networks.
//             *  You should NOT treat this event as an interstitial impression, but rather use InterstitialAdOpenedEvent
//             */
//            @Override
//            public void onInterstitialAdShowSucceeded() {
//                Log.i(TAG, "onInterstitialAdShowSucceeded: ");
//            }
//        });
//
//        IronSource.loadInterstitial();
//    }

//    private void onAdClosed() {
//        Log.i(TAG, "onInterstitialAdClosed: ");
//        String url = urlsList.get(binding.VPagerFinal.getCurrentItem());
//        switch (selectedForAd) {
//            case 0:
//                shareImage(url);
//                break;
//            case 1:
//                saveToGallery(url);
//                break;
//            case 2:
//                String sUrl = "";
//                if (!isFromDownloads) {
//                    int indexInMain = urlsList.indexOf(url);
//                    sUrl = sUrlsList.get(indexInMain);
//                }
//
//                onClickApplyButton(url, sUrl);
//                break;
//        }
//        loadInterstitialAd();
//    }

    private void subscribePushNotifiaction() {
        FirebaseMessaging.getInstance().subscribeToTopic("updatesnewpubg")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Done";
                        if (!task.isSuccessful()) {
                            msg = "Failed";
                        }
                        Log.d(TAG, msg);

                    }
                });
    }

    private void saveToGallery(String url) {
        MyProgressDialog dialogClass = new MyProgressDialog(context);
        dialogClass.setMsg("Downloading...");

        Log.i(TAG, "saveToGallery: url: " + url);
        if (!givenStoragePermissions()) {
            Log.i(TAG, "saveToGallery: Permission not granted!");
            whichFeatureSelected = 0;
            requestPermissions();
            return;
        }

        if (listSavedWallpapers.contains(url) || listSavedWallpapers.size() > 9) {
            Toast.makeText(context,
                    "Wallpaper saved", Toast.LENGTH_SHORT).show();
            return;
        }

        ImageRequest imageRequest = new ImageRequest(url, bitmap -> {
            dialogClass.cancel();

            Log.i(TAG, "saveToGallery: downloaded!");
            String savedFilePath = pref.getString(url, "");
            File savedFile = new File(savedFilePath);

            if (savedFile.exists() || listSavedWallpapers.size() > 9) {
                Toast.makeText(context,
                        "Wallpaper saved", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread() {
                @Override
                public void run() {
                    try {
                        String result = saveFile(bitmap);
                        if (result == null) {
                            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "saveToGallery: Error in saving file ");
                            return;
                        }

                        runOnUiThread(() -> MediaScannerConnection.scanFile(context,
                                new String[]{result}, null,
                                (path1, uri) -> {
                                }));


                        pref.edit().putString(url, result).apply();
                        Log.i(TAG, "run: Wallpaper saved");
                        runOnUiThread(() -> Toast.makeText(context,
                                "Wallpaper saved", Toast.LENGTH_SHORT).show());
                        listSavedWallpapers.add(url);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();

        }
                , 0
                , 1500
                , ImageView.ScaleType.CENTER_CROP
                , null
                , volleyError -> {
            dialogClass.cancel();
            volleyError.printStackTrace();
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
        });

        queue.add(imageRequest);
        dialogClass.show();

    }

    private String saveFile(Bitmap bitmap) {
        //Write File
        String path = saveBitmap(bitmap);
        if (path.isEmpty()) {
            return null;
        }

        return path;

    }

    private String saveBitmap(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            return fileSaverForAndroid10(bitmap);
        }

        String fileName = System.currentTimeMillis() + ".jpg";
        File appFolder = new File(Configuration.appFolderPath);
        if (!appFolder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            appFolder.mkdirs();
        }

        File file = null;

        if (bitmap != null) {
            try {
                FileOutputStream outputStream = null;

                try {
                    file = new File(appFolder.getAbsolutePath() + "/" + fileName + "/");

                    outputStream = new FileOutputStream(file.getAbsolutePath()); //here is set your file path where you want to save or also here you can set file object directly

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); // bitmap is your Bitmap instance, if you want to compress it you can compress reduce percentage
                    // PNG is a lossless format, the compression factor (100) is ignored
                    return file.getAbsolutePath();
                } catch (Exception e) {
                    Log.i(TAG, "saveBitmap: " + e.getLocalizedMessage());
                    e.printStackTrace();
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                Log.i(TAG, "saveBitmap: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        return "";
    }

    private String fileSaverForAndroid10(Bitmap bitmap) {
        String fileName = System.currentTimeMillis() + "";

        ContentResolver resolver = context.getContentResolver();

        Uri collection;
        if (Build.VERSION.SDK_INT > 28) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Images.Media.WIDTH, bitmap.getWidth());
        contentValues.put(MediaStore.Images.Media.HEIGHT, bitmap.getHeight());
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + Configuration.appFolderName);
        }

        Uri resultUri = resolver.insert(collection, contentValues);

        //Save the file
        try {
            OutputStream outputStream = resolver.openOutputStream(resultUri);
            bitmap.compress(Bitmap.CompressFormat.JPEG,
                    100, outputStream);
            Log.i(TAG, "saveFile: File saved: " + "BGMI's Wallpapers");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        File folder = new File(Configuration.appFolderPath);
        File file = new File(folder.getAbsolutePath() + fileName + ".jpg");

        return file.getAbsolutePath();

    }

//    private Uri saveFile(Bitmap bitmap) {
//        ContentResolver resolver = context.getContentResolver();
//
//        Uri collection;
//        if (Build.VERSION.SDK_INT > 28) {
//            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
//        } else {
//            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        }
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis() + "");
//        contentValues.put(MediaStore.Images.Media.WIDTH, bitmap.getWidth());
//        contentValues.put(MediaStore.Images.Media.HEIGHT, bitmap.getHeight());
//        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "BGMI's Wallpapers");
//        }
//
//
//        Uri resultUri = resolver.insert(collection, contentValues);
//
//        //Save the file
//        try {
//            OutputStream outputStream = resolver.openOutputStream(resultUri);
//            bitmap.compress(Bitmap.CompressFormat.JPEG,
//                    100, outputStream);
//            Log.i(TAG, "saveFile: File saved: " + "PUBG Wallpapers 4k HD");
//            return resultUri;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//
//
//    }

    private boolean givenStoragePermissions() {
        if (Build.VERSION.SDK_INT < 23)
            return true;

        if (Build.VERSION.SDK_INT <= 28) {
            return (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    && (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        }

        return (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT < 23)
            return;

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT > 28)
            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

        ActivityCompat.requestPermissions(FinalWallpaperActivity.this, permissions, STORAGE_REQ);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.final_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_share) {
//            if (IronSource.isInterstitialReady()) {
//                selectedForAd = 0;
//                IronSource.showInterstitial();
//                return super.onOptionsItemSelected(item);
//            }

            shareImage(urlsList.get(binding.VPagerFinal.getCurrentItem()));
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareImage(String url) {
        //If activity is finished
        try {
            MyProgressDialog dialogClass = new MyProgressDialog(context);
            dialogClass.setMsg("Downloading...");

            if (!givenStoragePermissions()) {
                whichFeatureSelected = 2;
                requestPermissions();
                return;
            }

            if (isFromDownloads) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                share.putExtra(Intent.EXTRA_TEXT,
                        "Get more Wallpapers from - https://play.google.com/store/apps/details?id=" +
                                context.getPackageName());
                MediaScannerConnection.scanFile(context,
                        new String[]{url}, null,
                        (path1, uri) -> {
                            share.putExtra(Intent.EXTRA_STREAM, uri);
                            startActivity(Intent.createChooser(share, "Share"));
                        });
                return;
            }

            //Downloading Image
            ImageRequest imageRequest = new ImageRequest(url, bitmap -> {
                try {
                    dialogClass.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.i(TAG, "saveToGallery: downloaded!");
                //Sharing
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                share.putExtra(Intent.EXTRA_TEXT,
                        "Get more Wallpapers from - https://play.google.com/store/apps/details?id=" +
                                context.getPackageName());

                String savedPath = pref.getString(url, "");
                File savedFile = new File(savedPath);
                if (!savedFile.exists()) {
                    String result = saveFile(bitmap);
                    if (result == null) {
                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    pref.edit().putString(url, result).apply();
                    savedPath = result;
                }

                MediaScannerConnection.scanFile(context,
                        new String[]{savedPath}, null,
                        (path1, uri) -> {
                            share.putExtra(Intent.EXTRA_STREAM, uri);
                            startActivity(Intent.createChooser(share, "Share"));
                        });


//            ContentValues values = new ContentValues();
//            values.put(MediaStore.Images.Media.TITLE, "title");
//            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                    values);
//
//            OutputStream outstream;
//            try {
//                outstream = getContentResolver().openOutputStream(uri);
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
//                outstream.close();
//            } catch (Exception e) {
//                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
//                System.err.println(e.toString());
//            }


            }
                    , 0
                    , 1500
                    , ImageView.ScaleType.CENTER_CROP
                    , null
                    , volleyError -> {
                dialogClass.cancel();
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            });

            queue.add(imageRequest);
            dialogClass.show();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, 0);
            view.requestLayout();
        }
    }

    private void transparentStatusAndNavigation() {
        //make full transparent statusBar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    private void setWindowFlag(final int bits, boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (givenStoragePermissions()) {
            String url = urlsList.get(binding.VPagerFinal.getCurrentItem());

            switch (whichFeatureSelected) {
                case 0:
                    saveToGallery(url);
                    break;
                case 2:
                    shareImage(url);
                    break;
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);

                    Log.i(TAG, "onActivityResult: height: " + bitmap.getHeight());
                    Log.i(TAG, "onActivityResult: width: " + bitmap.getWidth());

                    showSetAsDialog(bitmap);

                } catch (IOException e) {
                    Log.i(TAG, "onActivityResult: unable to read bitmap");
                    e.printStackTrace();
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.i(TAG, "onActivityResult: error: " + error);
            }
        }
    }

    private void setWallpaper(int which, Bitmap bitmap) {
        AtomicBoolean result = new AtomicBoolean(false);
        MyProgressDialog dialogClass = new MyProgressDialog(context);
        dialogClass.setMsg("Applying...");

        new Thread() {
            @Override
            public void run() {
                try {
                    runOnUiThread(dialogClass::show);

                    WallpaperManager manager = WallpaperManager.getInstance(context);

                    //for low Version
                    if (which == -1) {
                        manager.setBitmap(bitmap);
                    }

                    //Set as home screen
                    else if (which == 0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            manager.setBitmap(bitmap, null,
                                    false, WallpaperManager.FLAG_SYSTEM);
                        }
                    }

                    //Set as Lock screen
                    else if (which == 1) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            manager.setBitmap(bitmap, null,
                                    false, WallpaperManager.FLAG_LOCK);
                        }
                    }

                    //Set as both
                    else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            manager.setBitmap(bitmap, null,
                                    false,
                                    WallpaperManager.FLAG_SYSTEM);
                            manager.setBitmap(bitmap, null,
                                    false, WallpaperManager.FLAG_LOCK);
                        }
                    }

                    result.set(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //On Set successful
                runOnUiThread(() -> {
                    dialogClass.cancel();
                    Toast.makeText(context, "Wallpaper applied!", Toast.LENGTH_SHORT).show();
                });

            }
        }.start();

    }

    private void showSetAsDialog(Bitmap bitmap) {
        Dialog_Item[] items = {
                new Dialog_Item("  Set on Home screen", R.drawable.ic_baseline_phone_android_24),
                new Dialog_Item("  Set on Lock screen", R.drawable.ic_baseline_lock_24),
                new Dialog_Item("  Set Both", R.drawable.ic_baseline_phonelink_lock_24)
        };

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            setWallpaper(-1, bitmap);
            return;
        }

        ListAdapter adapterT = new ArrayAdapter<Dialog_Item>(
                this,
                android.R.layout.select_dialog_item,
                android.R.id.text1,
                items) {
            public View getView(int position, View convertView, ViewGroup parent) {
                //Use super class to create the View
                View v = super.getView(position, convertView, parent);
                TextView tv = v.findViewById(android.R.id.text1);

                tv.setTextSize(18);
                //Put the image on the TextView
                tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);
                return v;
            }
        };

        MaterialAlertDialogBuilder setAsDialog = new MaterialAlertDialogBuilder(context);
        setAsDialog.setAdapter(adapterT, (dialog, which) -> {
            setWallpaper(which, bitmap);
        });

        setAsDialog.show();
    }

    static ArrayList<String> convertToFirebaseStyle(ArrayList<String> urls) {
        ArrayList<String> newList = new ArrayList<>();
        for (String anUrl : urls) {
            while (anUrl.contains("/")) {
                anUrl = anUrl.replace("/", ":slash:");
            }
            while (anUrl.contains(".")) {
                anUrl = anUrl.replace(".", ":dot:");
            }
            newList.add(anUrl);
        }
        return newList;
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
//                Log.i(TAG, "onBannerAdLoadFailed: ");
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
    }

    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }

    private void onClickApplyButton(String url, String sUrl) {
        if (isFromDownloads) {

            new Thread() {
                @Override
                public void run() {
                    Bitmap bitmap = BitmapFactory.decodeFile(url);
                    runOnUiThread(() -> showFirstApplyDialog(bitmap));
                }
            }.start();

            return;
        }

        if (listedOnDB)
            increasePopularity(sUrl);

        MyProgressDialog dialogClass = new MyProgressDialog(context);
        dialogClass.setMsg("Downloading....");

        ImageRequest imageRequest = new ImageRequest(url, bitmap -> {
            dialogClass.cancel();
            showFirstApplyDialog(bitmap);
        }
                , 0
                , 1500
                , ImageView.ScaleType.CENTER_CROP
                , null
                , volleyError -> {
            dialogClass.cancel();
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
        });

        queue.add(imageRequest);
        dialogClass.show();

    }

    private void increasePopularity(String url) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("All_Urls_101")
//                .whereEqualTo("mUrl", url)
//                .limit(1)
//                .get()
//                .addOnCompleteListener(task -> {
////                    List<Wallpaper2> data = task.getResult().toObjects(Wallpaper2.class);
////                    ArrayList<Wallpaper2> list = new ArrayList<>(data);
//
//                    Iterator<QueryDocumentSnapshot> iterator = task.getResult().iterator();
//
//                    if (iterator.hasNext()) {
//                        QueryDocumentSnapshot snapshot = iterator.next();
//
//                        //noinspection ConstantConditions
//                        long popularity = snapshot.getLong("popularity");
//                        String id = snapshot.getString("id");
//
//                        Map<String, Object> data = new HashMap<>();
//                        data.put("id", id);
//                        data.put("mUrl", snapshot.getString("mUrl"));
//                        data.put("sUrl", snapshot.getString("sUrl"));
//                        data.put("popularity", ++popularity);
//
//                        if (id != null)
//                            db.collection("All_Urls_101").document(id).set(data);
//                    }
//
////                    if (list.size() > 0) {
////                        Wallpaper2 wallpaper2 = list.get(0);
////                        String id = wallpaper2.getId();
////                        long popularity = wallpaper2.getPopularity() + 1;
////
////                        Map<String, Object> data1 = new HashMap<>();
////                        data1.put("id", wallpaper2.getId());
////                        data1.put("mUrl", wallpaper2.getmUrl());
////                        data1.put("sUrl", wallpaper2.getsUrl());
////                        data1.put("popularity", popularity);
////
////                        db.collection("All_Urls_101").document(id).set(data1);
////                    }
//
//
//                });

        ArrayList<String> list = new ArrayList<>();
        list.add(url);
        list = convertToFirebaseStyle(list);
        url = list.get(0);

        DatabaseReference urlRef = FirebaseDatabase.getInstance().getReference("Urls").child(url);
        Log.i(TAG, "increasePopularity: ");

        urlRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object object = snapshot.getPriority();
                Number priority = 0;

                if (object instanceof Number) {
                    Log.i(TAG, "onDataChange: yes it's Number");
                    priority = (Number) object;
                    Log.i(TAG, "onDataChange: old priority: " + priority);
                } else {
                    Log.i(TAG, "onDataChange: no it's not Number");
                }

                Number x = 1;
                priority = priority.intValue() + x.intValue();

                Number finalPriority = priority;
                urlRef.setPriority(finalPriority).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i(TAG, "onSuccess: updated priority: " + finalPriority);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showFirstApplyDialog(Bitmap bitmap) {
        Dialog_Item[] items = {
                new Dialog_Item("  Apply right now", R.drawable.ic_baseline_wallpaper_24),
                new Dialog_Item("  Crop & Apply", R.drawable.ic_baseline_crop_24),
        };

        ListAdapter adapterT = new ArrayAdapter<Dialog_Item>(
                this,
                android.R.layout.select_dialog_item,
                android.R.id.text1,
                items) {
            public View getView(int position, View convertView, ViewGroup parent) {
                //Use super class to create the View
                View v = super.getView(position, convertView, parent);
                TextView tv = v.findViewById(android.R.id.text1);

                tv.setTextSize(18);
                //Put the image on the TextView
                tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);
                return v;
            }
        };

        MaterialAlertDialogBuilder openWithDialog = new MaterialAlertDialogBuilder(context);
        openWithDialog.setAdapter(adapterT, (dialog, which) -> {
            Log.i(TAG, "onClickApplyButton: " + which);
            if (which == 0)
                showSetAsDialog(bitmap);
            else
                cropAndSetImage(bitmap);
        });


        openWithDialog.show();
    }

    private void cropAndSetImage(Bitmap bitmap) {
        //Height and width of screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        new Thread() {
            @Override
            public void run() {
                Uri uri = getTempFileUri(bitmap);

                runOnUiThread(() -> CropImage.activity(uri)
                        .setRequestedSize(width, height)
                        .setAllowFlipping(false)
                        .start(FinalWallpaperActivity.this));

            }
        }.start();

    }

    private Uri getTempFileUri(Bitmap bitmap) {
        String fileName = System.currentTimeMillis() + ".jpg";
        File folder = new File(getCacheDir() + "/MyTempFolder/");
        if (!folder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            folder.mkdirs();
        }

        File file;

        if (bitmap != null) {
            try {
                FileOutputStream outputStream = null;

                try {
                    file = new File(folder.getAbsolutePath() + "/" + fileName + "/");

                    outputStream = new FileOutputStream(file.getAbsolutePath()); //here is set your file path where you want to save or also here you can set file object directly

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); // bitmap is your Bitmap instance, if you want to compress it you can compress reduce percentage
                    // PNG is a lossless format, the compression factor (100) is ignored
                    return Uri.fromFile(file);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}


