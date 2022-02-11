package com.brotherhood.o2o.request;

import com.brotherhood.o2o.bean.ResponseResult;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.request.base.BaseAppRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 收藏接口
 */
public class CollectEventRequest extends BaseAppRequest<ResponseResult<String>>{


    public CollectEventRequest(Map<String, String> bodyMap, OnBaseResponseListener<ResponseResult<String>> baseResponseListener) {
        super(Constants.URL_POST_COLLECT, Method.POST, true, bodyMap, baseResponseListener);
    }

    /**
     *
     * @param activityId 活动ID
     * @param type  收藏类型：1、想玩，2、想吃，3、想看（可选，默认为1）
     * @param platform  来源平台，0、Near， 1、大众点评， 2、微票， 3、foursqare
     * @param listener
     * @return
     */
    public static CollectEventRequest createCollectEventRequest(String activityId, int type, int platform, OnResponseListener<String> listener){
        Map<String,String> params = new HashMap<>();
        params.put("activity_id", activityId);
        params.put("type", String.valueOf(type));
        params.put("platform", String.valueOf(platform));
        return new CollectEventRequest(params, listener);
    }
}
