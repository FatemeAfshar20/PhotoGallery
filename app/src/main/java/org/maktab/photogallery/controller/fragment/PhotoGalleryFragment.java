package org.maktab.photogallery.controller.fragment;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.maktab.photogallery.R;
import org.maktab.photogallery.Service.BitmapLooper;
import org.maktab.photogallery.model.GalleryItem;
import org.maktab.photogallery.repository.PhotoRepository;

import java.util.List;

public class PhotoGalleryFragment extends Fragment {

    private static final int SPAN_COUNT = 3;
    private static final String TAG = "PGF";
    private RecyclerView mRecyclerView;
    private PhotoRepository mRepository;
    int nubmer = 2;

    private BitmapLooper<PhotoHolder> mHolderBitmapLooper;

    private Handler mMainHandler;

    public PhotoGalleryFragment() {
        // Required empty public constructor
    }

    public static PhotoGalleryFragment newInstance() {
        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRepository = new PhotoRepository();
        mMainHandler = new Handler();

        FlickrTask flickrTask = new FlickrTask("1");
        flickrTask.execute();

        mHolderBitmapLooper = new BitmapLooper<>();
        mHolderBitmapLooper.start();
        mHolderBitmapLooper.getLooper();
        mHolderBitmapLooper.setMainHandler(mMainHandler);
        mHolderBitmapLooper.setBitmapDownloaded(new BitmapLooper.BitmapDownloadedListener() {
            @Override
            public void onBitmapDownloaded(Object target, Bitmap bitmap) {

                PhotoGalleryFragment.PhotoHolder photoHolder = (PhotoGalleryFragment.PhotoHolder) target;
                photoHolder.bindBitmap(bitmap);
            }
        });

        /*Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                FlickrFetcher flickrFetcher = new FlickrFetcher();
                try {
                    String response = flickrFetcher.getUrlString("https://www.digikala.com/");
                    Log.d(TAG, response);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTextView.setText(response);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        });
        thread.start();*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        findViews(view);
        initViews();

        return view;
    }

    private void findViews(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view_photo_gallery);
    }

    private void initViews() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), SPAN_COUNT));
    }

    private void setupAdapter(List<GalleryItem> items) {
        PhotoAdapter adapter = new PhotoAdapter(items);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {

                    if (nubmer < 10) {
                        new FlickrTask(String.valueOf(nubmer)).execute();
                        nubmer++;
                    }
                }
            }
        });
    }

    public class PhotoHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;
        private GalleryItem mItem;

        public PhotoHolder(@NonNull View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.img_gallery);
        }

        public void bindGalleryItem(GalleryItem item) {
            mItem = item;
            mImageView.setImageDrawable(getResources().getDrawable(R.mipmap.ic_place_holder));
            mHolderBitmapLooper.setMessageOnQueue(this, mItem.getUrl());
        }

        public void bindBitmap(Bitmap bitmap) {
            mImageView.setImageBitmap(bitmap);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mItems;

        public List<GalleryItem> getItems() {
            return mItems;
        }

        public void setItems(List<GalleryItem> items) {
            mItems = items;
        }

        public PhotoAdapter(List<GalleryItem> items) {
            mItems = items;
        }

        @NonNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
            holder.bindGalleryItem(mItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }

    private class FlickrTask extends AsyncTask<Void, Void, List<GalleryItem>> {
        String number = "";

        public String getNumber() {
            return number;
        }

        public FlickrTask(String number) {
            this.number = number;
        }

        //this method runs on background thread
        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {
            List<GalleryItem> items = mRepository.fetchItems();
            return items;
        }

        //this method run on main thread
        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            super.onPostExecute(items);

            setupAdapter(items);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mHolderBitmapLooper.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mHolderBitmapLooper.quit();
    }
}