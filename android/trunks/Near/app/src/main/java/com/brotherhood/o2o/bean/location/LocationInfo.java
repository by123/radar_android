package com.brotherhood.o2o.bean.location;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by jl.zhang on 2015/12/4.
 */
public class LocationInfo implements Serializable{

    //纬度
    @JSONField(name = "lat")
    public double mLatitude;
    //经度
    @JSONField(name = "lon")
    public double mLongitude;

    public String mAddress;
    public String mBuildingName;


}
