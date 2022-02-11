package com.brotherhood.o2o.location.model;

import com.brotherhood.o2o.explore.model.ActivityBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by by.huang on 2015/7/28.
 */
public class OrderInfo implements Serializable{
    public int mId;
    public int mItemId;
    public long mUid;
    /**
     * 倒计时
     */
    public long mBufferTime;
    /**
     * 创建时间
     */
    public long mCreateTime;
    /**
     * 到期时间
     */
    public long mExpireTime;
    /**
     * 类型 优惠券
     */
    public int mCategory;
    /**
     * 订单状态 0 锁定 1 未配送 2完成
     */
    public int mStatu;
    /**
     * 产品名称
     */
    public String mProductName;
    /**
     * 产品图片
     */
    public String mProductImg;
    /**
     * 产品详细信息
     */
    public ArrayList<ActivityBean> mActivityBean;

    public static OrderInfo getOrderInfo(String jsonStr) {
        OrderInfo orderInfo = new OrderInfo();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONObject dataObj = jsonObject.getJSONObject("data");
            orderInfo.mId = dataObj.optInt("id");
            orderInfo.mUid = dataObj.optLong("uid");
            orderInfo.mItemId = dataObj.optInt("item_id");
            orderInfo.mCategory = dataObj.optInt("category");
            orderInfo.mStatu = dataObj.optInt("status");
            orderInfo.mCreateTime = dataObj.optLong("create_time");
            orderInfo.mBufferTime = dataObj.optInt("buffer_time");
            orderInfo.mExpireTime = dataObj.optLong("expire_time");

            JSONObject infoObj = dataObj.optJSONObject("order_info");
            orderInfo.mProductName = infoObj.optString("title");
            orderInfo.mProductImg = infoObj.optString("icon");
            JSONArray activityArray = infoObj.optJSONArray("activity_info");
            ArrayList<ActivityBean> activityBeans = new ArrayList<>();
            ActivityBean activityBean = null;
            if (activityArray != null && activityArray.length() > 0) {
                for (int j = 0; j < activityArray.length(); j++) {
                    activityBean = new ActivityBean();
                    activityBean.mTitle = activityArray.getJSONObject(j).getString("title");
                    activityBean.mContent = activityArray.getJSONObject(j).getString("content");
                    activityBeans.add(activityBean);
                }
            }
            orderInfo.mActivityBean = activityBeans;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return orderInfo;
    }
}
