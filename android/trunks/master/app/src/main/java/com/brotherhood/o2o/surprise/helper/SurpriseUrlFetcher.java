package com.brotherhood.o2o.surprise.helper;

import com.brotherhood.o2o.extensions.http.HttpClient;
import com.brotherhood.o2o.utils.BaseRequestParams;
import com.brotherhood.o2o.config.Constants;

/**
 * Created by by.huang on 2015/7/29.
 */
public class SurpriseUrlFetcher {

    /**
     * @param category 列表类型，1、惊喜，2、优惠券
     * @param listener
     */
    public static void requestSurpriseList(int category, HttpClient.OnHttpListener listener) {
        BaseRequestParams params = new BaseRequestParams();
        params.put("category", category);
        HttpClient.getInstance().get_v2(Constants.URL_POST_ORDER_LIST, params, listener);
    }


    /**
     * @param orderid  订单ID
     * @param listener
     */
    public static void requestSurpriseDetail(int orderid, HttpClient.OnHttpListener listener) {
        BaseRequestParams params = new BaseRequestParams();
        params.put("id", orderid);
        HttpClient.getInstance().get_v2(Constants.URL_POST_ORDER_DETAIL, params, listener);
    }
}

