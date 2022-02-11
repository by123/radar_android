package com.brotherhood.o2o.address.helper;

import android.content.Context;

import com.brotherhood.o2o.address.model.AddressInfo;
import com.brotherhood.o2o.utils.ContextUtils;
import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.Location;
import com.tencent.lbssearch.object.param.SearchParam;
import com.tencent.lbssearch.object.result.SearchResultObject;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.map.geolocation.TencentPoi;

import org.apache.http.Header;

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
//        final PoiSearch search = PoiSearch.newInstance();
//        search.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
//            @Override
//            public void onGetPoiResult(PoiResult poiResult) {
//                if (poiResult.getTotalPageNum() >= 0) {
//                    postCallback(handler, newAddressInfoList(poiResult.getAllPoi()), null);
//                } else {
//                    postCallback(handler, null, "无法获取地理位置");
//                }
//                search.destroy();
//            }
//
//            @Override
//            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
//            }
//        });
//        PoiNearbySearchOption option = new PoiNearbySearchOption();
//        option.keyword(keyword);
//        option.location(new LatLng(22.549385, 113.942969));
//        option.pageCapacity(100);
//        search.searchNearby(option);

        final Context context = ContextUtils.context();
        TencentLocationRequest request = TencentLocationRequest.create();
        request.setAllowCache(true);
        request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_GEO);
        final TencentLocationManager lm = TencentLocationManager.getInstance(context);
        lm.requestLocationUpdates(request, new TencentLocationListener() {
            @Override
            public void onLocationChanged(TencentLocation location, int error, final String reason) {
                if (error == TencentLocation.ERROR_OK) {
                    TencentSearch search = new TencentSearch(context);
                    SearchParam.Nearby nearby = new SearchParam.Nearby().point(new Location().lat((float) location.getLatitude()).lng((float) location.getLongitude()));
                    nearby.r(1000);
                    SearchParam param = new SearchParam().boundary(nearby).keyword(keyword);
                    search.search(param, new HttpResponseListener() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, BaseObject object) {
                            if (object != null) {
                                SearchResultObject result = (SearchResultObject) object;
                                List<AddressInfo> dataSet = newAddressInfoListFromSearch(result.data);
                                postCallback(handler, dataSet, null);
                            } else {
                                postCallback(handler, null, "服务器异常");
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers,
                                              String responseString, Throwable throwable) {
                            postCallback(handler, null, "服务器异常");
                        }
                    });

                } else {
                    postCallback(handler, null, reason);
                }
                lm.removeUpdates(this);
            }

            @Override
            public void onStatusUpdate(String s, int i, String s1) {
            }
        });

    }

    public void searchNearByBuilding(final Callback handler) {
//        final PoiSearch search = PoiSearch.newInstance();
//        search.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
//            @Override
//            public void onGetPoiResult(PoiResult poiResult) {
//                if (poiResult.getTotalPageNum() >= 0) {
//                    postCallback(handler, newAddressInfoList(poiResult.getAllPoi()), null);
//                } else {
//                    postCallback(handler, null, "无法获取地理位置");
//                }
//                search.destroy();
//            }
//
//            @Override
//            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
//            }
//        });
//        PoiNearbySearchOption option = new PoiNearbySearchOption();
//        option.keyword("附近");
//        option.location(new LatLng(22.549385, 113.942969));
//        option.radius(500);
//        option.pageCapacity(100);
//        search.searchNearby(option);

        Context context = ContextUtils.context();
        TencentLocationRequest request = TencentLocationRequest.create();
        request.setAllowCache(true);
        request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_POI);
        final TencentLocationManager lm = TencentLocationManager.getInstance(context);
        lm.requestLocationUpdates(request, new TencentLocationListener() {
            @Override
            public void onLocationChanged(TencentLocation location, int error, String reason) {
                if (error == TencentLocation.ERROR_OK) {
                    List<AddressInfo> dataSet = newAddressInfoListFromNearBy(location.getPoiList());
                    postCallback(handler, dataSet, null);
                } else {
                    postCallback(handler, null, reason);
                }
                lm.removeUpdates(this);
            }

            @Override
            public void onStatusUpdate(String s, int i, String s1) {
            }
        });
    }

    private void postCallback(Callback callback, List<AddressInfo> dataSetOrNil, String errorOrNil) {
        callback.onFinish(dataSetOrNil, errorOrNil);
    }

    private List<AddressInfo> newAddressInfoListFromNearBy(List<TencentPoi> poiList) {
        if (poiList == null)
            return new ArrayList<>(0);

        ArrayList<AddressInfo> ret = new ArrayList<>(poiList.size());
        for (TencentPoi poi : poiList)
            ret.add(newAddressInfo(poi));
        return ret;
    }

    private List<AddressInfo> newAddressInfoListFromSearch(List<SearchResultObject.SearchResultData> poiList) {
        if (poiList == null)
            return new ArrayList<>(0);

        ArrayList<AddressInfo> ret = new ArrayList<>(poiList.size());
        for (SearchResultObject.SearchResultData poi : poiList)
            ret.add(newAddressInfo(poi));
        return ret;
    }

    private AddressInfo newAddressInfo(SearchResultObject.SearchResultData poi) {
        AddressInfo ret = new AddressInfo();
        ret.mLongitude = poi.location.lng;
        ret.mLatitude = poi.location.lat;
        ret.mAddress = poi.address;
        ret.mBuildingName = poi.title;
        return ret;
    }

    private AddressInfo newAddressInfo(TencentPoi poi) {
        AddressInfo ret = new AddressInfo();
        ret.mLongitude = poi.getLongitude();
        ret.mLatitude = poi.getLatitude();
        ret.mAddress = poi.getAddress();
        ret.mBuildingName = poi.getName();
        return ret;
    }

    public interface Callback {
        void onFinish(List<AddressInfo> dataSetOrNil, String errorOrNil);
    }
}
