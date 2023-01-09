package com.firex.pubg_wallpaper_2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firex.pubg_wallpaper_2.FinalWallpaperActivity;
import com.firex.pubg_wallpaper_2.R;
import com.firex.pubg_wallpaper_2.Utilities.AdsController;
import com.firex.pubg_wallpaper_2.Utilities.DataTransfer;
import com.firex.pubg_wallpaper_2.Utilities.Utils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;

public class DownloadFragmentAdapter extends RecyclerView.Adapter<DownloadFragmentAdapter.ViewHolder> {
    Context myContext;
    File[] fileArray;

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(myContext)
                .load(fileArray[position])
                .placeholder(R.drawable.place_holder_img)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(myContext, FinalWallpaperActivity.class);
            ArrayList<String> list = new ArrayList<>();
            for (File aFile : fileArray) {
                list.add(aFile.getAbsolutePath());
            }
            DataTransfer.setMainList(list);
            intent.putExtra("pos", position);
            intent.putExtra("fromDownloads", true);

            AdsController.adCounter++;
            AdsController.showInterAd((Activity) myContext, intent, 0);
        });

        Utils.setOnTouchListener(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return fileArray.length;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cat_image);
        }

    }

    public DownloadFragmentAdapter(Context context, File[] files) {
        this.myContext = context;
        this.fileArray = files;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(myContext)
                        .inflate(R.layout.item_catx, parent, false)
        );
    }


}

