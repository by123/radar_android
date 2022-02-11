package com.brotherhood.o2o.util;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.bean.location.LocationInfo;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.RequestLocationCallback;
import com.brotherhood.o2o.manager.LogManager;
import com.google.android.gms.maps.model.LatLng;

/**
 * 海外版定位
 * Created by jl.zhang on 2016/1/4.
 */
public class OverseaLocationUtil {

    private static OverseaLocationUtil mInstance;
    private LocationManager mLocationManager;
    private static final int MIN_DISTANCE = 50;
    private String mProvider;
    private static RequestLocationCallback mLastCallback;

    private OverseaLocationUtil(){
        mLocationManager = (LocationManager) NearApplication.mInstance.getSystemService(Context.LOCATION_SERVICE);
    }

    public synchronized static OverseaLocationUtil getInstance(){
        if (mInstance == null){
            mInstance = new OverseaLocationUtil();
        }
        return mInstance;
    }

    public void init(){
        Criteria criteria = new Criteria();//定义Criteria对象
        criteria.setAccuracy(Criteria.ACCURACY_FINE);// 定位的精准度
        criteria.setAltitudeRequired(false);// 海拔信息是否关注
        criteria.setBearingRequired(false);// 对周围的事情是否进行关心
        criteria.setCostAllowed(false);// 是否支持收费的查询
        criteria.setPowerRequirement(Criteria.POWER_LOW);// 是否耗电
        criteria.setSpeedRequired(false);// 对速度是否关注

        mProvider = mLocationManager.getBestProvider(criteria, false);//得到最好的定位方式
        LocationProvider provider = mLocationManager.getProvider(LocationManager.NETWORK_PROVIDER);//网络定位
        if (provider != null) {
            mProvider = provider.getName();
        }
    }

    public void requestLocation(final RequestLocationCallback callback){
        if (mLocationManager == null || mProvider == null){
            LogManager.d("=================something about location is null===================="+getClass().getName());
            return;
        }
        mLastCallback = new RequestLocationCallback() {
            @Override
            public void onFinishRequestLocation(LocationInfo locationOrNil, String errorOrNil) {
                if (callback != null) {
                    callback.onFinishRequestLocation(locationOrNil, errorOrNil);
                }
                mLastCallback = null;
            }
        };
        //注册监听
        mLocationManager.requestLocationUpdates(mProvider, Constants.LOCATION_UPDATE_INTERVAL, MIN_DISTANCE, new OverseaLocationListener());

    }

    /**
     * 实现监听接口
     */
    private final class OverseaLocationListener implements LocationListener {
        @Override// 位置的改变
        public void onLocationChanged(Location location) {
            //显示当前坐标
            if (location == null) {
                dispatchOnFinishRequestLocationEvent(null, NearApplication.mInstance.getString(R.string.unknow_location_error));
            } else {
                dispatchOnFinishRequestLocationEvent(location, null);
            }
        }

        private void dispatchOnFinishRequestLocationEvent(Location locationOrNil, String errorOrNil) {
            if (mLastCallback != null) {
                LocationInfo info = new LocationInfo();
                info.mLatitude = locationOrNil.getLatitude();
                info.mLongitude = locationOrNil.getLongitude();
                com.brotherhood.o2o.manager.LocationManager.getInstance().setMyLocation(new LatLng(info.mLatitude, info.mLongitude));
                mLastCallback.onFinishRequestLocation(info, errorOrNil);
            }
        }

        @Override// gps卫星有一个没有找到
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override// 某个设置被打开
        public void onProviderEnabled(String provider) {
        }

        @Override// 某个设置被关闭
        public void onProviderDisabled(String provider) {
        }

    }
}
