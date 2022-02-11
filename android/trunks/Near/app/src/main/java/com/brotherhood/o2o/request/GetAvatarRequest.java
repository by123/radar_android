package com.brotherhood.o2o.request;

import com.brotherhood.o2o.bean.UserInfoBean;
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
 * Created by laimo.li on 2015/12/28.
 */
public class GetAvatarRequest extends BaseAppRequest<ResponseResult<List<UserInfoBean>>> {


    public GetAvatarRequest(String url, OnBaseResponseListener<ResponseResult<List<UserInfoBean>>> baseResponseListener) {
        super(url, Method.GET, true, baseResponseListener);
    }

    public static GetAvatarRequest createAvatarRequest(String uid, OnResponseListener<List<UserInfoBean>> listener) {
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        String getUrl = RequestParameterUtil.buildParamsUrl(Constants.URL_GET_AVATAR, params);
        return new GetAvatarRequest(getUrl, listener);
    }


}
