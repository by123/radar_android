package com.brotherhood.o2o.location.model;

import java.io.Serializable;

/**
 * Created by ZhengYi on 15/6/8.
 */
public class LocationInfo  implements Serializable {
    /**
     * 经度
     */
    public double mLongitude;
    /**
     * 纬度
     */
    public double mLatitude;
    /**
     * 建筑名称
     */
    public String mBuildingName;
    /**
     * 地址
     */
    public String mAddress;
    /**
     * 电话
     */
    public String mPhone;
    /**
     * 姓名
     */
    public String mName;

    /**
     * 选中状态
     */
    public boolean mStatu;

    @Override
    public int hashCode() {
        return mBuildingName.hashCode() + mAddress.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LocationInfo && hashCode() == o.hashCode();
    }

    public LocationInfo() {
    }

    public LocationInfo(double mLatitude, double mLongitude, String mBuildingName, String mAddress, String mPhone, String mName, boolean mStatu) {
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mBuildingName = mBuildingName;
        this.mAddress = mAddress;
        this.mPhone = mPhone;
        this.mName = mName;
        this.mStatu = mStatu;
    }

//
//    public static ArrayList<LocationInfo> getCache() {
//        ArrayList<LocationInfo> infos = new ArrayList<LocationInfo>();
//        JSONArray jsonArray = CacheUtils.get(MyApplication.mApplication, Constants.LocationCche).getAsJSONArray(Constants.LocationCche);
//        if (jsonArray == null || jsonArray.length() == 0) {
//            return infos;
//        }
//        for (int i = 0; i < jsonArray.length(); i++) {
//            try {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                infos.add(new LocationInfo(jsonObject.optDouble("mLatitude"), jsonObject.optDouble("mLongitude")
//                        , jsonObject.optString("mBuildingName"), jsonObject.optString("mAddress"), jsonObject.optString("mPhone"), jsonObject.optString("mName"), false));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return infos;
//    }
//
//    public static void saveCache(ArrayList<LocationInfo> infos) {
//        if (infos == null) {
//            return;
//        }
//        JSONArray jsonArray = new JSONArray();
//        for (int i = 0; i < infos.size(); i++) {
//            LocationInfo info = infos.get(i);
//            try {
//                JSONObject object = new JSONObject();
//                object.put("mLatitude", info.mLatitude);
//                object.put("mLongitude", info.mLongitude);
//                object.put("mAddress", info.mAddress);
//                object.put("mBuildingName", info.mBuildingName);
//                object.put("mPhone", info.mPhone);
//                object.put("mName", info.mName);
//                jsonArray.put(i, object);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        CacheUtils.get(MyApplication.mApplication, Constants.LocationCche).put(Constants.LocationCche, jsonArray);
//    }


}
