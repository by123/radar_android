package com.brotherhood.o2o.bean.radar;

import android.view.View;

import com.alibaba.fastjson.annotation.JSONField;
import com.brotherhood.o2o.bean.location.LocationInfo;

import java.io.Serializable;

/**
 * 信标通用数据
 * Created by jl.zhang on 2015/12/12.
 */
public abstract class RadarPoi implements Serializable{
    @JSONField(name = "location")
    public LocationInfo mLocation;

    /**
     * 计算数据
     */
    //角度
    public double mDegree;
    //实际雷达X位置
    public double mPosX;
    //实际雷达Y位置
    public double mPosY;
    //绑定view
    public View mHeadView;
    //是否展示
    public boolean isShow;
    //用于区分人[0]、服务[1]等信标
    public int mType;
}
