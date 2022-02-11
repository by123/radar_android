package com.brotherhood.o2o.surprise.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhengYi on 15/6/30.
 * 实物奖励的数据容器
 */
public class ItemRewardInfo {
    public int mId;
    public long mCreateTime;
    public long mExpireTime;
    public int mItemid;
    public int mMode;
    /**
     *  订单状态 0锁定 1未使用 2已使用 3已过期
     */
    public int mStatus;
    public String mIconUrl;
    public String mTitle;
    public String mWhitePath;

    public static List<ItemRewardInfo> getDatas(String jsonStr) {
        List<ItemRewardInfo> itemRewardInfos = new ArrayList<>();
        ItemRewardInfo itemRewardInfo = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray dataArraty = jsonObject.optJSONArray("data");
            if (dataArraty != null && dataArraty.length() > 0) {
                int size = dataArraty.length();
                for (int i = 0; i < size; i++) {
                    itemRewardInfo = new ItemRewardInfo();
                    JSONObject infoObj = dataArraty.getJSONObject(i);
                    itemRewardInfo.mCreateTime = infoObj.optLong("create_time");
                    itemRewardInfo.mExpireTime = infoObj.optLong("expire_time");
                    itemRewardInfo.mId = infoObj.optInt("id");
                    itemRewardInfo.mItemid = infoObj.optInt("item_id");
                    itemRewardInfo.mMode = infoObj.optInt("mode");
                    itemRewardInfo.mStatus = infoObj.optInt("status");
                    itemRewardInfo.mTitle = infoObj.optString("title");
                    itemRewardInfo.mIconUrl = infoObj.optString("list_icon");
                    itemRewardInfos.add(itemRewardInfo);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return itemRewardInfos;
    }

}
