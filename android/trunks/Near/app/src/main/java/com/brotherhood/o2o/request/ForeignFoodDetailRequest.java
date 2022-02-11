package com.brotherhood.o2o.request;

import com.brotherhood.o2o.bean.ResponseResult;
import com.brotherhood.o2o.bean.nearby.FoodDetail;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.request.base.BaseAppRequest;
import com.brotherhood.o2o.util.RequestParameterUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jl.zhang on 2015/12/29.
 * 海外版美食详情
 */
public class ForeignFoodDetailRequest extends BaseAppRequest<ResponseResult<FoodDetail>> {


    public ForeignFoodDetailRequest(String url, OnBaseResponseListener<ResponseResult<FoodDetail>> baseResponseListener) {
        super(url, Method.GET, true, baseResponseListener);
    }

    /**
     * @param businessId 商家ID
     * @param listener
     * @return
     */
    public static ForeignFoodDetailRequest createFoodDetailRequest(String businessId, OnResponseListener<FoodDetail> listener){
        Map<String,String> params = new HashMap<>();
        params.put("business_id", businessId);

        String url = "http://192.168.122.82:8088/foods/overseas_detail.json";
        String getUrl = RequestParameterUtil.buildParamsUrl(Constants.URL_GET_FOREIGN_FOODDETAIL /*url*/ , params);
        return new ForeignFoodDetailRequest(getUrl, listener);
    }

}
