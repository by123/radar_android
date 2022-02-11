package com.brotherhood.o2o.category.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhengYi on 15/6/2.
 * 商家信息的模型类
 */
public class SellerInfo {
    private static final String[] COVER_URL_ARRAY = {
            "http://cdn.akamai.steamstatic.com/steam/apps/208650/capsule_616x353.jpg?t=1432832565",
            "http://cdn.akamai.steamstatic.com/steam/apps/730/capsule_616x353.jpg?t=1432930508",
            "http://cdn.akamai.steamstatic.com/steam/apps/322330/capsule_616x353.jpg?t=1433367975",
            "http://cdn.akamai.steamstatic.com/steam/apps/306130/capsule_616x353.jpg?t=1433273090",
            "http://cdn.akamai.steamstatic.com/steam/apps/374570/capsule_616x353.jpg?t=1433010312"};

    /**
     * 商家id
     */
    public String mSellerId;
    /**
     * banner封面的下载地址
     */
    public String mCoverURL;

    public static List<SellerInfo> create(int count) {
        ArrayList<SellerInfo> dataSet = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            dataSet.add(new SellerInfo(i));
        }
        return dataSet;
    }

    public SellerInfo(int seed) {
        mSellerId = "" + seed;
        mCoverURL = COVER_URL_ARRAY[seed % COVER_URL_ARRAY.length];
    }
}
