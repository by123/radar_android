package com.brotherhood.o2o.request;

import com.brotherhood.o2o.bean.ResponseResult;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.request.base.BaseAppRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jl.zhang on 2015/12/24.
 */
public class LikeUserRequest extends BaseAppRequest<ResponseResult<String>> {
    public LikeUserRequest(Map<String, String> bodyMap, OnBaseResponseListener<ResponseResult<String>> baseResponseListener) {
        super(Constants.URL_POST_LIKE, Method.POST, true, bodyMap, baseResponseListener);
    }

    public static LikeUserRequest createLikeRequest(String uid, OnResponseListener<String> listener){
        Map<String,String> params = new HashMap<>();
        params.put(" like_uid", uid);
        return new LikeUserRequest(params,listener);
    }
}
