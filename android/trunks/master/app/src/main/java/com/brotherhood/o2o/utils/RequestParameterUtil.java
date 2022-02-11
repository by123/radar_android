package com.brotherhood.o2o.utils;

import android.text.TextUtils;

import com.brotherhood.o2o.config.Constants;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with Android Studio.
 * <p/>
 * Author:Lw
 * <p/>
 * Data:2015/10/23.
 */

public class RequestParameterUtil {


    public static String getParameterToString(Map<String, String> map) {
        return ((String) buildBody(map, ParameterType.String));
    }

    public static Map<String, String> getParameterToMap() {
        return ((Map<String, String>) buildBody(null, ParameterType.Map));
    }
    /**
     * 生成post传输内容
     * @param map
     * @return
     */
    @SuppressWarnings("finally")
    private static Object buildBody(Map<String, String> map, ParameterType type) {
        if (map == null) {
            map = new HashMap<>();
        }
        map.put("pf", "near");
        map.put("from_pf", Constants.LOGIN_TYOE);
        List<NameValuePair> params = new ArrayList<>();
        for (String key : map.keySet()) {
            String value = map.get(key);
            params.add(new BasicNameValuePair(key, value));
        }
        String paramString = URLEncodedUtils.format(params, HTTP.UTF_8);
        return paramString;
    }

    /**
     * todo  根据文件上传接口的设计，来重构该方法
     * @param url
     * @param params
     * @return
     */
    @SuppressWarnings("finally")
    /*private static Object buildBody(Map<String, String> dataMap, ParameterType type) {
        if (dataMap == null) {
            dataMap = new HashMap<String, String>();
        }
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        if (AccountManager.getInstance().hasUser()) {  //如果已登录,提交session_token,uid
            map.put("session_token", AccountManager.getInstance().getUser().session_token);
            dataMap.put("uid", AccountManager.getInstance().getUser().uid);
        }
        map.put("versionCode", String.valueOf(PackageUtil.getVersionCode(MuzwApplication
                .getInstance())));
        map.put("info", DeviceParamsUtil.getDeviceParams());
        if (dataMap.size() > 0) {
            String dataJson = com.alibaba.fastjson.JSONObject.toJSONString(dataMap);
            map.put("data", dataJson);
        }
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (String key : map.keySet()) {
            String value = map.get(key);
            params.add(new BasicNameValuePair(key, value));
        }
        String paramString = URLEncodedUtils.format(params, HTTP.UTF_8);
        String token = MD5.md5sum(URLDecoder.decode(paramString) +
                "51aa699686ad210cbc0f67fff9a84fea");
        if (type == ParameterType.Map) {
            map.put("token", token);
            return map;
        } else {
            return paramString + "&token=" + token;
        }
    }*/


    /**
     * 生成get请求url
     * @param url
     * @param params
     * @return
     */
    public static String buildParamsUrl(String url, Map<String, String> params) {
        if (params == null){
            params = new HashMap<>();
        }
        params.put("pf", "near");
        params.put("from_pf", Constants.LOGIN_TYOE);
        String query = buildQuery(params);
        if (!TextUtils.isEmpty(query)) {
            url = url + "?" + query;
        }
        return url;
    }

    private static String buildQuery(Map<String, String> params) {
        StringBuilder query = new StringBuilder();
        try {
            Set<Map.Entry<String, String>> entries = params.entrySet();
            boolean hasParam = false;
            for (Map.Entry<String, String> entry : entries) {
                String name = entry.getKey();
                String value = entry.getValue();
                if (name != null && value != null) {
                    if (hasParam) {
                        query.append("&");
                    } else {
                        hasParam = true;
                    }
                    query.append(name).append("=").append(URLEncoder.encode(value, HTTP.UTF_8));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return query.toString();
    }


    enum ParameterType {
        Map,
        String,
    }
}
