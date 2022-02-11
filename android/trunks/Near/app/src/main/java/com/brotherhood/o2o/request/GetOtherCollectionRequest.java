package com.brotherhood.o2o.request;

import com.brotherhood.o2o.bean.ResponseResult;
import com.brotherhood.o2o.bean.account.CollectionWrapper;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.request.base.BaseAppRequest;
import com.brotherhood.o2o.util.RequestParameterUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取他人收藏信息
 * Created by jl.zhang on 2015/12/25.
 */
public class GetOtherCollectionRequest extends BaseAppRequest<ResponseResult<CollectionWrapper>> {
    public GetOtherCollectionRequest(String url, OnBaseResponseListener<ResponseResult<CollectionWrapper>> baseResponseListener) {
        super(url, Method.GET, true, baseResponseListener);
    }

    /**
     *
     * @param uid 查看的用户id
     * @param collectType 收藏类型：1、想玩，2、想吃，3、想看（可选，默认为1）
     * @param listener
     * @return
     */
    public static GetOtherCollectionRequest createOtherCollectRequest(String uid, int collectType, OnResponseListener<CollectionWrapper> listener){
        Map<String,String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("type", String.valueOf(collectType));
        String getUrl = RequestParameterUtil.buildParamsUrl(Constants.URL_GET_OTHER_COLLECTION, params);
        return new GetOtherCollectionRequest(getUrl, listener);
    }
}
