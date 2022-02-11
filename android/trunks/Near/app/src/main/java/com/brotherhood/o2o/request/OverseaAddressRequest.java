package com.brotherhood.o2o.request;

import com.brotherhood.o2o.bean.GooglePoiInfo;
import com.brotherhood.o2o.bean.GoogleResponseResult;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.listener.OnGooglePoiResponseListener;
import com.brotherhood.o2o.request.base.BaseAppRequest;

import java.util.List;

/**
 * 谷歌，根据经纬度获取地址
 * Created by jl.zhang on 2016/1/4.
 */
public class OverseaAddressRequest extends BaseAppRequest<GoogleResponseResult<List<GooglePoiInfo>>> {
    public OverseaAddressRequest(String url, OnBaseResponseListener<GoogleResponseResult<List<GooglePoiInfo>>> baseResponseListener) {
        super(url, Method.GET, false, null, baseResponseListener);
    }

    public static OverseaAddressRequest createAddressRequest(String latlng, OnGooglePoiResponseListener<List<GooglePoiInfo>> listener){
        String requestURL = "http://maps.google.com/maps/api/geocode/json?latlng=" + latlng + "&language=en-us&sensor=false";
        return new OverseaAddressRequest(requestURL, listener);
    }
}
