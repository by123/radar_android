package com.brotherhood.o2o.chat.model;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2015/12/23 0023.
 */
public class IMSystemMsgBean {

    public int _id;

    public String title;
    public String content;
    public long time;
    public long msgId;

    public boolean hasRead;

    public static IMSystemMsgBean getBean(String json) {
        IMSystemMsgBean bean = new IMSystemMsgBean();
        if (!TextUtils.isEmpty(json)) {
            JSONObject content = null;
            try {
                String s = new String(json.getBytes(), "UTF-8");
                content = new JSONObject(s);
                String title = content.getString("title");
                String c = content.getString("content");
                long create_time = content.getLong("create_time");
                bean.content = c;
                bean.title = title;
                bean.time = create_time;
                return bean;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return bean;
    }
}
