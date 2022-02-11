package com.brotherhood.o2o.category.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhengYi on 15/6/2.
 * 服务分类的模型类
 */
public class SellerCategoryInfo {
    private static String[] NAME_ARRAY = {"外卖", "水果", "分类", "零食"};
    private static String[] ICON_URL_ARRAY = {"https://www.baidu.com/img/bd_logo1.png"};

    /**
     * 服务分类的id
     */
    public String mCategoryId;

    /**
     * 服务分类的名称
     */
    public String mName;

    /**
     * 分类图标的下载地址
     */
    public String mIconURL;

    public static List<SellerCategoryInfo> create(int count) {
        ArrayList<SellerCategoryInfo> dataSet = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            dataSet.add(new SellerCategoryInfo(i));
        }
        return dataSet;
    }

    public SellerCategoryInfo(int seed) {
        mCategoryId = "" + seed;
        mName = NAME_ARRAY[seed % NAME_ARRAY.length];
        mIconURL = ICON_URL_ARRAY[seed % ICON_URL_ARRAY.length];
    }
}
