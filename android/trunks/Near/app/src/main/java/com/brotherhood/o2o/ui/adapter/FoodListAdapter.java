package com.brotherhood.o2o.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.nearby.FoodListItem;
import com.brotherhood.o2o.bean.nearby.FoodPrice;
import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.manager.LocationManager;
import com.brotherhood.o2o.ui.activity.OverseaFoodDetailActivity;
import com.brotherhood.o2o.ui.widget.YelpRatingView;
import com.brotherhood.o2o.ui.widget.nearby.FoodPriceLevelView;
import com.brotherhood.o2o.ui.widget.nearby.FoodScoreView;
import com.brotherhood.o2o.util.DistanceFormatUtil;
import com.brotherhood.o2o.util.ViewUtil;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * 美食列表适配器
 * Created by jl.zhang on 2015/12/28.
 */
public class FoodListAdapter extends LoadMoreRecylerAdatper<FoodListItem, FoodListAdapter.FoodViewHolder> {

    private Context mContext;

    public FoodListAdapter(Context context, List<FoodListItem> list) {
        super(list);
        mContext = context;
    }

    @Override
    protected FoodViewHolder onCreateItemViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.nearby_food_item, null);
        return new FoodViewHolder(itemView);
    }

    @Override
    protected void onBindItemViewHolder(FoodViewHolder holder, final FoodListItem foreignFood, int position) {
        if (foreignFood.mPhoto != null) {
            ImageLoaderManager.displayRoundImageByUrl(mContext, holder.mIvIcon, foreignFood.mPhoto, R.mipmap.img_default, 5);
        } else {
            ImageLoaderManager.displayImageByResource(mContext, holder.mIvIcon, R.mipmap.img_default);
        }
        holder.mTvTitle.setText(foreignFood.mBusinessName);
        holder.mTvType.setText(foreignFood.mFoodType);

        LatLng myLatlng = LocationManager.getInstance().getMyLatlng();
        if (myLatlng != null) {
            LatLng destPoint = new LatLng(foreignFood.mLatitude, foreignFood.mLongitude);
            holder.mTvDistance.setVisibility(View.VISIBLE);
            holder.mTvDistance.setText(DistanceFormatUtil.getGoogleMapDistance(mContext, myLatlng, destPoint));
        } else {
            holder.mTvDistance.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(foreignFood.isOpen)) {
            holder.mTvClose.setVisibility(View.GONE);
        } else {
            int open = Integer.valueOf(foreignFood.isOpen);
            if (open == 0) {
                holder.mTvClose.setVisibility(View.VISIBLE);
            } else {
                holder.mTvClose.setVisibility(View.GONE);
            }
        }
        if (foreignFood.mCollection == 1) {
            holder.mIvCollect.setVisibility(View.VISIBLE);
        } else {
            holder.mIvCollect.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(foreignFood.mVote)) {
            holder.mTvVote.setVisibility(View.GONE);
        } else {
            holder.mTvVote.setVisibility(View.VISIBLE);
            holder.mTvVote.setText(mContext.getString(R.string.foreign_food_vote, foreignFood.mVote));
        }

        if (TextUtils.isEmpty(foreignFood.mFoodScore)) {
            holder.mFoodScoreView.setVisibility(View.GONE);
        } else {
            holder.mFoodScoreView.setVisibility(View.VISIBLE);
            holder.mFoodScoreView.setScore(foreignFood.mFoodScore);
        }
        if ((TextUtils.isEmpty(foreignFood.mFoodScore) || foreignFood.mFoodScore.equals("0")) && (TextUtils.isEmpty(foreignFood.mVote) || foreignFood.mVote.equals("0"))) {
            ViewUtil.toggleView(holder.llfoursquare,false);
        }else{
            ViewUtil.toggleView(holder.llfoursquare,true);
        }

        FoodPrice price = foreignFood.mPrice;
        String unit = "$";
        int level = 1;
        if (price == null) {
            holder.mPriceLevel.setVisibility(View.GONE);
        } else {
            holder.mPriceLevel.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(price.mUnit)) {
                unit = price.mUnit;
            }
            if (price.mLevel > 0) {
                level = price.mLevel;
            }
            holder.mPriceLevel.setLevel(level, unit);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OverseaFoodDetailActivity.show(mContext, foreignFood.mBusinessId);
            }
        });

        holder.yelpRating.rating(foreignFood.mYelpRating);
        holder.yelpRating.reviews(foreignFood.mReviews);
    }

    protected static class FoodViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvTitle;
        private TextView mTvType;
        private TextView mTvDistance;
        private TextView mTvClose;
        private TextView mTvVote;
        private FoodScoreView mFoodScoreView;
        private ImageView mIvIcon;
        private FoodPriceLevelView mPriceLevel;
        private ImageView mIvCollect;
        private YelpRatingView yelpRating;
        private LinearLayout llfoursquare;

        public FoodViewHolder(View itemView) {
            super(itemView);
            mIvIcon = (ImageView) itemView.findViewById(R.id.ivForeignFoodIcon);
            mTvClose = (TextView) itemView.findViewById(R.id.tvForeignClosed);
            mTvType = (TextView) itemView.findViewById(R.id.tvForeignFoodType);
            mTvTitle = (TextView) itemView.findViewById(R.id.tvForeignFoodTitle);
            mTvDistance = (TextView) itemView.findViewById(R.id.tvForeignFoodDistance);
            mTvVote = (TextView) itemView.findViewById(R.id.tvForeignFoodVotes);
            mFoodScoreView = (FoodScoreView) itemView.findViewById(R.id.fsForeignFoodScore);
            mPriceLevel = (FoodPriceLevelView) itemView.findViewById(R.id.fpForeignFoodPriceLevel);
            mIvCollect = (ImageView) itemView.findViewById(R.id.ivFoodListCollect);
            yelpRating = (YelpRatingView) itemView.findViewById(R.id.yelpRating);
            llfoursquare = (LinearLayout) itemView.findViewById(R.id.llfoursquare);
        }
    }
}
