package com.brotherhood.o2o.request.base;

import com.alibaba.fastjson.JSON;
import com.android.volley.ParseError;
import com.android.volley.VolleyError;
import com.brotherhood.o2o.bean.ResponseResult;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.network.OkHttpClientManager;
import com.brotherhood.o2o.util.RequestParameterUtil;
import com.brotherhood.o2o.wrapper.NearBugtagsWrapper;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


/**
 * todo  注意：OKHttp中未封装通用请求参数，需 Map<String,String> params = RequestParameterUtil.getParameterToMap();创建带通用参数的map
 * Created with Android Studio.
 *
 * @param <T>
 */

public abstract class BaseUploadRequest<T> {

    private String mUrl = "";
    private String mFileName = "";
    private String mFilePath = "";
    private Map<String, String> mParams;
    private OnBaseResponseListener<T> mListener;
    private Object mTag;

    private OkHttpClientManager okHttp;


    public BaseUploadRequest(String url, String fileName, String filePath, Map<String, String> params, Object tag, final OnBaseResponseListener<T> listener) {
        mUrl = url;
        mFileName = fileName;
        mFilePath = filePath;
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("pf", "near");
        params.put("from_pf", Constants.LOGIN_TYPE);
        mParams = params;
        mTag = tag;
        mListener = listener;
    }

    ;

    /**
     * @param hasHead 是否需要网关
     */
    public void postAsyn(boolean hasHead) {
        Type genType = getClass().getGenericSuperclass();
        final Type trueType = ((ParameterizedType) genType).getActualTypeArguments()[0];

        okHttp = OkHttpClientManager.getInstance();
        OkHttpClientManager.UploadDelegate uploadDelegate = okHttp._getUploadDelegate();
        OkHttpClientManager.ResultCallback<String> callbcak = okHttp.new ResultCallback<String>() {

            @Override
            public void onError(Request request, Exception e) {
                NearBugtagsWrapper.bugtagsLog("url:"+mUrl
                        +"\r\nrequestFileName:"+mFileName
                        +"\r\nrequestFilePath:"+mFilePath
                        +"\r\nrequest error exception:"+e.getMessage());
                mListener.onResponseFailure(new VolleyError(e));

            }

            @Override
            public void onResponse(String response) {
                try {
                    T type = (T) JSON.parseObject(response, trueType);
                    JSONObject jo = new JSONObject(response);
                    if (jo.has("c")) {
                        ((ResponseResult) type).setCode(jo.getInt("c"));
                    } else if (jo.has("code")) {
                        ((ResponseResult) type).setCode(jo.getInt("code"));
                    }
                    NearBugtagsWrapper.bugtagsLog("url:"+mUrl
                            +"\r\nrequestFileName:"+mFileName
                            +"\r\nrequestFilePath:"+mFilePath
                            +"\r\njson"+response
                            + "\r\nparseResponse success");
                    mListener.onResponseSuccess(type, true);
                } catch (JSONException e) {
                    NearBugtagsWrapper.bugtagsLog("url:"+mUrl
                            +"\r\nrequestFileName:"+mFileName
                            +"\r\nrequestFilePath:"+mFilePath
                            +"\r\njson:"+response
                            +"\r\nparseResponse error exception:"+e.getMessage());
                    LogManager.e(e);
                    mListener.onResponseFailure(new ParseError((e)));
                }

            }
        };
        try {
            if (hasHead) {
                uploadDelegate.postAsynWithHead(mUrl, mFileName, mFilePath == null?null:new File(mFilePath), mParams, callbcak, mTag);
            } else {
                uploadDelegate.postAsyn(mUrl, mFileName, mFilePath == null?null:new File(mFilePath), RequestParameterUtil.getParameterToMap(), callbcak, mTag);
            }
        } catch (Exception e) {
            NearBugtagsWrapper.bugtagsLog("url:"+mUrl
                    +"\r\nrequestFileName:"+mFileName
                    +"\r\nrequestFilePath:"+mFilePath
                    +"\r\n:requestError"+e.getMessage());
            mListener.onResponseFailure(new VolleyError(e.getMessage(), e.getCause()));
        }
    }

    public void cancel() {
        okHttp._cancelTag(mTag);
    }

}
