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
public class OverseaFoodDetailRequest extends BaseAppRequest<ResponseResult<FoodDetail>> {


    public OverseaFoodDetailRequest(String url, OnBaseResponseListener<ResponseResult<FoodDetail>> baseResponseListener) {
        super(url, Method.GET, true, baseResponseListener);
    }

    /**
     * @param businessId 商家ID
     * @param listener
     * @return
     */
    public static OverseaFoodDetailRequest createFoodDetailRequest(String businessId, OnResponseListener<FoodDetail> listener){
        Map<String,String> params = new HashMap<>();
        params.put("business_id", businessId);
        String getUrl = RequestParameterUtil.buildParamsUrl(Constants.URL_GET_FOREIGN_FOODDETAIL, params);
        return new OverseaFoodDetailRequest(getUrl, listener);
    }

}
