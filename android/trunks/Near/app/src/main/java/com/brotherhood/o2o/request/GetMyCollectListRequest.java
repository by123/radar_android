package com.brotherhood.o2o.request;

import com.brotherhood.o2o.bean.MyCollectBean;
import com.brotherhood.o2o.bean.ResponseResult;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.request.base.BaseAppRequest;
import com.brotherhood.o2o.util.RequestParameterUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by laimo.li on 2016/1/6.
 */
public class GetMyCollectListRequest extends BaseAppRequest<ResponseResult<List<MyCollectBean>>> {

    public GetMyCollectListRequest(String url, OnBaseResponseListener<ResponseResult<List<MyCollectBean>>> baseResponseListener) {
        super(url, Method.GET, true, baseResponseListener);
    }

    public static GetMyCollectListRequest createMyCollectListRequest (int limit,int offset,int type,OnResponseListener<List<MyCollectBean>> listener) {
        Map<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(limit));
        params.put("offset", String.valueOf(offset));
        params.put("type", String.valueOf(type));
        String getUrl = RequestParameterUtil.buildParamsUrl(Constants.URL_GET_MY_COLLECT_LIST, params);
        return new GetMyCollectListRequest(getUrl, listener);
    }
}
