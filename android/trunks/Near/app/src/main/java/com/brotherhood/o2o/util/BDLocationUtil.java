package com.brotherhood.o2o.util;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.bean.location.LocationInfo;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.RequestLocationCallback;
import com.brotherhood.o2o.manager.LocationManager;
import com.brotherhood.o2o.manager.LogManager;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ZhengYi on 15/7/9.
 */
public class BDLocationUtil {
    /**
     * 61 ： GPS定位结果
     * 62 ： 扫描整合定位依据失败。此时定位结果无效。
     * 63 ： 网络异常，没有成功向服务器发起请求。此时定位结果无效。
     * 65 ： 定位缓存的结果。
     * 66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果
     * 67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果
     * 68 ： 网络连接失败时，查找本地离线定位时对应的返回结果
     * 161： 表示网络定位结果
     * 162~167： 服务端定位失败
     * 502：KEY参数错误
     * 505：KEY不存在或者非法
     * 601：KEY服务被开发者自己禁用
     * 602: KEY Mcode不匹配,意思就是您的ak配置过程中安全码设置有问题，请确保： sha1正确，“;”分号是英文状态；且包名是您当前运行应用的包名
     * 501-700：KEY验证失败
     */
    private static int[] sSuccessRetCode = new int[]{61, 65, 66, 161};

    private static LocationClient mClient;
    private static RequestLocationCallback mLastCallback;
    private static BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                dispatchOnFinishRequestLocationEvent(null, "未知错误");
            } else {
                int code = location.getLocType();

                /**
                 * 判断当前返回结果是否正常
                 */
                boolean isRequestLocationSuccess = false;
                for (int successCode : sSuccessRetCode) {
                    if (successCode == code) {
                        isRequestLocationSuccess = true;
                        break;
                    }
                }
                LogManager.d("=====================百度定位===================");

                if (isRequestLocationSuccess) {
                    dispatchOnFinishRequestLocationEvent(location, null);
                } else {
                    dispatchOnFinishRequestLocationEvent(null, "服务器异常");
                }

            }
        }

        private void dispatchOnFinishRequestLocationEvent(BDLocation locationOrNil, String errorOrNil) {
            if (mLastCallback != null) {
                LocationInfo info = new LocationInfo();
                if (locationOrNil != null){
                    info.mLatitude = locationOrNil.getLatitude();
                    info.mLongitude = locationOrNil.getLongitude();
                    LocationInfo myLocationInfo = CoordinateUtil.bd09_To_Gps84(info.mLatitude, info.mLongitude);
                    LocationManager.getInstance().setMyLocation(new LatLng(myLocationInfo.mLatitude, myLocationInfo.mLongitude));
                }
                mLastCallback.onFinishRequestLocation(info, errorOrNil);
            }
        }
    };

    public static void init() {
        SDKInitializer.initialize(NearApplication.mInstance.getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(Constants.LOCATION_UPDATE_INTERVAL);//定位时间间隔
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        option.setNeedDeviceDirect(false);
        option.setCoorType("bd09ll");
        mClient = new LocationClient(NearApplication.mInstance.getApplicationContext(), option);
        mClient.registerLocationListener(mListener);
    }

    public static void requestLocation(final RequestLocationCallback callback) {
        if(mClient!=null) {
            mClient.start();
            mLastCallback = new RequestLocationCallback() {
                @Override
                public void onFinishRequestLocation(LocationInfo locationOrNil, String errorOrNil) {
                    if (callback != null){
                        callback.onFinishRequestLocation(locationOrNil, errorOrNil);
                    }
                    mLastCallback = null;
                }
            };
            mClient.requestLocation();
        }
    }

    public static void destory() {
        mClient.unRegisterLocationListener(mListener);
        mClient.stop();
    }
}
