package com.brotherhood.o2o.request;

import com.android.volley.Request;
import com.brotherhood.o2o.bean.ResponseResult;
import com.brotherhood.o2o.chat.model.IMGroupInfoBean;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.request.base.BaseAppRequest;
import com.brotherhood.o2o.util.RequestParameterUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by laimo.li on 2016/1/4.
 */
public class GetGroupInfoRequest extends BaseAppRequest<ResponseResult<IMGroupInfoBean>> {

    public GetGroupInfoRequest(String url, OnBaseResponseListener<ResponseResult<IMGroupInfoBean>> baseResponseListener) {
        super(url, Request.Method.GET, true, baseResponseListener);
    }

    public static GetGroupInfoRequest createGroupInfoRequest(String gid, OnResponseListener<IMGroupInfoBean> listener) {
        Map<String, String> params = new HashMap<>();
        params.put("conference_id", gid);
        String getUrl = RequestParameterUtil.buildParamsUrl(Constants.URL_GET_GROUP_INFO, params);
        return new GetGroupInfoRequest(getUrl, listener);
    }


}
