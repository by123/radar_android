package com.brotherhood.o2o.request;

import com.brotherhood.o2o.bean.ResponseResult;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.request.base.BaseAppRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 举报
 * Created by jl.zhang on 2015/12/24.
 */
public class ReportUserRequest extends BaseAppRequest<ResponseResult<String>> {
    public ReportUserRequest(Map<String, String> bodyMap, OnBaseResponseListener<ResponseResult<String>> baseResponseListener) {
        super(Constants.URL_POST_REPORT, Method.POST, true, bodyMap, baseResponseListener);
    }

    public static ReportUserRequest createReportUserRequest(String uid, String reason, OnResponseListener<String> listener){
        Map<String,String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("reason", reason);
        return new ReportUserRequest(params, listener);
    }
}
