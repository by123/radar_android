package com.brotherhood.o2o.request;

import com.brotherhood.o2o.bean.ResponseResult;
import com.brotherhood.o2o.bean.location.LocationInfo;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.request.base.BaseAppRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 上报地理位置
 * Created by jl.zhang on 2015/12/3.
 */
public class UploadLocationRequest extends BaseAppRequest<ResponseResult<String>> {

    public UploadLocationRequest(Map<String, String> bodyMap, OnBaseResponseListener<ResponseResult<String>> baseResponseListener) {
        super(Constants.URL_POST_LOCATION, Method.POST, true, bodyMap, baseResponseListener);
    }

    public static UploadLocationRequest createUploadLocationRequest(LocationInfo location, OnResponseListener<String> listener){
        Map<String,String> params = new HashMap();
        params.put("longitude", String.valueOf(location.mLongitude));
        params.put("latitude", String.valueOf(location.mLatitude));
        UploadLocationRequest request = new UploadLocationRequest(params, listener);
        return request;
    }
}