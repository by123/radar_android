package com.brotherhood.o2o.request;

import com.brotherhood.o2o.bean.MyFriendBean;
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
 * Created by laimo.li on 2015/12/24.
 */
public class GetMyFriendsRequest extends BaseAppRequest<ResponseResult<List<MyFriendBean>>> {


    public GetMyFriendsRequest(String url, OnBaseResponseListener<ResponseResult<List<MyFriendBean>>> baseResponseListener) {
        super(url, Method.GET, true, baseResponseListener);
    }

    public static GetMyFriendsRequest createMyFriendsRequest(OnResponseListener<List<MyFriendBean>> listener) {
        Map<String, String> params = new HashMap<>();
        String getUrl = RequestParameterUtil.buildParamsUrl(Constants.URL_GET_MY_FRIENDS, params);
        return new GetMyFriendsRequest(getUrl, listener);
    }
}
