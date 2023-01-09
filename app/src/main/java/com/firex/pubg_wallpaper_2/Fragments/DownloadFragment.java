package com.firex.pubg_wallpaper_2.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firex.pubg_wallpaper_2.Adapters.DownloadFragmentAdapter;
import com.firex.pubg_wallpaper_2.R;
import com.firex.pubg_wallpaper_2.Utilities.Configuration;
import com.firex.pubg_wallpaper_2.databinding.FragmentDownloadsBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;


public class DownloadFragment extends Fragment {
    private File[] files;
    Context mContext;
    Activity activity;
    private final String TAG = "DownloadsFragmentTAGG";
    private DownloadFragmentAdapter adapter;
    FragmentDownloadsBinding binding;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.downloads_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.clear_downloads_menu)
            showClearDownloadsWaningDialog();
        return super.onOptionsItemSelected(item);
    }

    private void showClearDownloadsWaningDialog() {
        new MaterialAlertDialogBuilder(mContext)
                .setMessage("Are you sure?")
                .setPositiveButton("Delete", (dialogInterface, i) -> {
                    if (files != null && files.length > 0) {
                        for (File aFile : files) {
                            try {
                                aFile.delete();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.i(TAG, "showClearDownloadsWaningDialog: " + e.getLocalizedMessage());
                            }
                        }
                    }

                    fetchFiles();
                    if (files != null && files.length > 0) {
                        Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Successfully deleted", Toast.LENGTH_SHORT).show();
                    }

                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {

                })
                .show();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDownloadsBinding.inflate(inflater, container, false);

        ((AppCompatActivity) activity).setSupportActionBar(binding.toolbar);
        setHasOptionsMenu(true);

        fetchFiles();

        return binding.getRoot();
    }

    private void fetchFiles() {
        File folder = new File(Configuration.appFolderPath);
        files = folder.listFiles();

        if (files != null && files.length > 0) {
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return Long.compare(f2.lastModified(), f1.lastModified());
                }
            });

            String subtitle = files.length + " wallpaper";
            binding.toolbar.setSubtitle(subtitle);

            adapter = new DownloadFragmentAdapter(mContext, files);
            StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2,
                    StaggeredGridLayoutManager.VERTICAL);
            binding.TVNoDownloads.setVisibility(View.INVISIBLE);
            binding.RVDownloads.setVisibility(View.VISIBLE);
            binding.RVDownloads.setLayoutManager(manager);
            binding.RVDownloads.setAdapter(adapter);
        } else {
            binding.toolbar.setSubtitle("0 wallpaper");
            binding.TVNoDownloads.setVisibility(View.VISIBLE);
            binding.RVDownloads.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
        activity = (Activity) context;
    }


}