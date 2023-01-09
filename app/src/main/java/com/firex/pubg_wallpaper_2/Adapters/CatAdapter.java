package com.firex.pubg_wallpaper_2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firex.pubg_wallpaper_2.CatX;
import com.firex.pubg_wallpaper_2.Models.CatModel;
import com.firex.pubg_wallpaper_2.R;
import com.firex.pubg_wallpaper_2.Utilities.AdsController;
import com.firex.pubg_wallpaper_2.Utilities.DataTransfer;
import com.firex.pubg_wallpaper_2.Utilities.Utils;

import java.util.ArrayList;

public class CatAdapter extends RecyclerView.Adapter<CatAdapter.ViewHolder> {
    private ArrayList<ArrayList<String>> keyList = new ArrayList<>();
    private ArrayList<ArrayList<String>> valueList = new ArrayList<>();
    Context myContext;
    ArrayList<CatModel> cats;



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

    public CatAdapter(Context context, ArrayList<CatModel> categories) {
        this.myContext = context;
        this.cats = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(myContext)
                        .inflate(R.layout.item_cat, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(cats.get(position).getTitle());

        Glide.with(myContext)
                .load(cats.get(position).getUrl())
                .placeholder(R.drawable.place_holder_img)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(myContext, CatX.class);
            intent.putExtra("title", cats.get(position).getTitle());
            DataTransfer.setMainList(valueList.get(position));
            DataTransfer.setsList(keyList.get(position));

            AdsController.adCounter++;
            AdsController.showInterAd((Activity) myContext, intent, 0);
        });

        Utils.setOnTouchListener(holder.itemView);

    }

    @Override
    public int getItemCount() {
        return cats.size();
    }
}
