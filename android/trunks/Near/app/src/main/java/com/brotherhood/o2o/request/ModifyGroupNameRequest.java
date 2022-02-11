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
public class ModifyGroupNameRequest extends BaseAppRequest<ResponseResult<IMGroupInfoBean>> {

    public ModifyGroupNameRequest(String url, OnBaseResponseListener<ResponseResult<IMGroupInfoBean>> baseResponseListener) {
        super(url, Request.Method.GET, true, baseResponseListener);
    }

    public static ModifyGroupNameRequest createModifyGroupNameRequest(String title, OnResponseListener<IMGroupInfoBean> listener) {
        Map<String, String> params = new HashMap<>();
        params.put("title", title);
        String getUrl = RequestParameterUtil.buildParamsUrl(Constants.URL_GET_MODIFY_GROUP_NAME, params);
        return new ModifyGroupNameRequest(getUrl, listener);
    }

}
