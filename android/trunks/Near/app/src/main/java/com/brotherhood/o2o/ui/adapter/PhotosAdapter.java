package com.brotherhood.o2o.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.ui.activity.PhotosGalleryActivity;

import java.util.ArrayList;

/**
 * Created by laimo.li on 2016/1/22.
 */
public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder>{

    private ArrayList<String> photos;

    private Context mContext;

    public PhotosAdapter(Context context,ArrayList<String> photos){
        this.photos = photos;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.photos_item_view, parent, false);
            return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageLoaderManager.displayImageByUrl(mContext, holder.ivPhoto, photos.get(position), R.mipmap.img_default);
        holder.ivPhoto.setTag(position);
        holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                PhotosGalleryActivity.show(mContext,photos,position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView ivPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.ivPhoto);
        }
    }

}
