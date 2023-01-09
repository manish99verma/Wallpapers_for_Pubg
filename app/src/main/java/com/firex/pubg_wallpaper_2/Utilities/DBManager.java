package com.firex.pubg_wallpaper_2.Utilities;

import android.content.Context;

public class DBManager {
    Context context;
    private static final String TAG = "DatabaseManagerTAG";

//    public DatabaseManager(Context context) {
//        this.context = context;
//
//        /**Categories Uploader*/
////        new Thread() {
////            @Override
////            public void run() {
////                ArrayList<String> listSUrls = readUrlsFileMain(false);
////                ArrayList<String> listMUrls = readUrlsFileMain(true);
////
////                Log.i(TAG, "run: sizeOfSUrls: " + listSUrls.size());
////                Log.i(TAG, "run: sizeOfMUrls: " + listMUrls.size());
////
////                FirebaseFirestore db = FirebaseFirestore.getInstance();
////                String path = "Categories_101";
////                String catName = "Girls";
////
////                Map<String, Object> data = new HashMap<>();
////                data.put("title", catName);
////                data.put("url","https://blogger.googleusercontent.com/img/a/AVvXsEjT2MB-0qkfqk4UoNlsGThJHOV_VlRKoe1l-zi1C95zFAinwomT05lGjAMx43D62YWydWAYI9TZ_OkQxFwMd3h-JZimo5gIB_lgM09VsCDGxhPOdY302OUZ9L58iTo-AemO22YA3ffiJxAejE2kBTY95bC6y3i7R9ri7c2KNiI3Q41BeaBqEAj9ajRY=s400");
////
////                db.collection(path)
////                        .document(catName)
////                        .set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
////                    @Override
////                    public void onComplete(@NonNull Task<Void> task) {
////                        if (task.isSuccessful()) {
////                            Log.i(TAG, "onComplete: Successfully Uploaded thumbnails!");
////                            uploadWallpapersListOfCats(db, path, catName, listSUrls, listMUrls);
////                        } else {
////                            Log.i(TAG, "onComplete: Error");
////                            task.getException().printStackTrace();
////                        }
////                    }
////                });
////
////            }
////        }.start();
//
//        /**All Urls Uploader*/
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
////        db.collection("Categories_101").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
////            @Override
////            public void onComplete(@NonNull Task<QuerySnapshot> task) {
////                if (task.isSuccessful()) {
////                    List<Category> data = task.getResult().toObjects(Category.class);
////                    ArrayList<Category> listCategories = new ArrayList<>(data);
////
////                    for (Category category : listCategories) {
////                        db.collection("Categories_101")
////                                .document(category.getTitle())
////                                .collection("Wallpapers").get()
////                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
////                                    @Override
////                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
////                                        List<Wallpaper> data = task.getResult().toObjects(Wallpaper.class);
////                                        ArrayList<Wallpaper> listWallpapers = new ArrayList<>(data);
////
////                                        for (Wallpaper wallpaper : listWallpapers) {
////                                            String wallpaperId=UUID.randomUUID().toString();
////
////                                            Map<String,Object> newData=new HashMap<>();
////                                            newData.put("id",wallpaperId);
////                                            newData.put("sUrl",wallpaper.getsUrl());
////                                            newData.put("mUrl",wallpaper.getmUrl());
////                                            newData.put("popularity",0L);
////
////                                            db.collection("All_Urls_101")
////                                                    .document(wallpaperId)
////                                                    .set(newData);
////
////                                        }
////
////                                    }
////                                });
////                    }
////
////                }
////            }
////        });
//
//
////        Verifying All urls
////        db.collection("All_Urls_101").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
////            @Override
////            public void onComplete(@NonNull Task<QuerySnapshot> task) {
////                List<Wallpaper> data = task.getResult().toObjects(Wallpaper.class);
////                ArrayList<Wallpaper> listTrending = new ArrayList<>(data);
////
////                Log.i(TAG, "onComplete: size of all urls: "+listTrending.size());
////            }
////        });
//
//    }

//    private void uploadWallpapersListOfCats(FirebaseFirestore db, String pathName, String catName, ArrayList<String> listSUrls, ArrayList<String> listMUrls) {
//        for (int i = 0; i < listSUrls.size(); i++) {
//            Map<String, Object> data = new HashMap<>();
//            data.put("sUrl", listSUrls.get(i));
//            data.put("mUrl", listMUrls.get(i));
//
//            int finalI = i;
//            db.collection(pathName)
//                    .document(catName)
//                    .collection("Wallpapers")
//                    .document(UUID.randomUUID().toString())
//                    .set(data).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    Log.i(TAG, "onComplete: uploaded: " + finalI + "/" + listMUrls.size());
//                } else {
//                    Log.i(TAG, "onComplete: Error uploading wallpapers collection");
//                    task.getException().printStackTrace();
//                }
//            });
//        }
//    }

//    private ArrayList<String> readUrlsFileMain(boolean isMain) {
//        ArrayList<String> result = new ArrayList<>();
//        long startTime = System.currentTimeMillis();
//
//        InputStream ins;
//
//        Log.d(TAG, "readUrlsFileMain: yes");
//
//
//        //Reading the txt file
//        ins = context.getResources().openRawResource(R.raw.wallpapers_hash_320);
//        if (isMain)
//            ins = context.getResources().openRawResource(R.raw.wallpapers_hash_1000);
//
//        StringBuilder rawData = new StringBuilder();
//
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ins));
//
//        try {
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                rawData.append(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //Getting HashMap
//        Pattern pattern = Pattern.compile("start:(.*?)end:");
//        Matcher matcher = pattern.matcher(rawData);
//
//        int count = 0;
//        while (matcher.find()) {
//            String urlsList = matcher.group(1);
//            count++;
//
//            if (urlsList == null) {
//                Log.i(TAG, "readUrlsFile: null list");
//                throw new RuntimeException("Something went wrong");
//            }
//
//            String[] urls = urlsList.split(",");
//
//            ArrayList<String> aList = new ArrayList<>(Arrays.asList(urls));
//
//            result.addAll(aList);
//        }
//
//        Log.i(TAG, "readUrlsFileMain: count: " + count);
//        Log.d(TAG, "readUrlsFileMain: time taken: " + (System.currentTimeMillis() - startTime) + "ms");
//
//        return result;
//    }

}
