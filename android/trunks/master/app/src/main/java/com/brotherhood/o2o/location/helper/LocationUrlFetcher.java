package com.brotherhood.o2o.location.helper;

import com.brotherhood.o2o.extensions.http.HttpClient;
import com.brotherhood.o2o.location.model.MyLocationInfo;
import com.brotherhood.o2o.utils.BaseRequestParams;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.config.Constants;

/**
 * Created by by.huang on 2015/7/28.
 */
public class LocationUrlFetcher {

    /**
     * 获取收货地址列表
     *
     * @param listener
     */
    public static void requestAddress(HttpClient.OnHttpListener listener) {
        BaseRequestParams params = new BaseRequestParams();
        HttpClient.getInstance().get_v2(Constants.URL_GET_ADDRESS_LIST, params, listener);
    }

    /**
     * 添加一个收货地址
     *
     * @param info
     * @param listener
     */
    public static void requestAddLocation(MyLocationInfo info, HttpClient.OnHttpListener listener) {
        BaseRequestParams params = new BaseRequestParams();
        params.put("receiver", info.mName);
        params.put("mobile", info.mPhone);
        params.put("address", info.mAddress);
        HttpClient.getInstance().post_v2(Constants.URL_POST_ADD_ADDRESS, params, listener);
    }

    /**
     * 删除地址
     *
     * @param id
     * @param listener
     */
    public static void requestDeleteLocation(int id, HttpClient.OnHttpListener listener) {
        BaseRequestParams params = new BaseRequestParams();
        params.put("id", id);
        HttpClient.getInstance().post_v2(Constants.URL_POST_DELETE_ADDRESS, params, listener);
    }

    /**
     * 确定选择地址
     *
     * @param id
     * @param addressId
     */
    public static void requestConfirmLocation(int id, int addressId, HttpClient.OnHttpListener listener) {
        BaseRequestParams params = new BaseRequestParams();
        params.put("id", id);
        params.put("address_id", addressId);
        params.put("mode", 2);
        HttpClient.getInstance().post_v2(Constants.URL_POST_CONFIRM_ADDRESS, params, listener);
    }

    /**
     * 上报地理位置
     * @param longitude
     * @param latitude
     */
    public static void UploadLocation(double longitude,double latitude)
    {
        BaseRequestParams params=new BaseRequestParams();
        params.put("longitude",longitude);
        params.put("latitude",latitude);
        HttpClient.getInstance().post_v2(Constants.URL_POST_LOCATION, params, new HttpClient.OnHttpListener() {
            @Override
            public void OnStart() {

            }

            @Override
            public void OnSuccess(HttpClient.RequestStatu statu, Object respondObject) {

                ByLogout.out("上报地理位置成功");
            }

            @Override
            public void OnFail(HttpClient.RequestStatu statu, String resons) {

            }
        });
    }
}
