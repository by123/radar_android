package com.brotherhood.o2o.bean.nearby;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by jl.zhang on 2015/12/29.
 */
public class FoodDetail {

    @JSONField(name = "id")
    public String mBusinessId;

    @JSONField(name = "name")
    public String mBusinessName;

    @JSONField(name = "lat")
    public double mLatitude;

    @JSONField(name = "lng")
    public double mLongitude;

    @JSONField(name = "price")
    public FoodPrice mFoodPrice;

    @JSONField(name = "timeZone")
    public String mTimeZone;

    @JSONField(name = "contact")
    public String phoneNo;

    @JSONField(name = "timeframes")
    public FoodTime mFoodTime;

    @JSONField(name = "photos")
    public FoodPhoto mFoodPhoto;

    @JSONField(name = "categories")
    public String mFoodType;

    @JSONField(name = "tips")
    public FoodCommentWrapper mCommentWrapper;

    @JSONField(name = "menu")
    public String mMenu;

    @JSONField(name = "rating")
    public String mScore;

    @JSONField(name = "ratingSignals")
    public String mVotes;

    @JSONField(name = "collection")
    public int mCollection;

    @JSONField(name = "open_table")
    public String mOpenTable;

    @JSONField(name = "yelp_rating")
    public String mYelpRating;

    @JSONField(name = "yelp_rating_count")
    public String mYelpRatingCount;

    //@JSONField(name = "photos")
    //public Photos photos;
    //
    //public class Photos{
    //
    //    @JSONField(name = "count")
    //    public int count;
    //
    //    @JSONField(name = "photos")
    //    public List<String> photos;
    //
    //}

}
