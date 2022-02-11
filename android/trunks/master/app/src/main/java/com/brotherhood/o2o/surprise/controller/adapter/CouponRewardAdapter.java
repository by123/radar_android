package com.brotherhood.o2o.surprise.controller.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brotherhood.o2o.application.MyApplication;
import com.brotherhood.o2o.R;
import com.brotherhood.o2o.extensions.fresco.ImageLoader;
import com.brotherhood.o2o.surprise.model.CouponRewardInfo;
import com.brotherhood.o2o.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by by.huang on 2015/7/29.
 */
public class CouponRewardAdapter extends RecyclerView.Adapter<CouponRewardAdapter.ViewHolder> {
    private List<CouponRewardInfo> mDatas;
    private int mCount = 0;
    private OnRecyclerItemListener mListener;

    public CouponRewardAdapter(List<CouponRewardInfo> datas) {
        this.mDatas = datas;
        if (mDatas != null && mDatas.size() > 0) {
            mCount = mDatas.size();
        }
    }

    public void setOnItemClickListener(OnRecyclerItemListener listener) {
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.surprise_cell_coupon_reward, parent, false);
        return new ViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.configureItemView(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @InjectView(R.id.img_icon)
        SimpleDraweeView mIconImg;

        @InjectView(R.id.txt_name)
        TextView mNameTxt;

        @InjectView(R.id.txt_code)
        TextView mCodeTxt;

        @InjectView(R.id.img_expand)
        ImageView mExpandImg;

        @InjectView(R.id.txt_expiretime)
        TextView mExpireTimeTxt;

        @InjectView(R.id.layout_expand)
        View mExpandLayout;

        @InjectView(R.id.txt_exchange)
        TextView mExchangeTxt;

        @InjectView(R.id.txt_use)
        TextView mUseTxt;

        @InjectView(R.id.btn_copyuse)
        View mCopyUseBtn;

        @InjectView(R.id.layout_time)
        View mTimeLayout;


        public ViewHolder(View itemView, OnRecyclerItemListener listener) {
            super(itemView);
            mListener = listener;
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void configureItemView(final CouponRewardInfo info) {
            ImageLoader.getInstance().setImageUrl(mIconImg, info.mIconUrl);
            mNameTxt.setText(info.mTitle);
            mCodeTxt.setText(info.mCode);
            String time = Utils.formatTime(info.mExpireTime, "yyyy-MM-dd");
            mExpireTimeTxt.setText(Utils.getString(R.string.conpon_reward_item_expiretime, time));
            mExchangeTxt.setText(Utils.getString(R.string.conpon_reward_item_exchange, info.mExchangeLabel));
            mUseTxt.setText(info.mUseLabel);

            mExpandImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (info.mExpand) {
                        info.mExpand = false;
                        mExpandLayout.setVisibility(View.GONE);
                        mExpandImg.setImageResource(R.drawable.selector_btn_expand_down);
                        mTimeLayout.setBackgroundDrawable(MyApplication.mApplication.getResources().getDrawable(R.drawable.shape_coupon_bottombg));
                    } else {
                        info.mExpand = true;
                        mExpandLayout.setVisibility(View.VISIBLE);
                        mExpandImg.setImageResource(R.drawable.selector_btn_expand_up);
                        mTimeLayout.setBackgroundColor(MyApplication.mApplication.getResources().getColor(R.color.white));
                    }
                }
            });

            mCopyUseBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Utils.copy(info.mCode);
                    Utils.showShortToast("复制成功!");
                }
            });
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