package com.brotherhood.o2o.location.helper;

import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.brotherhood.o2o.extensions.BDLocationServiceWrapper;
import com.brotherhood.o2o.extensions.http.HttpClient;
import com.brotherhood.o2o.location.model.LocationInfo;
import com.brotherhood.o2o.config.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhengYi on 15/6/8.
 */
public class LocationHelper {
    private static LocationHelper sInstance;

    private LocationHelper() {
    }

    public static LocationHelper shareManager() {
        if (sInstance == null)
            sInstance = new LocationHelper();
        return sInstance;
    }

    public void searchBuilding(final String keyword, final Callback handler) {
        final PoiSearch search = PoiSearch.newInstance();
        search.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult.getTotalPageNum() >= 0) {
                    postCallback(handler, newLocationInfoListFromSearch(poiResult.getAllPoi()), null);
                } else {
                    postCallback(handler, null, "无法获取地理位置");
                }
                search.destroy();
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            }
        });
        PoiNearbySearchOption option = new PoiNearbySearchOption();
        option.keyword(keyword);
        option.location(new LatLng(22.549385, 113.942969));
        option.pageCapacity(100);
        search.searchNearby(option);
    }

    public void searchNearByBuilding(final Callback handler) {
        BDLocationServiceWrapper.requestLocation(new BDLocationServiceWrapper.RequestLocationCallback() {
            @Override
            public void onFinishRequestLocation(BDLocation locationOrNil, String errorOrNil) {
                if (locationOrNil == null) {
                    assert errorOrNil != null;
                    postCallback(handler, null, "失败");
                } else {
                    final BDLocation location = locationOrNil;
                    String requestURL = createRequestURL(location.getLatitude(), location.getLongitude());
                    HttpClient.getInstance().get(requestURL, new HttpClient.OnHttpListener() {
                        @Override
                        public void OnStart() {
                        }

                        @Override
                        public void OnSuccess(HttpClient.RequestStatu status, Object responseObj) {
                            String response = responseObj.toString().trim();
                            if (!TextUtils.isEmpty(response) && response.startsWith("renderReverse&&renderReverse(")) {
                                response = response.substring("renderReverse&&renderReverse(".length(), response.length() - 1);
                                List<LocationInfo> dataSet = newLocationInfoListFromNearByResponse(response);
                                postCallback(handler, dataSet, null);
                            } else {
                                postCallback(handler, null, "服务器异常");
                            }
                        }

                        @Override
                        public void OnFail(HttpClient.RequestStatu statu, String resons) {
                            postCallback(handler, null, "网络异常");
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
        });

//        Context context = ContextUtils.context();
//        TencentLocationRequest request = TencentLocationRequest.create();
//        request.setAllowCache(false);
//        request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_POI);
//        final TencentLocationManager lm = TencentLocationManager.getInstance(context);
//        lm.requestLocationUpdates(request, new TencentLocationListener() {
//            @Override
//            public void onLocationChanged(TencentLocation location, int error, String reason) {
//                if (error == TencentLocation.ERROR_OK) {
//                    List<LocationInfo> dataSet = newLocationInfoListFromNearBy(location.getPoiList());
//                    postCallback(handler, dataSet, null);
//                } else {
//                    postCallback(handler, null, reason);
//                }
//                lm.removeUpdates(this);
//            }
//
//            @Override
//            public void onStatusUpdate(String s, int i, String s1) {
//            }
//        });
    }

    private void postCallback(Callback callback, List<LocationInfo> dataSetOrNil, String errorOrNil) {
        callback.onFinish(dataSetOrNil, errorOrNil);
    }

    private List<LocationInfo> newLocationInfoListFromNearByResponse(String response) {
        try {
            JSONArray jsonArray = new JSONObject(response).getJSONObject("result").getJSONArray("pois");
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

    private List<LocationInfo> newLocationInfoListFromSearch(List<PoiInfo> poiList) {
        if (poiList == null || poiList.isEmpty()) {
            return new ArrayList<>(0);
        }

        ArrayList<LocationInfo> ret = new ArrayList<>(poiList.size());

        for (PoiInfo poi : poiList) {
            ret.add(newLocationInfo(poi));
        }

        return ret;
    }
//
//    private List<LocationInfo> newLocationInfoListFromNearBy(List<TencentPoi> poiList) {
//        if (poiList == null)
//            return new ArrayList<>(0);
//
//        ArrayList<LocationInfo> ret = new ArrayList<>(poiList.size());
//        for (TencentPoi poi : poiList)
//            ret.add(newLocationInfo(poi));
//        return ret;
//    }
//
//    private List<LocationInfo> newLocationInfoListFromSearch(List<SearchResultObject.SearchResultData> poiList) {
//        if (poiList == null)
//            return new ArrayList<>(0);
//
//        ArrayList<LocationInfo> ret = new ArrayList<>(poiList.size());
//        for (SearchResultObject.SearchResultData poi : poiList)
//            ret.add(newLocationInfo(poi));
//        return ret;
//    }

    private LocationInfo newLocationInfo(PoiInfo poi) {
        LocationInfo ret = new LocationInfo();
        ret.mLongitude = poi.location.longitude;
        ret.mLatitude = poi.location.latitude;
        ret.mAddress = poi.address;
        ret.mBuildingName = poi.name;
        return ret;
    }

    private LocationInfo newLocationInfo(JSONObject poiObj) throws JSONException {
        LocationInfo ret = new LocationInfo();
        ret.mLatitude = poiObj.getJSONObject("point").getDouble("x");
        ret.mLongitude = poiObj.getJSONObject("point").getDouble("y");
        ret.mAddress = poiObj.getString("addr");
        ret.mBuildingName = poiObj.getString("name");
        return ret;
    }

//    private LocationInfo newLocationInfo(SearchResultObject.SearchResultData poi) {
//        LocationInfo ret = new LocationInfo();
//        ret.mLongitude = poi.location.lng;
//        ret.mLatitude = poi.location.lat;
//        ret.mAddress = poi.address;
//        ret.mBuildingName = poi.title;
//        return ret;
//    }
//
//    private LocationInfo newLocationInfo(TencentPoi poi) {
//        LocationInfo ret = new LocationInfo();
//        ret.mLongitude = poi.getLongitude();
//        ret.mLatitude = poi.getLatitude();
//        ret.mAddress = poi.getAddress();
//        ret.mBuildingName = poi.getName();
//        return ret;
//    }

    public interface Callback {
        void onFinish(List<LocationInfo> dataSetOrNil, String errorOrNil);
    }

}
