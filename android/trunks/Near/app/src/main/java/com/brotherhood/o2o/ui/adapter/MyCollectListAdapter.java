package com.brotherhood.o2o.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.MyCollectBean;
import com.brotherhood.o2o.bean.nearby.FoodPrice;
import com.brotherhood.o2o.lib.multiStateView.MultiStateView;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.listener.SwLinViewHolderClickListener;
import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.request.DeleteMyCollectRequest;
import com.brotherhood.o2o.ui.activity.OverseaFoodDetailActivity;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.brotherhood.o2o.ui.widget.SwLin;
import com.brotherhood.o2o.ui.widget.YelpRatingView;
import com.brotherhood.o2o.ui.widget.nearby.FoodPriceLevelView;
import com.brotherhood.o2o.ui.widget.nearby.FoodScoreView;

import java.util.List;

/**
 * Created by laimo.li on 2016/1/6.
 */
public class MyCollectListAdapter extends SwLinLoadMoreRecylerAdatper<MyCollectBean, MyCollectListAdapter.ViewHolder> implements SwLinViewHolderClickListener {

    public static final int HEIGHT_ITEM = -993;

    private Context mContext;

    private int type;

    private MultiStateView stateView;

    public MyCollectListAdapter(Context context, List<MyCollectBean> list, MultiStateView stateView, int type) {
        super(list);
        this.mContext = context;
        this.stateView = stateView;
        this.type = type;
    }

    @Override
    protected boolean getShowFootView() {
        return false;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mList.size() - 1) {
            return FOOTER;
        } else {
            MyCollectBean bean = mList.get(position);
            int showCount = 0;
            if (bean.price != null) {
                showCount++;
            }
            if (!TextUtils.isEmpty(bean.rating) ) {
                if(!bean.rating.equals("0")) {
                    showCount++;
                }
            }
            if (!TextUtils.isEmpty(bean.yelp_rating)) {
                showCount++;
            }
            if (showCount > 2) {
                return HEIGHT_ITEM;
            } else {
                return ITEM;
            }
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEIGHT_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_collect_list_height_item_view, null);
            return new ViewHolder(v, this);
        }
        return super.onCreateViewHolder(parent, viewType);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == HEIGHT_ITEM) {
            onBindItemViewHolder((ViewHolder) holder, getItem(position), position);
            if (holder instanceof SwLinViewHolder) {
                mSwLinController.put(position, ((SwLinViewHolder) holder).swLinLayout);
            }
        } else {
            super.onBindViewHolder(holder, position);
        }
    }

    @Override
    protected ViewHolder onCreateItemViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.my_collect_list_item_view, null);
        return new ViewHolder(itemView, this);
    }

    @Override
    protected void onBindItemViewHolder(ViewHolder holder, final MyCollectBean myCollectBean, final int position) {
        holder.tvName.setText(myCollectBean.name);
        holder.tvCategories.setText(myCollectBean.categories);
        holder.yelpRating.rating(myCollectBean.yelp_rating);
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCollect(position, myCollectBean.id, myCollectBean.platform);
            }
        });
        ImageLoaderManager.displayRoundImageByUrl(mContext, holder.ivIcon, myCollectBean.photo, R.mipmap.img_default, 6);

        FoodPrice price = myCollectBean.price;
        String unit = "$";
        int level = 1;
        if (price == null) {
            holder.fpPriceLevel.setVisibility(View.GONE);
        } else {
            holder.fpPriceLevel.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(price.mUnit)) {
                unit = price.mUnit;
            }
            if (price.mLevel > 0) {
                level = price.mLevel;
            }
            holder.fpPriceLevel.setLevel(level, unit);
        }

        if (TextUtils.isEmpty(myCollectBean.rating) || myCollectBean.rating.equals("0")) {
            holder.llfoursquare.setVisibility(View.GONE);
        } else {
            holder.llfoursquare.setVisibility(View.VISIBLE);
            holder.fsForeignFoodScore.setScore(myCollectBean.rating);
        }

        //if (!TextUtils.isEmpty(myCollectBean.yelp_rating) && showCount > 1) {
        //    holder.swLin.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dp2px(170)));
        //} else {
        //    holder.swLin.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dp2px(120)));
        //}

    }

    @Override
    public void onItemClick(int position) {
        OverseaFoodDetailActivity.show(mContext, mList.get(position).id);
    }

    protected static class ViewHolder extends SwLinViewHolder {

        private ImageView ivIcon;
        private TextView tvName;
        private TextView tvCategories;
        private FoodPriceLevelView fpPriceLevel;
        private FoodScoreView fsForeignFoodScore;
        private YelpRatingView yelpRating;

        private Button btnDelete;

        private SwLin swLin;

        private LinearLayout llfoursquare;

        public ViewHolder(View itemView, SwLinViewHolderClickListener listener) {
            super(itemView, listener);
            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvCategories = (TextView) itemView.findViewById(R.id.tvCategories);

            fpPriceLevel = (FoodPriceLevelView) itemView.findViewById(R.id.fpPriceLevel);
            fsForeignFoodScore = (FoodScoreView) itemView.findViewById(R.id.fsForeignFoodScore);

            btnDelete = (Button) itemView.findViewById(R.id.btnDelete);

            yelpRating = (YelpRatingView) itemView.findViewById(R.id.yelpRating);

            llfoursquare = (LinearLayout) itemView.findViewById(R.id.llfoursquare);

            swLin = (SwLin) itemView.findViewById(R.id.swLinLayout);
        }
    }

    private void deleteCollect(final int position, String activity_id, int platform) {
        DeleteMyCollectRequest request = DeleteMyCollectRequest.createDeleteMyCollectRequest(activity_id, type, platform, new OnResponseListener<List<MyCollectBean>>() {
            @Override
            public void onSuccess(int code, String msg, List<MyCollectBean> myCollectBeans, boolean cache) {
                remove(position);
            }

            @Override
            public void onFailure(int code, String msg) {
                ColorfulToast.orange(mContext, msg, 0);
            }
        });
        request.sendRequest();
    }


    public void remove(String id) {
        remove(getPosition(id));
    }


    @Override
    protected void remove(int position) {
        super.remove(position);
        if (mList.isEmpty()) {
            stateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        }
    }

    private int getPosition(String id) {
        int position = -1;
        for (int i = 0; i < mList.size(); i++) {
            MyCollectBean bean = mList.get(i);
            if (bean.id.equals(id)) {
                position = i;
                break;
            }
        }
        return position;
    }


}
