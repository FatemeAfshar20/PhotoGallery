package org.maktab.photogallery.Gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.maktab.photogallery.model.GalleryItem;

import java.util.List;

public class GsonCollections {
    List<GalleryItem> mGalleryItems;

    public GsonCollections(List<GalleryItem> galleryItems) {
        mGalleryItems = galleryItems;
    }

    public static GsonCollections parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        GsonCollections gsonCollections = gson.fromJson(response, GsonCollections.class);
        return gsonCollections;
    }
}
