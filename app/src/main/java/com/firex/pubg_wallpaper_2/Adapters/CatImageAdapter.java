package com.firex.pubg_wallpaper_2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firex.pubg_wallpaper_2.FinalWallpaperActivity;
import com.firex.pubg_wallpaper_2.Models.WallpaperModel;
import com.firex.pubg_wallpaper_2.R;
import com.firex.pubg_wallpaper_2.Utilities.AdsController;
import com.firex.pubg_wallpaper_2.Utilities.DataTransfer;
import com.firex.pubg_wallpaper_2.Utilities.Utils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class CatImageAdapter extends RecyclerView.Adapter<CatImageAdapter.ViewHolder> {
    Context mContext;
    ArrayList<WallpaperModel> wallpapersList;
    boolean listedOnDB = true;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(mContext)
                        .inflate(R.layout.item_catx, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int pos = position;

        loadImage(pos,holder.imageView);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, FinalWallpaperActivity.class);
            ArrayList<String> list = new ArrayList<>();
            ArrayList<String> sUrlsList = new ArrayList<>();
            for (WallpaperModel wallpaper : wallpapersList) {
                list.add(wallpaper.getmUrl());
                sUrlsList.add(wallpaper.getsUrl());
            }
            DataTransfer.setMainList(list);
            DataTransfer.setsList(sUrlsList);
            intent.putExtra("pos", position);
            intent.putExtra("listedOnDB", listedOnDB);

            AdsController.adCounter++;
            AdsController.showInterAd((Activity) mContext, intent, 0);
        });

        Utils.setOnTouchListener(holder.itemView);
    }

    private  void loadImage(int pos, ImageView imageView){
        Glide.with(mContext)
                .load(wallpapersList.get(pos).getsUrl())
                .placeholder(R.drawable.place_holder_img)
                .error(R.drawable.place_holder_img)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.i("Glide", "onLoadFailed: " + pos);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.i("Glide", "onResourceReady: " + pos);
                        return false;
                    }
                })
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return wallpapersList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cat_image);
        }

    }

    public CatImageAdapter(Context context, ArrayList<WallpaperModel> wallpapers, boolean listedOnDB) {
        this.mContext = context;
        this.listedOnDB = listedOnDB;
        this.wallpapersList = wallpapers;
    }


}

