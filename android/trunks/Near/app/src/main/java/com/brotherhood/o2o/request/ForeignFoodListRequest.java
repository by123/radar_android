package com.brotherhood.o2o.request;

import android.text.TextUtils;

import com.brotherhood.o2o.bean.ResponseResult;
import com.brotherhood.o2o.bean.nearby.FoodListItem;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.request.base.BaseAppRequest;
import com.brotherhood.o2o.util.RequestParameterUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jl.zhang on 2015/12/29.
 * 海外版美食列表
 */
public class ForeignFoodListRequest extends BaseAppRequest<ResponseResult<List<FoodListItem>>> {
    public ForeignFoodListRequest(String url, OnBaseResponseListener<ResponseResult<List<FoodListItem>>> baseResponseListener) {
        super(url, Method.GET, true, baseResponseListener);
    }

    /**
     * @param longitude 经度
     * @param latitude  纬度
     * @param pageNo    页码
     * @param limit     单页数量（最大40）
     * @param keyword     搜索关键词
     * @param listener
     * @return
     */
    public static ForeignFoodListRequest createFoodListRequest(double longitude, double latitude, int pageNo, int limit, String keyword, OnResponseListener<List<FoodListItem>> listener) {
        Map<String, String> params = new HashMap<>();
        params.put("longitude", String.valueOf(longitude));
        params.put("latitude", String.valueOf(latitude));
        params.put("page", String.valueOf(pageNo));
        params.put("limit", String.valueOf(limit));
        if (!TextUtils.isEmpty(keyword)){
            params.put("keyword", keyword);
        }
        String url = "http://192.168.122.82:8088/foods/overseas_list.json";
        String getUrl = RequestParameterUtil.buildParamsUrl(Constants.URL_GET_FOREIGN_FOODLIST /*url*/ , params);
        return new ForeignFoodListRequest(getUrl, listener);
    }
}
