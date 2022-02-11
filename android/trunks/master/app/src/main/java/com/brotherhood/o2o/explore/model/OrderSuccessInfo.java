package com.brotherhood.o2o.explore.model;

import android.app.Activity;
import android.content.pm.ActivityInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by by.huang on 2015/7/29.
 */
public class OrderSuccessInfo {


    public int mId;
    public int mItemId;
    public long mCreateTime;
    public long mExpireTime;
    /**
     * 订单状态 0锁定 1未使用 2已使用 3已过期
     */
    public int mStatu;
    public String mDisCountPrice;

    public String mExchangeCode;
    public String mIconUrl;
    public String mListIconUrl;
    public int mMode;
    public String mPrice;
    public String mSupplier;
    public String mSummary;
    public String mTitle;
    public List<ActivityBean> mActivityBeans;

    public String mUserName;
    public String mUserPhone;
    public String mUserAddress;
    public int mBufferTime;


    public static OrderSuccessInfo getData(String jsonStr) {

        OrderSuccessInfo info = new OrderSuccessInfo();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr).optJSONObject("data");
            info.mCreateTime=jsonObject.optLong("create_time");
            info.mExpireTime=jsonObject.optLong("expire_time");
            info.mId=jsonObject.optInt("id");
            info.mItemId=jsonObject.optInt("item_id");
            info.mStatu=jsonObject.optInt("status");
            JSONObject orderObj=jsonObject.optJSONObject("order_info");
            info.mUserAddress=orderObj.optString("address");
            info.mUserName=orderObj.optString("receiver");
            info.mBufferTime=orderObj.optInt("buffer_time");
            info.mDisCountPrice=orderObj.optString("discount_price");
            info.mIconUrl=orderObj.optString("icon");
            info.mListIconUrl=orderObj.optString("list_icon");
            info.mUserPhone=orderObj.optString("mobile");
            info.mMode=orderObj.optInt("mode");
            info.mPrice=orderObj.optString("price");
            info.mTitle=orderObj.optString("title");
            info.mExchangeCode=orderObj.optString("exchange_code");
            info.mSummary=orderObj.optString("summary");
            info.mSupplier=orderObj.optString("supplier");
            JSONArray activityArray = orderObj.optJSONArray("activity_info");
            List<ActivityBean> activityBeans = new ArrayList<>();
            ActivityBean activityBean = null;
            if (activityArray != null && activityArray.length() > 0) {
                for (int j = 0; j < activityArray.length(); j++) {
                    activityBean = new ActivityBean();
                    activityBean.mTitle = activityArray.getJSONObject(j).optString("title");
                    activityBean.mContent = activityArray.getJSONObject(j).optString("content");
                    activityBeans.add(activityBean);
                }
            }
            info.mActivityBeans = activityBeans;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info;
    }


}
