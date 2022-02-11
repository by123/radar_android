package com.brotherhood.o2o.surprise.controller.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brotherhood.o2o.application.MyApplication;
import com.brotherhood.o2o.R;
import com.brotherhood.o2o.extensions.fresco.ImageLoader;
import com.brotherhood.o2o.surprise.model.ItemRewardInfo;
import com.brotherhood.o2o.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by by.huang on 2015/7/29.
 */
public class ItemRewardAdapter extends RecyclerView.Adapter<ItemRewardAdapter.ViewHolder> {
    private List<ItemRewardInfo> mDataSet;
    private int mCount = 0;
    private OnRecyclerItemListener mListener;

    public ItemRewardAdapter(List<ItemRewardInfo> dataSet) {
        mDataSet = dataSet;
        if (dataSet != null && dataSet.size() > 0) {
            mCount = mDataSet.size();
        }
    }

    public List<ItemRewardInfo> getDatas() {
        return mDataSet;
    }

    public void setOnItemClickListener(OnRecyclerItemListener listener) {
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.surprise_cell_item_reward, parent, false);
        return new ViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.configureItemView(mDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @InjectView(R.id.image_icon)
        SimpleDraweeView mIconImg;
        @InjectView(R.id.txt_name)
        TextView mNameTxt;
        @InjectView(R.id.txt_time)
        TextView mTimeTxt;
        @InjectView(R.id.txt_authentication)
        TextView mAuthenticationTxt;
        @InjectView(R.id.img_statu)
        ImageView mStatuImg;
        @InjectView(R.id.img_arrow)
        ImageView mArrowImg;
        @InjectView(R.id.txt_free)
        TextView mFreeTxt;

        public ViewHolder(View itemView, OnRecyclerItemListener listener) {
            super(itemView);
            mListener = listener;
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void configureItemView(final ItemRewardInfo item) {
            if (item.mStatus == 2 || item.mStatus == 3) {
                if (TextUtils.isEmpty(item.mWhitePath)) {
                    ImageLoader.getInstance().setImageUrl(mIconImg, item.mIconUrl, 1, new ImageLoader.ImageLoaderListener() {
                        @Override
                        public void OnLoadStart() {

                        }

                        @Override
                        public void OnLoadFinish(Bitmap bitmap) {
                            item.mWhitePath = ImageLoader.getInstance().saveBitmap(Utils.convertToBlackWhite(bitmap));
                            ImageLoader.getInstance().setImageLocal(mIconImg, item.mWhitePath);
                        }

                        @Override
                        public void OnLoadFail() {

                        }
                    }, Utils.dip2px(60), Utils.dip2px(60));
                } else {
                    ImageLoader.getInstance().setImageLocal(mIconImg, item.mWhitePath);
                }
            } else {
                ImageLoader.getInstance().setImageUrl(mIconImg, item.mIconUrl);
            }
            mNameTxt.setText(item.mTitle);
            mTimeTxt.setText(Utils.getString(R.string.surprise_time, Utils.formatTime(item.mCreateTime, "yyyy.MM.dd HH:mm")));
            if (item.mStatus == 2 || item.mStatus == 3) {
                mStatuImg.setVisibility(View.VISIBLE);
                mArrowImg.setVisibility(View.GONE);

                mNameTxt.setTextColor(Color.parseColor("#66000000"));
                mTimeTxt.setTextColor(Color.parseColor("#66000000"));
                mAuthenticationTxt.setTextColor(Color.parseColor("#66000000"));
                mFreeTxt.setBackgroundDrawable(MyApplication.mApplication.getResources().getDrawable(R.drawable.shape_free_gray_bg));

                if (item.mStatus == 2) {
                    mStatuImg.setImageResource(R.drawable.img_surprise_employ_normal);
                } else {
                    mStatuImg.setImageResource(R.drawable.img_surprise_exceed_normal);
                }
            } else {
                mStatuImg.setVisibility(View.GONE);
                mArrowImg.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.OnItemClick(view, getPosition());
            }
        }
    }

    public interface OnRecyclerItemListener {
        void OnItemClick(View view, int postion);
    }
}