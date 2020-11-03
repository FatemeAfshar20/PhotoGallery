package org.maktab.photogallery.Service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;

import org.maktab.photogallery.LruCach.PhotoGalleryLruCache;
import org.maktab.photogallery.network.FlickrFetcher;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BitmapLooper<T> extends HandlerThread {
    public static final int WHAT_DOWNLOAD_BITMAP = 1;
    private Handler mHandler;
    private Handler mMainHandler;
    private FlickrFetcher mFlickrFetcher;

    private ConcurrentMap<T, String> mTargetStringMap =
            new ConcurrentHashMap<>();

    private PhotoGalleryLruCache mPhotoGalleryLruCache;

    public BitmapDownloadedListener getBitmapDownloaded() {
        return mBitmapDownloaded;
    }

    public void setBitmapDownloaded(BitmapDownloadedListener bitmapDownloaded) {
        mBitmapDownloaded = bitmapDownloaded;
    }

    private BitmapDownloadedListener mBitmapDownloaded;

    public BitmapLooper() {
        super("Bitmap Looper");
        mFlickrFetcher = new FlickrFetcher();

        mPhotoGalleryLruCache=new PhotoGalleryLruCache();
    }

    public Handler getMainHandler() {
        return mMainHandler;
    }

    public void setMainHandler(Handler mainHandler) {
        mMainHandler = mainHandler;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();

        mHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                try {
                    handlerBitmapDownload(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void handlerBitmapDownload(@NonNull Message msg) throws IOException {
        if (msg.what == WHAT_DOWNLOAD_BITMAP) {
            if (msg.obj == null)
                return;

            T target = (T) msg.obj;
            String url = mTargetStringMap.get(target);

            byte[] bitmapArray = mFlickrFetcher.getUrlBytes(url);
            Bitmap bitmap = BitmapFactory.decodeByteArray(
                    bitmapArray,
                    0,
                    bitmapArray.length);

            mPhotoGalleryLruCache.
                    addBitmapToMemoryCache(url,bitmap);

            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mTargetStringMap.get(target) != url)
                        return;

                            mBitmapDownloaded.
                                    onBitmapDownloaded(
                                            target,
                                            mPhotoGalleryLruCache.
                                                    getBitmapFromMemCache(url));
                }
            });
        }
    }

    public void setMessageOnQueue(T target, String url) {
        mTargetStringMap.put(target, url);
        Message message = mHandler.obtainMessage(
                WHAT_DOWNLOAD_BITMAP, target);

        message.sendToTarget();
    }

    public interface BitmapDownloadedListener {
        void onBitmapDownloaded(Object target, Bitmap bitmap);
    }

    public void clearQueue(){
        mHandler.removeMessages(WHAT_DOWNLOAD_BITMAP);
    }
}
