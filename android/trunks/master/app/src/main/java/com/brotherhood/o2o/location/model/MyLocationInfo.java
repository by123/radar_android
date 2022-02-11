package com.brotherhood.o2o.location.model;

import com.brotherhood.o2o.ui.widget.deletelistview.DeleteBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ZhengYi on 15/6/8.
 */
public class MyLocationInfo extends DeleteBean implements Serializable {
    /**
     * id
     */
    public int mId;
    /**
     * 地址
     */
    public String mAddress;
    /**
     * 电话
     */
    public String mPhone;
    /**
     * 姓名
     */
    public String mName;
    /**
     * 选中状态
     */
    public boolean mStatu;


    public MyLocationInfo() {
    }


    public static ArrayList<MyLocationInfo> getDatas(String jsonStr) {
        ArrayList<MyLocationInfo> infos = new ArrayList<>();
        MyLocationInfo info = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonStr).getJSONObject("data");
            JSONArray contentArray = jsonObject.getJSONArray("content");
            if (contentArray != null && contentArray.length() > 0) {
                int size = contentArray.length();
                for (int i = 0; i < size; i++) {
                    info = new MyLocationInfo();
                    JSONObject dataObj = contentArray.getJSONObject(i);
                    info.mId = dataObj.optInt("id");
                    info.mAddress = dataObj.optString("address");
                    info.mName = dataObj.optString("receiver");
                    info.mPhone = dataObj.optString("mobile");
                    infos.add(info);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return infos;
    }


}
