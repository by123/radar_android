package com.brotherhood.o2o.model.explore;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by jl.zhang on 2015/12/4.
 */
public class LocationInfo {

    //纬度
    @JSONField(name = "lat")
    public double mLatitude;
    //经度
    @JSONField(name = "lon")
    public double mLontitude;
}
