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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firex.pubg_wallpaper_2.CatX;
import com.firex.pubg_wallpaper_2.Models.CatModel;
import com.firex.pubg_wallpaper_2.R;
import com.firex.pubg_wallpaper_2.Utilities.AdsController;
import com.firex.pubg_wallpaper_2.Utilities.DataTransfer;
import com.firex.pubg_wallpaper_2.Utilities.Utils;

import java.util.ArrayList;

public class CatAdapter2 extends RecyclerView.Adapter<CatAdapter2.ViewHolder> {
    Context mContext;
    ArrayList<CatModel> cats;
    private ArrayList<ArrayList<String>> keyList = new ArrayList<>();
    private ArrayList<ArrayList<String>> valueList = new ArrayList<>();

    @Override
    public int getItemCount() {
        return cats.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.catImage);
            textView = itemView.findViewById(R.id.catName);
        }
    }

    public void settingKeyList(ArrayList<ArrayList<String>> keyList) {
        this.keyList = keyList;
    }

    public void settingValueList(ArrayList<ArrayList<String>> valueList) {
        this.valueList = valueList;
    }

    public CatAdapter2(Context context, ArrayList<CatModel> categories) {
        this.mContext = context;
        this.cats = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(mContext)
                        .inflate(R.layout.item_cat_type_2, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(cats.get(position).getTitle());

        final int pos = position;
        Glide.with(mContext)
                .load(cats.get(position).getUrl())
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
                .into(holder.imageView);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, CatX.class);
            intent.putExtra("title", cats.get(position).getTitle());
            DataTransfer.setMainList(valueList.get(position));
            DataTransfer.setsList(keyList.get(position));
            intent.putExtra("isFromMore", true);

            AdsController.adCounter++;
            AdsController.showInterAd((Activity) mContext, intent, 0);
        });

        Utils.setOnTouchListener(holder.itemView);

    }


}
