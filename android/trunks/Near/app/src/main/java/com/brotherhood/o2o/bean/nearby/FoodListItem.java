package com.brotherhood.o2o.bean.nearby;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by jl.zhang on 2015/12/29.
 */
public class FoodListItem implements Serializable{

    @JSONField(name = "id")
    public String mBusinessId;

    @JSONField(name = "name")
    public String mBusinessName;

    @JSONField(name = "photo")
    public String mPhoto;//可能为null

    @JSONField(name = "lat")
    public double mLatitude;

    @JSONField(name = "lng")
    public double mLongitude;

    @JSONField(name = "categories")
    public String mFoodType;

    //以下数据有可能为null
    @JSONField(name = "price")
    public FoodPrice mPrice;

    @JSONField(name = "rating")
    public String mFoodScore;

    @JSONField(name = "ratingSignals")
    public String mVote;

    @JSONField(name = "open")
    public String isOpen;

    @JSONField(name = "collection")
    public int mCollection;

    @JSONField(name = "yelp_rating")
    public String mYelpRating;

    @JSONField(name = "yelp_rating_count")
    public String mReviews;
}
