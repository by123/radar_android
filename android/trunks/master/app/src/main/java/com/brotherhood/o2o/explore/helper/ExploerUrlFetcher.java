package com.brotherhood.o2o.explore.helper;

import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.extensions.BaseURLFetcher;
import com.brotherhood.o2o.extensions.http.HttpClient;
import com.brotherhood.o2o.location.LocationComponent;
import com.brotherhood.o2o.utils.BaseRequestParams;

/**
 * Created by by.huang on 2015/6/2.
 */
public class ExploerUrlFetcher extends BaseURLFetcher {


    /**
     * longitude: 经度
     * latitude: 纬度
     * distance: 覆盖范围, 默认5KM
     * offset: 偏移值，默认O
     * size： 显示记录条数，默认50
     */
    public static void requestRadarDatas(HttpClient.OnHttpListener listener) {
        if (LocationComponent.shareComponent().getCachedCurrentAddressOrNil() == null) {
            return;
        }
        BaseRequestParams params = new BaseRequestParams();
        params.put("longitude", LocationComponent.shareComponent().getCachedCurrentAddressOrNil().mLatitude);
        params.put("latitude", LocationComponent.shareComponent().getCachedCurrentAddressOrNil().mLongitude);
        params.put("distance", Constants.dLargestDistance);
        HttpClient.getInstance().get_v2(Constants.URL_GET_RADAR, params, listener);
    }


    /**
     * 请求活动数据
     *
     * @param beaconId
     * @param listener
     */
    public static void requsetProductDatas(String beaconId, HttpClient.OnHttpListener listener) {
        BaseRequestParams params = new BaseRequestParams();
        params.put("beacon_id", beaconId);
        HttpClient.getInstance().get_v2(Constants.URL_GET_PRODUCT, params, listener);
    }

    /**
     * 创建订单
     *
     * @param beaconId 活动ID
     * @param listener
     */
    public static void requestCreateOrder(String beaconId, HttpClient.OnHttpListener listener) {
        BaseRequestParams params = new BaseRequestParams();
        params.put("beacon_id", beaconId);
        HttpClient.getInstance().post_v2(Constants.URL_POST_CREATE_ORDER, params, listener);
    }

    /**
     * 商家消码
     *
     * @param id
     * @param code
     */
    public static void requestConsumeCode(int id, String code, HttpClient.OnHttpListener listener) {
        BaseRequestParams params = new BaseRequestParams();
        params.put("id", id);
        params.put("code", code);
        HttpClient.getInstance().post_v2(Constants.URL_POST_CONSUME_CODE, params, listener);
    }
}
