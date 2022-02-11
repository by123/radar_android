package com.brotherhood.o2o.request;

import com.android.volley.Request;
import com.brotherhood.o2o.bean.ResponseResult;
import com.brotherhood.o2o.bean.radar.RadarItem;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.manager.LocationManager;
import com.brotherhood.o2o.request.base.BaseAppRequest;
import com.brotherhood.o2o.util.RequestParameterUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jl.zhang on 2015/12/4.
 */
public class RadarDataRequest extends BaseAppRequest<ResponseResult<RadarItem>> {

    public RadarDataRequest(String url, OnBaseResponseListener<ResponseResult<RadarItem>> baseResponseListener) {
        super(url, Request.Method.GET, true, null, baseResponseListener);
    }
    /**
     * longitude: 经度
     * latitude: 纬度
     * distance: 覆盖范围, 默认5KM
     * offset: 偏移值，默认O
     * size： 显示记录条数，默认50
     */
    public static RadarDataRequest createRadarDataRequest(OnResponseListener<RadarItem> listener){
        Map<String,String> params = new HashMap();
        String latitude = String.valueOf(LocationManager.getInstance().getCachedCurrentAddressOrNil().mLatitude);
        String longitude = String.valueOf(LocationManager.getInstance().getCachedCurrentAddressOrNil().mLongitude);
        //String distance = String.valueOf(Constants.dLargestDistance);
        params.put("longitude", longitude);
        params.put("latitude", latitude);
        //params.put("distance", distance);

        String getUrl = RequestParameterUtil.buildParamsUrl(Constants.URL_GET_RADAR, params);

        return new RadarDataRequest(getUrl, listener);
    }
}
