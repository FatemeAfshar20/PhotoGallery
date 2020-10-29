package org.maktab.photogallery.repository;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.maktab.photogallery.model.GalleryItem;
import org.maktab.photogallery.network.FlickrFetcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoRepository {

    private static final String TAG = "PhotoRepository";
    private FlickrFetcher mFetcher;
    private List<GalleryItem> mItems=new ArrayList<>();

    public PhotoRepository() {
        mFetcher = new FlickrFetcher();
    }

    public List<GalleryItem> getItems() {
        return mItems;
    }

    public void setItems(List<GalleryItem> items) {
        mItems = items;
    }

    //this method must run on background thread.
    public List<GalleryItem> fetchItems(String number) {
        GsonBuilder gsonBuilder=new GsonBuilder();

        Gson gson=new GsonBuilder().setPrettyPrinting().create();
        String url = mFetcher.getRecentUrl(number);
        try {
            String response = mFetcher.getUrlString(url);
            Log.d(TAG, "response: " + response);

            JSONObject bodyObject = new JSONObject(response);
            List<GalleryItem> items = parseJson(bodyObject);
            mItems.addAll(items);
            return mItems;
        } catch (IOException | JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    private List<GalleryItem> parseJson(JSONObject bodyObject) throws JSONException {
        List<GalleryItem> items = new ArrayList<>();
        Gson gson=new GsonBuilder().setPrettyPrinting().create();

        JSONObject photosObject = bodyObject.getJSONObject("photos");
        JSONArray photoArray = photosObject.getJSONArray("photo");

        for (int i = 0; i < photoArray.length(); i++) {
            JSONObject photoObject = photoArray.getJSONObject(i);

            if (!photoObject.has("url_s"))
                continue;

       /*     String id = photoObject.getString("id");
            String title = photoObject.getString("title");
            String url = photoObject.getString("url_s");*/

 /*           GalleryItem item = new GalleryItem(id, title, url);*/

            GalleryItem item=gson.fromJson(String.valueOf(photoObject),GalleryItem.class);
            items.add(item);
        }

        return items;
    }
}