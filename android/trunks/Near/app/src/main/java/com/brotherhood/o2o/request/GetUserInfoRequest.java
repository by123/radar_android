package com.brotherhood.o2o.request;

import android.text.TextUtils;

import com.brotherhood.o2o.bean.ResponseResult;
import com.brotherhood.o2o.bean.account.WrapperUserInfo;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.request.base.BaseAppRequest;
import com.brotherhood.o2o.util.RequestParameterUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取用户信息
 * Created by jl.zhang on 2015/12/7.
 */
public class GetUserInfoRequest extends BaseAppRequest<ResponseResult<WrapperUserInfo>> {

    public GetUserInfoRequest(String url, int method, boolean hasHead, Map<String, String> bodyMap, OnBaseResponseListener<ResponseResult<WrapperUserInfo>>
            baseResponseListener) {
        super(url, method, hasHead, bodyMap, baseResponseListener);
    }

    public static GetUserInfoRequest createGetUserInfoRequest(String uid,OnResponseListener<WrapperUserInfo> listener){
        Map<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(uid)) {
            params.put("uid", uid);
        }
        String getUrl = RequestParameterUtil.buildParamsUrl(Constants.URL_GET_USERINFO, params);
        return new GetUserInfoRequest(getUrl, Method.GET, true, params, listener);
    }
}
