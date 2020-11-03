package org.maktab.photogallery.LruCach;

import android.graphics.Bitmap;
import android.util.LruCache;

import org.maktab.photogallery.Service.BitmapLooper;

public class PhotoGalleryLruCache {
    private LruCache<String, Bitmap> mLruCache;
    private final int MAX_MEMORY =
            (int) (Runtime.getRuntime().maxMemory() / 1024);

    private final int CACHE_SIZE = MAX_MEMORY / 8;

    public PhotoGalleryLruCache() {
        mLruCache = new LruCache<String, Bitmap>(CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mLruCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mLruCache.get(key);
    }
}
