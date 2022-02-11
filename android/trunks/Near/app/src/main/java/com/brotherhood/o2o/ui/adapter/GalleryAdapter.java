package com.brotherhood.o2o.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.manager.ImageLoaderManager;

import java.util.List;

public class GalleryAdapter extends PagerAdapter {

    private List<String> photos;
    private Context mContext;

    private SparseArray<ImageView> mViews;

    public GalleryAdapter(Context mContext, List<String> photos) {
        this.photos = photos;
        this.mContext = mContext;
        mViews = new SparseArray<ImageView>();
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        String url = photos.get(position);
        ImageView view = mViews.get(position, null);
        if (view == null) {
            view = new ImageView(mContext);
            mViews.append(position, view);
        }
        ImageLoaderManager.displayImageByUrl(mContext, view,url, R.mipmap.img_default);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ImageView view = mViews.get(position, null);
        container.removeView(view);
    }



}
