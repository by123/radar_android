package com.brotherhood.o2o.address.model;

/**
 * Created by ZhengYi on 15/6/8.
 */
public class AddressInfo {
    /**
     * 纬度
     */
    public double mLatitude;
    /**
     * 经度
     */
    public double mLongitude;
    /**
     * 建筑名称
     */
    public String mBuildingName;
    /**
     * 地址
     */
    public String mAddress;

    @Override
    public int hashCode() {
        return mBuildingName.hashCode() + mAddress.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof AddressInfo && hashCode() == o.hashCode();
    }
}
