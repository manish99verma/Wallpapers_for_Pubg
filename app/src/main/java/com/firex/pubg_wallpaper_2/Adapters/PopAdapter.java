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

import java.util.ArrayList;

public class PopAdapter extends RecyclerView.Adapter<PopAdapter.ViewHolder> {
    Context context;
    ArrayList<WallpaperModel> wallpapers;

    public PopAdapter(Context context, ArrayList<WallpaperModel> wallpapers) {
        this.context = context;
        this.wallpapers = wallpapers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context)
                        .inflate(R.layout.item_popular_recycler, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int pos=position;
       loadImage(pos,holder.imageView);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, FinalWallpaperActivity.class);
            ArrayList<String> list = new ArrayList<>();
            ArrayList<String> sUrlsList = new ArrayList<>();
            for (WallpaperModel wallpaper : wallpapers) {
                list.add(wallpaper.getmUrl());
                sUrlsList.add(wallpaper.getsUrl());
            }
            DataTransfer.setMainList(list);
            DataTransfer.setsList(sUrlsList);
            intent.putExtra("pos", position);

            AdsController.adCounter++;
            AdsController.showInterAd((Activity) context, intent, 0);
        });

        Utils.setOnTouchListener(holder.itemView);
    }

    private  void loadImage(int pos,ImageView imageView){
        Glide.with(context)
                .load(wallpapers.get(pos).getsUrl())
                .placeholder(R.drawable.place_holder_img)
                .error(R.drawable.place_holder_img)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.i("Glide", "onLoadFailed: "+pos);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.i("Glide", "onResourceReady: "+pos);
                        return false;
                    }
                })
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return wallpapers.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image_popular);
        }
    }

}
