package com.brotherhood.o2o.bean.radar;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by jl.zhang on 2015/12/8.
 */
public class RadarEvent extends RadarPoi{
    @JSONField(name = "id")
    public long mId;

    @JSONField(name = "mode_id")
    public long mModeId;

    @JSONField(name = "title")
    public String mTitle;

    @JSONField(name = "icon")
    public String mIcon;

    @JSONField(name = "icon_in")
    public String mIconIn;

    @JSONField(name = "logo")
    public String mLogo;

    @JSONField(name = "distance")
    public long mDistance;

    @JSONField(name = "priority")
    public int mPriority;

    @JSONField(name = "supplier")
    public String mSupplier;

    @JSONField(name = "place")
    public String mPlace;

    @JSONField(name = "price")
    public double mPrice;

    @JSONField(name = "service_radius")
    public String mServiceRadius;

    @JSONField(name = "beacon_id")
    public String mBeaconId;


    @JSONField(name = "url")
    public String mWebEventUrl;

    @JSONField(name = "end_time")
    public String mEndTime;

}
