package com.brotherhood.o2o.explore.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by by.huang on 2015/7/24.
 */
public class ProductBean {

    /**
     * 商品id
     */
    public int mId;
    /**
     * 商品类型 1、"到店消码",2、"实物配送",3、"线上发放码"
     */
    public int mMode;
    /**
     * 商品介绍图
     */
    public String mIcon;
    /**
     * 商品名称
     */
    public String mTitle;
    /**
     * 商品文字介绍
     */
    public String mSummary;
    /**
     * 原价
     */
    public String mPrice;
    /**
     * 折扣价
     */
    public String mDiscountPrice;
    /**
     * 总共份数
     */
    public int mQuota;
    /**
     * 剩余份数
     */
    public int mRemainQuota;
    /**
     * 提供者
     */
    public String mSupplier;
    /**
     * 地图url
     */
    public String mMapUrl;
    /**
     * 有效时间
     */
    public long mExpireTime;

    /**
     * 活动信息介绍
     */
    public List<ActivityBean> mActivityBeans;
    /**
     * 图文信息介绍
     */
    public List<ContentBean> mContentBeans;

    /**
     * 点赞数
     * @return
     */
    public int mPraiseCount;

    @Override
    public String toString() {
        return super.toString();
    }

    public static ProductBean getDatas(String jsonStr) {
        ProductBean data = new ProductBean();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONObject dataObj = jsonObject.getJSONObject("data");
            data.mId = dataObj.getInt("id");
            data.mMode = dataObj.getInt("mode");
            data.mIcon = dataObj.getString("icon");
            data.mTitle = dataObj.getString("title");
            data.mSummary = dataObj.getString("summary");
            data.mPrice = dataObj.getString("price");
            data.mDiscountPrice = dataObj.getString("discount_price");
            data.mQuota = dataObj.getInt("quota");
            data.mRemainQuota = dataObj.getInt("remain_quota");
            data.mSupplier = dataObj.getString("supplier");
            data.mMapUrl = dataObj.getString("map_url");
            data.mExpireTime = dataObj.getLong("expire_time");
            data.mPraiseCount=dataObj.getInt("praise_count");

            JSONArray activityArray = dataObj.getJSONArray("activity_info");
            List<ActivityBean> activityBeans = new ArrayList<ActivityBean>();
            ActivityBean activityBean = null;
            if (activityArray != null && activityArray.length() > 0) {

                for (int i = 0; i < activityArray.length(); i++) {
                    activityBean = new ActivityBean();
                    JSONObject activityObj = activityArray.getJSONObject(i);
                    activityBean.mTitle = activityObj.getString("title");
                    activityBean.mContent = activityObj.getString("content");
                    activityBeans.add(activityBean);
                }
            }
            data.mActivityBeans = activityBeans;


            JSONArray contentArray = dataObj.getJSONArray("content");
            ArrayList<ContentBean> contentBeans = new ArrayList<>();
            ContentBean contentBean = null;
            if (contentArray != null && contentArray.length() > 0) {

                for (int i = 0; i < contentArray.length(); i++) {
                    contentBean = new ContentBean();
                    JSONObject contentObj = contentArray.getJSONObject(i);
                    contentBean.mType = contentObj.getString("type");
                    contentBean.mContent = contentObj.getString("content");
                    contentBeans.add(contentBean);
                }
            }
            data.mContentBeans = contentBeans;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

}

