package com.firex.pubg_wallpaper_2.Adapters;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firex.pubg_wallpaper_2.R;

import java.util.ArrayList;

public class FullSizeImageAdapter extends RecyclerView.Adapter<FullSizeImageAdapter.FullSizeViewHolder> {
    private String TAG = "x";
    private ArrayList<String> urlList;
    private Context myContext;

    class FullSizeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;

        public FullSizeViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.fullIamageView);
            progressBar = itemView.findViewById(R.id.progressFullSize);
        }
    }

    public FullSizeImageAdapter(String TAG, ArrayList<String> urls, Context context) {
        this.TAG = TAG;
        this.urlList = urls;
        this.myContext = context;
    }


    @NonNull
    @Override
    public FullSizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.item_recycler_fullsize, parent, false);
        return new FullSizeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FullSizeViewHolder holder, int position) {
        holder.progressBar.setVisibility(View.VISIBLE);
        loadImage(position,holder);

    }

    private void loadImage(int position,FullSizeViewHolder holder){
        Glide.with(myContext)
                .load(urlList.get(position))
                .error(R.color.main_app_bg)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.VISIBLE);
                        Log.i(TAG, "onLoadFailed: " + position);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.INVISIBLE);
                        Log.i(TAG, "onResourceReady: " + position);
                        return false;
                    }
                })
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return urlList.size();
    }



}