package com.brotherhood.o2o.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.nearby.FoodComment;
import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.ui.widget.YelpRatingView;
import com.brotherhood.o2o.util.DateUtil;

import java.util.List;

/**
 * Created by jl.zhang on 2015/12/30.
 */
public class FoodDetailCommentAdapter extends BaseAdapter {

    private Context mContext;
    private boolean hasMore;
    private List<FoodComment> mFoodCommentList;
    private OnMoreCommentCallBack mCallBack;

    public FoodDetailCommentAdapter(Context context, List<FoodComment> foodComments) {
        mContext = context;
        mFoodCommentList = foodComments;
        if (foodComments == null || foodComments.size() <= 3) {
            hasMore = false;
        } else {
            hasMore = true;
        }
    }

    public void replaceAll(List<FoodComment> elem) {
        mFoodCommentList.clear();
        mFoodCommentList.addAll(elem);
        notifyDataSetChanged();
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    @Override
    public int getCount() {
        if (mFoodCommentList == null || mFoodCommentList.isEmpty()) {
            return 0;
        }
        return mFoodCommentList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFoodCommentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentHolder holder = null;
        if (convertView == null || convertView.getTag() == null) {
            holder = new CommentHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.oversea_food_comment_item, null);
            holder.mViDivide = convertView.findViewById(R.id.viFoodCommentDivide);
            holder.mLlMore = (LinearLayout) convertView.findViewById(R.id.llFoodCommentMore);
            holder.mIvIcon = (ImageView) convertView.findViewById(R.id.ivFoodCommentIcon);
            holder.mTvTitle = (TextView) convertView.findViewById(R.id.tvFoodCommentTitle);
            holder.mTvTime = (TextView) convertView.findViewById(R.id.tvFoodCommentTime);
            holder.mTvContent = (TextView) convertView.findViewById(R.id.tvFoodCommentContent);
            holder.mIvImage = (ImageView) convertView.findViewById(R.id.ivFoodCommentImage);
            holder.mFrom = (ImageView) convertView.findViewById(R.id.ivFrom);
            holder.yelpRatingView = (YelpRatingView) convertView.findViewById(R.id.yelpRatingView);
            convertView.setTag(holder);
        } else {
            holder = (CommentHolder) convertView.getTag();
        }
        FoodComment foodComment = mFoodCommentList.get(position);


        if (position == mFoodCommentList.size() - 1) {
            holder.mViDivide.setVisibility(View.GONE);
        } else {
            holder.mViDivide.setVisibility(View.VISIBLE);
        }
        if ((position == mFoodCommentList.size() - 1) && hasMore) {
            holder.mLlMore.setVisibility(View.VISIBLE);
        } else {
            holder.mLlMore.setVisibility(View.GONE);
        }
        if(foodComment.from.equals("foursquare")){
            holder.mFrom.setImageResource(R.mipmap.ic_details_foursquare_normal);
        }else{
            holder.mFrom.setImageResource(R.mipmap.ic_details_yelp_normal);
        }
        holder.yelpRatingView.rating(foodComment.rating,false);
        FoodComment.User user = foodComment.user;
        ImageLoaderManager.displayCircleImageByUrl(mContext, holder.mIvIcon, user.avatar, R.mipmap.ic_msg_default);
        holder.mTvTitle.setText(user.name);
        //String todayStr = DateUtil.parseUnixTimeToString(System.currentTimeMillis(), "yyyy-MM-dd");
        String commentStr = DateUtil.parseUnixTimeToString(foodComment.mCommentTime, "yyyy-MM-dd");
        //if (todayStr.equalsIgnoreCase(commentStr)) {
        //    holder.mTvTime.setText(DateUtil.parseUnixTimeToString(foodComment.mCommentTime, "HH:mm"));
        //} else {
            holder.mTvTime.setText(commentStr);
        //}
        holder.mTvContent.setText(foodComment.mContent);
        if (TextUtils.isEmpty(foodComment.mCommentImage)) {
            holder.mIvImage.setVisibility(View.GONE);
        } else {
            holder.mIvImage.setVisibility(View.VISIBLE);
            ImageLoaderManager.displayImageByUrl(mContext, holder.mIvImage, foodComment.mCommentImage, R.mipmap.img_default);
        }
        holder.mLlMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack == null) {
                    return;
                }
                mCallBack.clickMore();
            }
        });
        return convertView;
    }

    public void setOnMoreCommentCallBack(OnMoreCommentCallBack callBack) {
        mCallBack = callBack;
    }

    public interface OnMoreCommentCallBack {
        void clickMore();
    }

    private static class CommentHolder {
        public View mViDivide;
        public LinearLayout mLlMore;
        public ImageView mIvIcon;
        public TextView mTvTitle;
        public TextView mTvTime;
        public TextView mTvContent;
        public ImageView mIvImage;
        public ImageView mFrom;
        public YelpRatingView yelpRatingView;
    }
}
