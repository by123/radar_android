package com.brotherhood.o2o.request;

import com.brotherhood.o2o.bean.BaiduResponseResult;
import com.brotherhood.o2o.listener.OnBaiduPoiResponseListener;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.request.base.BaseAppRequest;

/**
 * 根据经纬度获取周边POI(调用百度平台提供的接口)
 * Created by jl.zhang on 2015/12/4.
 */
public class NearByBuildingRequest extends BaseAppRequest<BaiduResponseResult<String>> {

    public NearByBuildingRequest(String url, OnBaseResponseListener<BaiduResponseResult<String>> baseResponseListener) {
        super(url, Method.GET, false, null, baseResponseListener);
    }
    /**
     * 根据经纬度获取周边POI(调用百度平台提供的接口)
     */
    public static NearByBuildingRequest createNearByBuildingRequest(String url, OnBaiduPoiResponseListener<String> listener){
        return new NearByBuildingRequest(url, listener);
    }
}
