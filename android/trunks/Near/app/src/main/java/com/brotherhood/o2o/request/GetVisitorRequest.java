package com.brotherhood.o2o.request;

import android.text.TextUtils;

import com.brotherhood.o2o.bean.ResponseResult;
import com.brotherhood.o2o.bean.account.WrapperVisitor;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.request.base.BaseAppRequest;
import com.brotherhood.o2o.util.RequestParameterUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * URL_GET_VISITOR
 * Created by jl.zhang on 2015/12/22.
 */
public class GetVisitorRequest extends BaseAppRequest<ResponseResult<WrapperVisitor>> {
    public GetVisitorRequest(String url, OnBaseResponseListener<ResponseResult<WrapperVisitor>> baseResponseListener) {
        super(url, Method.GET, true, baseResponseListener);
    }

    /**
     * @param time      获取time这个时间戳之前的访客，如果不传此时，从最近的开始返回
     * @param limit     返回limit条访客记录，这个是为了分页和减少没有必要的流量
     * @param listener
     * @return
     */
    public static GetVisitorRequest createVisitorRequest(String time, String limit, OnResponseListener<WrapperVisitor> listener){
        Map<String,String> params = new HashMap<>();
        if (!TextUtils.isEmpty(time)) {
            params.put("time", time);
        }
        if(!TextUtils.isEmpty(limit)) {
            params.put("limit", limit);
        }
        String getUrl = RequestParameterUtil.buildParamsUrl(Constants.URL_GET_VISITOR, params);
        return new GetVisitorRequest(getUrl, listener);
    }
}
