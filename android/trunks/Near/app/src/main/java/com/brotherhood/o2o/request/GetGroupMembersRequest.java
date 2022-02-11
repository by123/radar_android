package com.brotherhood.o2o.request;

import com.brotherhood.o2o.bean.GroupUserBean;
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
 * Created by laimo.li on 2016/1/4.
 */
public class GetGroupMembersRequest extends BaseAppRequest<ResponseResult<List<GroupUserBean>>> {

    public GetGroupMembersRequest(String url, OnBaseResponseListener<ResponseResult<List<GroupUserBean>>> baseResponseListener) {
        super(url, Method.GET, true, baseResponseListener);
    }

    public static GetGroupMembersRequest createGroupMembersRequest(String gid, OnResponseListener<List<GroupUserBean>> listener) {
        Map<String, String> params = new HashMap<>();
        params.put("conference_id", gid);
        String getUrl = RequestParameterUtil.buildParamsUrl(Constants.URL_GET_MEMBERS, params);
        return new GetGroupMembersRequest(getUrl, listener);
    }



}
