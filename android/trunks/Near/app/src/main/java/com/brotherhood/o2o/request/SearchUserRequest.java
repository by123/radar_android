package com.brotherhood.o2o.request;

import com.brotherhood.o2o.bean.ResponseResult;
import com.brotherhood.o2o.bean.SearchUserBean;
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
public class SearchUserRequest extends BaseAppRequest<ResponseResult<List<SearchUserBean>>> {


    public SearchUserRequest(String url, OnBaseResponseListener<ResponseResult<List<SearchUserBean>>> baseResponseListener) {
        super(url, Method.GET, true, baseResponseListener);
    }

    public static SearchUserRequest createSearchUserRequest(String nickname,OnResponseListener<List<SearchUserBean>> listener) {
        Map<String, String> params = new HashMap<>();
        params.put("nickname",nickname);
        String getUrl = RequestParameterUtil.buildParamsUrl(Constants.URL_GET_SEARCH_FRIENDS, params);
        return new SearchUserRequest(getUrl, listener);
    }
}