package com.brotherhood.o2o.personal.model;

import android.text.TextUtils;

import com.brotherhood.o2o.application.MyApplication;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.utils.CacheUtils;
import com.brotherhood.o2o.config.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by by.huang on 2015/6/10.
 */
public class SystemMsgBean {
    public String mTitle;
    public String mContent;
    public long mCreateTime;
    public boolean mRead = false;


    public SystemMsgBean(String mTitle, String mContent, long mCreateTime, boolean mRead) {
        this.mTitle = mTitle;
        this.mContent = mContent;
        this.mCreateTime = mCreateTime;
        this.mRead = mRead;
    }

    /**
     * 收到一条系统消息
     *
     * @param jsonStr
     */
    public static void addSystemMsg(String jsonStr) {
        ByLogout.out("qqqqqqqqqqqqqqqqqqqqqqqqq->"+jsonStr);
        if (TextUtils.isEmpty(jsonStr)) {
            return;
        }
        ArrayList<SystemMsgBean> msgBeans = getCache();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            String mTitle = jsonObject.optString("title");
            String mContent = jsonObject.optString("content");
            long mCreateTime = jsonObject.optLong("create_time");
            SystemMsgBean msgBean = new SystemMsgBean(mTitle, mContent, mCreateTime, false);
            if (msgBean == null) {
                return;
            }
            msgBeans.add(msgBean);
            saveCache(msgBeans);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取本地缓存系统消息
     *
     * @return
     */
    public static ArrayList<SystemMsgBean> getCache() {
        ArrayList<SystemMsgBean> datas = new ArrayList<SystemMsgBean>();
        JSONArray jsonArray = CacheUtils.get(MyApplication.mApplication.getApplicationContext(), "systemmsg").getAsJSONArray(Constants.SYSTEM_MSG);
        if (jsonArray == null) {
            return datas;
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                datas.add(new SystemMsgBean(jsonObject.optString("title"), jsonObject.optString("content"), jsonObject.optLong("time"), jsonObject.optBoolean("read")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return datas;
    }

    /**
     * 保存系统消息到缓存
     *
     * @param datas
     */
    public static void saveCache(ArrayList<SystemMsgBean> datas) {
        if (datas == null) {
            return;
        }
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < datas.size(); i++) {
            SystemMsgBean data = datas.get(i);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("title", data.mTitle);
                jsonObject.put("content", data.mContent);
                jsonObject.put("time", data.mCreateTime);
                jsonObject.put("read", data.mRead);
                jsonArray.put(i, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        CacheUtils.get(MyApplication.mApplication.getApplicationContext(), "systemmsg").put(Constants.SYSTEM_MSG, jsonArray);
    }
}
