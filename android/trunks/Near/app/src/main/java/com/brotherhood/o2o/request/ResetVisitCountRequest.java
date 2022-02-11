package com.brotherhood.o2o.request;

import com.brotherhood.o2o.bean.ResponseResult;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.request.base.BaseAppRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jl.zhang on 2015/12/25.
 * 重置最新访客数量
 */
public class ResetVisitCountRequest extends BaseAppRequest<ResponseResult<String>>{
    public ResetVisitCountRequest(Map<String, String> bodyMap, OnBaseResponseListener<ResponseResult<String>> baseResponseListener) {
        super(Constants.URL_POST_RESET_VISITCOUNT, Method.POST, true, bodyMap, baseResponseListener);
    }

    public static ResetVisitCountRequest createResetVisitCountRequest(OnResponseListener<String> listener){
        return new ResetVisitCountRequest(new HashMap<String,String>(), listener);
    }

}
