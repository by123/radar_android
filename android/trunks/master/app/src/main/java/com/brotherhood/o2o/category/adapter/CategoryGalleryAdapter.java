package com.brotherhood.o2o.category.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.category.model.SellerInfo;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ZhengYi on 15/6/2.
 */
public class CategoryGalleryAdapter extends BaseAdapter {
    private List<SellerInfo> mDataSet;
    private int mCount;

    public CategoryGalleryAdapter(List<SellerInfo> dataSet) {
        mDataSet = dataSet;
        mCount = mDataSet.size();
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public SellerInfo getItem(int position) {
        return mDataSet.get(position % mCount);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_cell_banner, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        SellerInfo item = getItem(position);
        holder.configureCell(item);
        return convertView;
    }

    static class ViewHolder {
        @InjectView(android.R.id.icon)
        SimpleDraweeView mCoverImageView;

        ViewHolder(View itemView) {
            ButterKnife.inject(this, itemView);
        }

        void configureCell(SellerInfo item) {
            Uri imageUri = Uri.parse(item.mCoverURL);
            mCoverImageView.setImageURI(imageUri);
        }
    }
}
