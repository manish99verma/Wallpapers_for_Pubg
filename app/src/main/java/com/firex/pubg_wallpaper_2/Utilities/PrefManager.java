package com.firex.pubg_wallpaper_2.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private static PrefManager prefManager;
    private final SharedPreferences.Editor editor;
    private final SharedPreferences sharedPreferences;

    public PrefManager(Context c) {
        sharedPreferences = c.getSharedPreferences("XVPN", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public boolean getBoolean(String key, boolean def) {
        return sharedPreferences.getBoolean(key, def);
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value).apply();
    }

    public static PrefManager getInstance(Context c) {
        if (prefManager == null)
            prefManager = new PrefManager(c);

        return prefManager;
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value).apply();
    }

}
