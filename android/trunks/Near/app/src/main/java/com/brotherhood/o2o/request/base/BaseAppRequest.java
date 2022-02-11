package com.brotherhood.o2o.request.base;

import android.os.Build;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.bean.ResponseResult;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.manager.IDSEnvManager;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.util.DeviceUtil;
import com.brotherhood.o2o.util.RequestParameterUtil;
import com.brotherhood.o2o.util.ZipUtil;
import com.brotherhood.o2o.wrapper.NearBugtagsWrapper;
import com.skynet.library.login.net.LoginManager;

import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 接口访问基类，因为请求服务器无论是get还是post请求，都是要带自定义的header�?
 * �?��其他�?��接口必须继承此类，其他请求接口可以继承BaseRequestWrapper、StringRequest、JsonObjectRequest�?
 */
public abstract class BaseAppRequest<T> extends BaseRequestWrapper<T> {

    private String mUrl;
    /**
     * Post请求内容的字符串
     */
    private String mStrBody;

    /**
     * 参数map
     */
    protected Map<String, String> mParamMap = new HashMap<>();

    private BaseAppRequest(int method, String url, boolean hasHead, OnBaseResponseListener<T> baseResponseListener) {
        super(method, url, hasHead, baseResponseListener);
    }

    /**
     * @param url                  路径
     * @param method               请求方式，只支持POST、GET
     * @param hasHead              是否需要网关(是否需要添加请求头)
     * @param bodyMap              请求参数
     * @param baseResponseListener 回调函数
     */
    public BaseAppRequest(String url, int method, boolean hasHead, Map<String, String> bodyMap, OnBaseResponseListener<T> baseResponseListener) {
        this(method, url, hasHead, baseResponseListener);
        mUrl = url;
        if (Request.Method.POST == method) {
            mStrBody = buildBodyString(bodyMap);
            LogManager.i("mStrBody====== %s", mStrBody);
        }
    }

    /**
     * @param url                  路径
     * @param method               请求方式，只支持POST、GET
     * @param hasHead              是否需要网关(是否需要添加请求头)
     * @param baseResponseListener 回调函数
     */
    public BaseAppRequest(String url, int method, boolean hasHead, OnBaseResponseListener<T> baseResponseListener) {
        this(method, url, hasHead, baseResponseListener);
    }

    /**
     * 接口请求默认用自身的对象作为请求的标识
     */
    @Override
    public Object getRequestTag() {
        return this;
    }

    /**
     * 请求优先级，如果需要调整请求优先级重写此方法
     */
    @Override
    public Request.Priority getPriority() {
        return super.getPriority();
    }

    /**
     * 请求头部
     */
    @Override
    public Map<String, String> getHeaders(String url, int method, boolean hasHead) throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        if (hasHead) {
            String encodeMethod = "POST";
            if (method == Request.Method.POST) {
                encodeMethod = "POST";
            } else if (method == Request.Method.GET) {
                encodeMethod = "GET";
            }

            int versionCode = DeviceUtil.getVersionCode(NearApplication.mInstance);
            String channel = IDSEnvManager.getInstance().getChannel();
            String brand = Build.BRAND;
            String OsversionName = "Android"+Build.VERSION.RELEASE;
            String phoneType = DeviceUtil.getPhoneType();
            String buildVersionCode = DeviceUtil.getVersionName(NearApplication.mInstance);


            String userAgentValue = "Near/"+ versionCode +" ("+ brand +"; "+ OsversionName +"; Scale/2.00) | channel="+ channel +"&version="+
                    versionCode
                    +"&phoneType="+ phoneType
                    +"&buildVersion="+ buildVersionCode;

            String header = LoginManager.getInstance().generateOAuthHeader(encodeMethod, url);
            headers.put("Authorization", header);
            headers.put("User-Agent", userAgentValue);
            LogManager.d("key:Authorization\r\n--->value:%s", header);
            LogManager.d("key:User-Agent\r\n--->value:%s", userAgentValue);
        }
        // 默认启动gzip压缩
        headers.put("Accept-Encoding", "gzip");
        return headers;
    }

    /**
     * post的body内容
     */
    @Override
    public byte[] getBody() throws AuthFailureError {
        byte[] content = null;
        if (mStrBody == null) {
            mStrBody = buildBodyString();
        }
        try {
            content = mStrBody.getBytes(HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return content;
    }


    /**
     * 拼接Get请求链接
     *
     * @param url
     * @param params Get请求的参数
     * @return
     */
    public static String buildParamsUrl(String url, Map<String, String> params) {
        String query = RequestParameterUtil.getParameterToString(params);
        if (!TextUtils.isEmpty(query)) {
            url = url + query;
        }
        return url;
    }

    /**
     * 生成post传输内容
     *
     * @return
     */
    @SuppressWarnings("finally")
    private String buildBodyString() {
        return buildBodyString(mParamMap);
    }

    /**
     * 生成post传输内容
     *
     * @param args
     * @return
     */
    @SuppressWarnings("finally")
    private String buildBodyString(Map<String, String> args) {
        return RequestParameterUtil.getParameterToString(args);
    }

    /**
     * 根据网络返回数据，进行解压解密等操作
     */
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {

        if (response == null || response.data == null) {
            NearBugtagsWrapper.bugtagsLog("url:"+mUrl
                    +"\r\nrequestBody:"+mStrBody
                    +"\r\n"
                    + response == null ? "response is null" : "response.data is null");
            return Response.error(null);
        }
        byte[] data;// volley缓存是针对处理后的response对象，故只针对response的data进行处理
        if (response.headers.containsKey("Content-Encoding") && response.headers.get("Content-Encoding").equals("gzip")) {
            data = ZipUtil.decompressZipToByte(response.data);
            if (data == null) {
                NearBugtagsWrapper.bugtagsLog("url:" + mUrl
                        + "\r\nrequestBody:" + mStrBody
                        + "\r\n"
                        + "byte[] data = ZipUtil.decompressZipToByte(response.data); data is null");
                return null;
            }
        } else {
            data = response.data;
        }
        Response<T> rsp = parseResponse(response, data);
        if (rsp != null) {
            return rsp;
        } else {
            return Response.error(new VolleyError());
        }
    }

    /**
     * 解析数据为相应的对象，解析内容可直接使用data参数
     *
     * @param response
     * @param data     解压或解密后的数据内容
     * @return
     */
    protected Response<T> parseResponse(NetworkResponse response, byte[] data) {
        String json = "";
        try {
            json = new String(data, "UTF-8");
            //json = URLDecoder.decode(json, "UTF-8");
            LogManager.i("json====== %s", json);
            //针对  NearByBuildingRequest 请求返回数据非json格式的处理
            if (!TextUtils.isEmpty(json) && json.startsWith("renderReverse&&renderReverse(")) {
                json = json.substring("renderReverse&&renderReverse(".length(), json.length() - 1);
            }
            JSONObject jo = new JSONObject(json);
            if (jo != null) {
                if (!jo.isNull("error")) {
                    NearBugtagsWrapper.bugtagsLog("url:"+mUrl
                            +"\r\nrequestBody:"+mStrBody
                            +"\r\njson:"+json
                            +"\r\nparseResponse result is null");
                    return null;
                } else {
                    String jsonString = jo.toString();
                    Type genType = getClass().getGenericSuperclass();
                    Type trueType = ((ParameterizedType) genType).getActualTypeArguments()[0];
                    T type = (T) JSON.parseObject(jsonString, trueType);
                    if (jo.has("c")) {
                        ((ResponseResult) type).setCode(jo.getInt("c"));
                    }else if (jo.has("code")){
                        ((ResponseResult) type).setCode(jo.getInt("code"));
                    }
                    NearBugtagsWrapper.bugtagsLog("url:"+mUrl
                            +"\r\nrequestBody:"+mStrBody
                            +"\r\njson:"+json
                            +"\r\nparseResponse success");
                    return Response.success(type, HttpHeaderParser.parseCacheHeaders(response));
                }
            } else {
                NearBugtagsWrapper.bugtagsLog("url:"+mUrl
                        +"\r\nrequestBody:"+mStrBody
                        +"\r\njson:"+json
                        +"\r\nJSONObject jo = new JSONObject(json);--->jo is null");
                return null;
            }
        } catch (Exception e) {
            NearBugtagsWrapper.bugtagsLog("url:"+mUrl
                    +"\r\nrequestBody:"+mStrBody
                    +"\r\njson:"+json
                    +"\r\nparseResponse error exception:"+e.getMessage());
            LogManager.e(e);
            return Response.error(new ParseError((e)));
        }
    }


}
