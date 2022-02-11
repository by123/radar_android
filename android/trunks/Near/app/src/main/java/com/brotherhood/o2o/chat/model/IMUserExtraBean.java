package com.brotherhood.o2o.chat.model;

import android.text.TextUtils;

import com.brotherhood.o2o.bean.account.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/12/26 0026.
 */
public class IMUserExtraBean {

    private final static String KEY_USER_AVATAR = "user_avatar";
    private final static String KEY_USER_ID = "user_id";
    private final static String KEY_USER_NAME = "user_name";
    private final static String KEY_USER_GENDER = "user_gender";
    private final static String KEY_USER_EXTRA_FALG_KEY = "extra_flag_key";
    private final static String KEY_USER_FROM_ANDROID = "user_from_android";

    public String name;
    public String userId;
    public String avatar;
    public String gender;
    public boolean fromAndroid = false;

    public static IMUserExtraBean getBean(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        IMUserExtraBean bean = new IMUserExtraBean();
        try {
            if (!json.contains("user_extra")) {
                return null;
            }
            JSONObject jsonObject = new JSONObject(json);
            bean.avatar = jsonObject.getString(KEY_USER_AVATAR);
            bean.userId = jsonObject.getString(KEY_USER_ID);
            bean.name = jsonObject.getString(KEY_USER_NAME);
            if (jsonObject.has(KEY_USER_FROM_ANDROID)) {
                bean.fromAndroid = jsonObject.getBoolean(KEY_USER_FROM_ANDROID);
            }
//            bean.gender = jsonObject.getString(KEY_USER_GENDER);
//            bean.gender = jsonObject.getInt(KEY_USER_GENDER) + "";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bean;
    }

    public static String createUserExtraData(UserInfo info) {
        HashMap map = new HashMap();
//        map.put(KEY_USER_EXTRA_FALG_KEY, "user_extra");
//        map.put(KEY_USER_AVATAR, info.mIcon);
//        map.put(KEY_USER_ID, info.mUid);

//        try {
//            map.put(KEY_USER_EXTRA_FALG_KEY, new String("user_extra".getBytes("GBK"),"UTF-8"));
//            map.put(KEY_USER_AVATAR, new String(info.mIcon.getBytes("GBK"),"UTF-8"));
//            map.put(KEY_USER_ID, new String(info.mUid.getBytes("GBK"),"UTF-8"));
//
//            String iso = new String(info.mNickName.getBytes("UTF-8"),"UTF-8");
//            String name = new String(iso.getBytes(),"iso-8859-1");
//            map.put(KEY_USER_NAME, iso);
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        map.put(KEY_USER_NAME, info.mNickName);


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_USER_EXTRA_FALG_KEY, "user_extra");
            jsonObject.put(KEY_USER_AVATAR, info.mIcon);
            jsonObject.put(KEY_USER_ID, info.mUid);
            jsonObject.put(KEY_USER_NAME, info.mNickName);
//            try {
//                jsonObject.put(KEY_USER_NAME, new String(info.mNickName.getBytes("GB2312"), "GB2312"));
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
            jsonObject.put(KEY_USER_FROM_ANDROID, true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
//        catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//        }
        return jsonObject.toString();
    }
}
