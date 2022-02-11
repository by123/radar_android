package com.brotherhood.o2o.manager;

import android.text.TextUtils;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.brotherhood.o2o.R;
import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.bean.location.LocationInfo;
import com.brotherhood.o2o.bean.nearby.OverseaGeo;
import com.brotherhood.o2o.bean.nearby.OverseaPoi;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnBaiduPoiResponseListener;
import com.brotherhood.o2o.listener.OnGooglePoiResponseListener;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.listener.OnSearchPoiResponseListener;
import com.brotherhood.o2o.listener.RequestLocationCallback;
import com.brotherhood.o2o.message.Message;
import com.brotherhood.o2o.request.NearByBuildingRequest;
import com.brotherhood.o2o.request.OverseaNearbyBuildingRequest;
import com.brotherhood.o2o.request.UploadLocationRequest;
import com.brotherhood.o2o.util.BDLocationUtil;
import com.brotherhood.o2o.util.OverseaLocationUtil;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jl.zhang on 2015/12/4.
 */
public class LocationManager {

    private static LocationManager sInstance;
    private LocationInfo mCurrentAddressOrNil;
    private LatLng mMyLatlng;
    private List<LocationInfo> mLocationList;//用于地点选择
    public static final int BD_LOACTION_TYPE = 1;
    public static final int OVERSEA_LOACTION_TYPE = 2;
    private int mLocationType;

    public void destroy(){
        sInstance = null;
        mCurrentAddressOrNil = null;
    }

    public void clearCache(){
        mCurrentAddressOrNil = null;
    }

    private LocationManager() {
    }

    /**
     * 百度经纬度 我的位置
     */
    public void setMyLocation(LatLng latLng){
        mMyLatlng = latLng;
    }

    /**
     * 返回百度定位  我的位置
     * @return
     */
    public LatLng getMyLatlng(){
        return mMyLatlng;
    }


    public synchronized static LocationManager getInstance() {
        if (sInstance == null)
            sInstance = new LocationManager();
        return sInstance;
    }

    /**
     * 定位类型 0 百度   1 原生
     * @return
     */
    public int getLocationType(){
        return mLocationType;
    }

    public void initLocation(int locationType){
        mLocationType = locationType;
        if (locationType == 1){
            BDLocationUtil.init();
        }else if (locationType == 2){
            OverseaLocationUtil.getInstance().init();
        }
    }


    /**
     * 获取当前位置
     */
    public void updateCurrentAddress() {
        searchNearByBuilding(new OnSearchPoiResponseListener() {
            @Override
            public void onFinish(List<LocationInfo> dataSetOrNil, String errorOrNil) {
                if (dataSetOrNil == null || dataSetOrNil.isEmpty()){
                    NearApplication.mInstance.getMessagePump().broadcastMessage(Message.Type.UPDATE_ADDRESS_FAILED, mCurrentAddressOrNil);
                    return;
                }
                LogManager.d("==================================mLocationType=" + mLocationType);
                mLocationList = dataSetOrNil;
                mCurrentAddressOrNil = dataSetOrNil.get(0);
                NearApplication.mInstance.getMessagePump().broadcastMessage(Message.Type.ADDRESS_CHANGED, mCurrentAddressOrNil);
                UploadLocationRequest request = UploadLocationRequest.createUploadLocationRequest(mCurrentAddressOrNil, new OnResponseListener<String>() {
                    @Override
                    public void onSuccess(int code, String msg, String s, boolean cache) {

                    }

                    @Override
                    public void onFailure(int code, String msg) {

                    }
                });
                request.sendRequest();
            }
        });
    }

    public LocationInfo getCachedCurrentAddressOrNil() {
        return mCurrentAddressOrNil;
    }

    /**
     * 根据当前坐标搜索poi
     * @param listener
     */
    private void searchNearByBuilding(final OnSearchPoiResponseListener listener) {
        if (mLocationType == BD_LOACTION_TYPE){
            BDLocationUtil.requestLocation(new RequestLocationCallback() {
                @Override
                public void onFinishRequestLocation(LocationInfo locationOrNil, String errorOrNil) {
                    if (locationOrNil == null) {
                        assert errorOrNil != null;
                        listener.onFinish(null, NearApplication.mInstance.getString(R.string.request_location_fail));
                    } else {
                        LogManager.d("===========baidu===latitude:"+locationOrNil.mLatitude+"=====longitude:"+locationOrNil.mLongitude);
                        String requestURL = createRequestURL(locationOrNil.mLatitude, locationOrNil.mLongitude);
                        if (TextUtils.isEmpty(requestURL)){
                            LogManager.e("=========bdlocation requesturl is empty=========");
                            return;
                        }
                        NearByBuildingRequest request = NearByBuildingRequest.createNearByBuildingRequest(requestURL, new OnBaiduPoiResponseListener<String>() {
                            @Override
                            public void onSuccess(int code, String msg, String s, boolean cache) {
                                if (TextUtils.isEmpty(s)){
                                    listener.onFinish(null, NearApplication.mInstance.getResources().getString(R.string.server_error));
                                    return;
                                }
                                String response = s.toString().trim();
                                if (TextUtils.isEmpty(response)) {
                                    listener.onFinish(null, NearApplication.mInstance.getResources().getString(R.string.server_error));
                                    return;
                                }
                                List<LocationInfo> dataSet = newLocationInfoListFromNearByResponse(response);
                                listener.onFinish(dataSet, null);
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                listener.onFinish(null, NearApplication.mInstance.getResources().getString(R.string.connect_network_fail));
                            }
                        });
                        request.sendRequest();
                    }
                }
            });
        }else if (mLocationType == OVERSEA_LOACTION_TYPE){
            OverseaLocationUtil.getInstance().requestLocation(new RequestLocationCallback() {
                @Override
                public void onFinishRequestLocation(LocationInfo locationInfo, String errorOrNil) {
                    if (locationInfo == null) {
                        assert errorOrNil != null;
                        listener.onFinish(null, NearApplication.mInstance.getString(R.string.request_location_fail));
                    } else {
                        String latlng = locationInfo.mLatitude + "," + locationInfo.mLongitude;
                        LogManager.d("===========google===latitude:"+locationInfo.mLatitude+"=====longitude:"+locationInfo.mLongitude);
                        OverseaNearbyBuildingRequest request = OverseaNearbyBuildingRequest.createOverseaNearbyRequest(latlng, 500,new
                                OnGooglePoiResponseListener<List<OverseaPoi>>() {
                            @Override
                            public void onSuccess(int code, String msg, List<OverseaPoi> overseaPois, boolean cache) {
                                if (overseaPois == null || overseaPois.isEmpty()){
                                    listener.onFinish(null, NearApplication.mInstance.getResources().getString(R.string.server_error));
                                    return;
                                }
                                List<LocationInfo> locationList = new ArrayList<>();
                                for (int i = 0; i < overseaPois.size(); i++) {
                                    OverseaPoi poi = overseaPois.get(i);
                                    OverseaGeo geoInfo = poi.geoInfo;
                                    if (geoInfo == null || geoInfo.location == null){
                                        continue;
                                    }
                                    LocationInfo info = new LocationInfo();
                                    info.mLatitude = geoInfo.location.mLatitude;
                                    info.mLongitude = geoInfo.location.mLongitude;
                                    info.mAddress = poi.name;
                                    locationList.add(info);
                                }
                                listener.onFinish(locationList, null);
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                listener.onFinish(null, NearApplication.mInstance.getResources().getString(R.string.connect_network_fail));
                            }
                        });
                        request.sendRequest();
                    }
                }
            });
        }
    }
    /**
     * 构建获取附近POI的请求URL
     * @param latitude  纬度
     * @param longitude 经度
     * @return 返回请求的URL
     */
    private String createRequestURL(double latitude, double longitude) {
        if (mLocationType == BD_LOACTION_TYPE) {
            /**
             * sample: http://api.map.baidu.com/geocoder/v2/?
             * ak=E4805d16520de693a3fe707cdc962045&
             * callback=renderReverse&
             * location=39.983424,116.322987&
             * output=json&
             * pois=1
             */
            String apiKey = Constants.BAIDU_LBS_APP_KEY;
            String location = latitude + "," + longitude;
            String securityCode = Constants.BAIDU_LBS_SECURITY_KEY;
            return "http://api.map.baidu.com/geocoder/v2/?ak=" + apiKey +
                    "&callback=renderReverse&location=" + location +
                    "&output=json&pois=1&mcode=" + securityCode;
        }
        return null;
    }

    /**
     * 根据关键词搜索poi
     * @param keyword
     * @param listener
     */
    public static void searchBuilding(final String keyword, final OnSearchPoiResponseListener listener) {
        final PoiSearch search = PoiSearch.newInstance();
        search.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult.getTotalPageNum() >= 0) {
                    listener.onFinish(newLocationInfoListFromSearch(poiResult.getAllPoi()), null);
                } else {
                    listener.onFinish(null, NearApplication.mInstance.getString(R.string.require_location_failed));
                }
                search.destroy();
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            }
        });
    }


    private static List<LocationInfo> newLocationInfoListFromNearByResponse(String response) {
        try {
            JSONArray jsonArray = new JSONObject(response)./*getJSONObject("result").*/getJSONArray("pois");
            final int count = jsonArray.length();
            ArrayList<LocationInfo> ret = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                ret.add(newLocationInfo(jsonArray.getJSONObject(i)));
            }
            return ret;
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>(0);
        }

    }

    private static LocationInfo newLocationInfo(JSONObject poiObj) throws JSONException {
        LocationInfo ret = new LocationInfo();
        ret.mLongitude = poiObj.getJSONObject("point").getDouble("x");//经度
        ret.mLatitude = poiObj.getJSONObject("point").getDouble("y");//纬度
        ret.mAddress = poiObj.getString("addr");
        ret.mBuildingName = poiObj.getString("name");
        return ret;
    }


    private static List<LocationInfo> newLocationInfoListFromSearch(List<PoiInfo> poiList) {
        if (poiList == null || poiList.isEmpty()) {
            return new ArrayList<>(0);
        }
        ArrayList<LocationInfo> ret = new ArrayList<>(poiList.size());

        for (PoiInfo poi : poiList) {
            ret.add(newLocationInfo(poi));
        }
        return ret;
    }



    private static LocationInfo newLocationInfo(PoiInfo poi) {
        LocationInfo ret = new LocationInfo();
        ret.mLongitude = poi.location.longitude;
        ret.mLatitude = poi.location.latitude;
        ret.mAddress = poi.address;
        ret.mBuildingName = poi.name;
        return ret;
    }
}
