package com.brotherhood.o2o.bean.nearby;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by jl.zhang on 2016/1/4.
 */
public class GoogleLocation {

    //纬度
    @JSONField(name = "lat")
    public double mLatitude;
    //经度
    @JSONField(name = "lng")
    public double mLongitude;
}
