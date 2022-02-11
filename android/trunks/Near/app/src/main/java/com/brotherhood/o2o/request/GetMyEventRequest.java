package com.brotherhood.o2o.request;

import com.brotherhood.o2o.bean.ResponseResult;
import com.brotherhood.o2o.bean.radar.RadarEvent;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.request.base.BaseAppRequest;
import com.brotherhood.o2o.util.RequestParameterUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 我的活动
 * Created by jl.zhang on 2015/12/21.
 */
public class GetMyEventRequest extends BaseAppRequest<ResponseResult<RadarEvent>> {
    /**
     * @param baseResponseListener 回调函数
     */
    public GetMyEventRequest(String url, OnBaseResponseListener<ResponseResult<RadarEvent>> baseResponseListener) {
        super(url, Method.GET, true, baseResponseListener);
    }

    /**
     *

     category 列表类型，1、惊喜，2、优惠券， 3、跑团活动列表
     timestamp 最老一条的的时间戳，即列表中时间戳最小值
     size 返回的记录条件


     * @return
     */
    public static GetMyEventRequest createEventRequest(int category, String timestamp, int size, OnResponseListener<RadarEvent> listener){
        Map<String, String> params = new HashMap<>();
        params.put("category", String.valueOf(category));
        params.put("timestamp", timestamp);
        params.put("size", String.valueOf(size));
        String getUrl = RequestParameterUtil.buildParamsUrl(Constants.URL_ORDER_LIST, params);
        return new GetMyEventRequest(getUrl, listener);
    }
}
