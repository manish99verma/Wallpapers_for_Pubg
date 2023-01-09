package com.firex.pubg_wallpaper_2.Utilities;

import java.util.ArrayList;

public class DataTransfer {
    private static ArrayList<String> mainList=new ArrayList<>();
    private static ArrayList<String> sList=new ArrayList<>();

    public static ArrayList<String> getMainList() {
        return mainList;
    }

    public static void setMainList(ArrayList<String> mainList) {
        DataTransfer.mainList = mainList;
    }

    public static ArrayList<String> getsList() {
        return sList;
    }

    public static void setsList(ArrayList<String> sList) {
        DataTransfer.sList = sList;
    }
}
