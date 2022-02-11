package com.brotherhood.o2o.surprise.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by by.huang on 2015/7/29.
 */
public class CouponRewardInfo {
    public int mId;
    public long mCreateTime;
    public long mExpireTime;
    public int mItemid;
    public int mMode;
    public int mStatus;
    public String mIconUrl;
    public String mTitle;
    public String mUseLabel;
    public String mExchangeLabel;
    public String mCode;
    public boolean mExpand=false;

    public static List<CouponRewardInfo> getDatas(String jsonStr) {
        List<CouponRewardInfo> couponRewardInfos = new ArrayList<>();
        CouponRewardInfo couponRewardInfo = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray dataArraty = jsonObject.optJSONArray("data");
            if (dataArraty != null && dataArraty.length() > 0) {
                int size = dataArraty.length();
                for (int i = 0; i < size; i++) {
                    couponRewardInfo = new CouponRewardInfo();
                    JSONObject infoObj = dataArraty.getJSONObject(i);
                    couponRewardInfo.mCreateTime = infoObj.optLong("create_time");
                    couponRewardInfo.mExpireTime = infoObj.optLong("expire_time");
                    couponRewardInfo.mId = infoObj.optInt("id");
                    couponRewardInfo.mItemid = infoObj.optInt("item_id");
                    couponRewardInfo.mMode = infoObj.optInt("mode");
                    couponRewardInfo.mStatus = infoObj.optInt("status");
                    couponRewardInfo.mTitle = infoObj.optString("title");
                    couponRewardInfo.mIconUrl = infoObj.optString("list_icon");
                    couponRewardInfo.mExchangeLabel = infoObj.optString("exchange_label");
                    couponRewardInfo.mUseLabel = infoObj.optString("use_label");
                    couponRewardInfo.mCode=infoObj.optString("code");
                    couponRewardInfos.add(couponRewardInfo);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return couponRewardInfos;
    }

}
