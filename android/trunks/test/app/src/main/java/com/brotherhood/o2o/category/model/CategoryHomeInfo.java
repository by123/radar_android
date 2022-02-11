package com.brotherhood.o2o.category.model;

import java.util.List;

/**
 * Created by ZhengYi on 15/6/2.
 */
public class CategoryHomeInfo {
    public List<SellerInfo> mTopBannerInfoList;
    public List<SellerCategoryInfo> mCategoryInfoList;
    public List<SellerInfo> mSellerInfoList;
    public List<SellerInfo> mBottomBannerInfoList;

    public CategoryHomeInfo() {
        mTopBannerInfoList = SellerInfo.create(5);
        mCategoryInfoList = SellerCategoryInfo.create(8);
        mSellerInfoList = SellerInfo.create(7);
        mBottomBannerInfoList = SellerInfo.create(4);
    }
}
