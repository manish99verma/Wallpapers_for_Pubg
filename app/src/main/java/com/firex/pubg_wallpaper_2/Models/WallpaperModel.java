package com.firex.pubg_wallpaper_2.Models;

public class WallpaperModel {
    String id = "";
    long popularity = 0;
    String sUrl = "";
    String mUrl = "";


    public WallpaperModel(String sUrl, String mUrl, String id, long popularity) {
        this.sUrl = sUrl;
        this.mUrl = mUrl;
        this.id = id;
        this.popularity = popularity;
    }

    public WallpaperModel(String sUrl, String mUrl) {
        this.sUrl = sUrl;
        this.mUrl = mUrl;
    }

    public String getsUrl() {
        return sUrl;
    }

    public String getmUrl() {
        return mUrl;
    }

    public long getPopularity() {
        return popularity;
    }

    public WallpaperModel() {
    }

    public String getId() {
        return id;
    }
}
