package com.brotherhood.o2o.request;

import com.brotherhood.o2o.bean.MyCollectBean;
import com.brotherhood.o2o.bean.ResponseResult;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.request.base.BaseAppRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by laimo.li on 2016/1/6.
 */
public class DeleteMyCollectRequest extends BaseAppRequest<ResponseResult<List<MyCollectBean>>> {

    public DeleteMyCollectRequest(Map<String, String> bodyMap,  OnBaseResponseListener<ResponseResult<List<MyCollectBean>>> baseResponseListener) {
        super(Constants.URL_GET_CANCEL_MY_COLLECT, Method.POST, true, bodyMap, baseResponseListener);
    }


    public static DeleteMyCollectRequest createDeleteMyCollectRequest(String activity_id, int type, int platform, OnResponseListener<List<MyCollectBean>> listener) {
        Map<String, String> params = new HashMap<>();
        params.put("activity_id", activity_id);
        params.put("type", String.valueOf(type));
        params.put("platform", String.valueOf(platform));
        return new DeleteMyCollectRequest(params, listener);
    }
}
