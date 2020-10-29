package org.maktab.photogallery.model;

import com.google.gson.annotations.SerializedName;

public class GalleryItem {
    @SerializedName("id")
    private String mId;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("url_s")
    private String mUrl;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public GalleryItem() {
    }

    public GalleryItem(String id, String title, String url) {
        mId = id;
        mTitle = title;
        mUrl = url;
    }
}
