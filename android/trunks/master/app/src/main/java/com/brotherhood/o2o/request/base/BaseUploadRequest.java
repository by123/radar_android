package com.brotherhood.o2o.request.base;

import com.alibaba.fastjson.JSON;
import com.android.volley.VolleyError;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.network.OkHttpClientManager;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.utils.RequestParameterUtil;
import com.squareup.okhttp.Request;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * Created with Android Studio.
 * 文件上传
 * @param <T>
 */

public abstract class BaseUploadRequest<T> {

    private String url = "";
    private String fileName = "";
    private String filePath = "";
    private OnBaseResponseListener<T> listener;
    private Object tag;

    private OkHttpClientManager okHttp;


    public BaseUploadRequest(String url,String fileName, String filePath,Object tag, final OnBaseResponseListener<T> listener){
        this.url = url;
        this.fileName = fileName;
        this.filePath = filePath;
        this.listener = listener;
    };

    public void postAsyn() {

        Type genType = getClass().getGenericSuperclass();
        final Type trueType = ((ParameterizedType) genType).getActualTypeArguments()[0];
        okHttp = OkHttpClientManager.getInstance();
        OkHttpClientManager.UploadDelegate uploadDelegate = okHttp._getUploadDelegate();
        OkHttpClientManager.ResultCallback<String> callbcak = okHttp.new ResultCallback<String>() {

            @Override
            public void onError(Request request, Exception e) {
                listener.onResponseFailure(null);
            }

            @Override
            public void onResponse(String response) {
                String reponseText = response;
                LogManager.i("responseJson: %s", reponseText);
                listener.onResponseSuccess((T) JSON.parseObject(reponseText, trueType),true);
            }
        };
        try {
            uploadDelegate.postAsyn(Constants.URL_ROOT_V1+url,fileName, new File(filePath), RequestParameterUtil.getParameterToMap(), callbcak, tag);
        }catch (Exception e){
            listener.onResponseFailure((VolleyError) e);
        }

    }


    /**
     * 取消上传任务
     */
    public void cancel(){
        okHttp._cancelTag(tag);
    }

}
