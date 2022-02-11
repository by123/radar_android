package com.brotherhood.o2o.listener;

import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.brotherhood.o2o.R;
import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.bean.BaiduResponseResult;

/**
 * Created with Android Studio.
 */

public abstract class OnBaiduPoiResponseListener<T> implements OnBaseResponseListener<BaiduResponseResult<T>> {

    private static final int SUCCESS_CODE = 0;//请求成功返回码
    /**
     *
     * @param response
     * @param cache    是否缓存response
     */
    @Override
    public void onResponseSuccess(BaiduResponseResult<T> response, boolean cache) {
        int code = response.getStatus();
        if (code == SUCCESS_CODE /*code == HttpStatus.SC_OK || code == HttpStatus.SC_NOT_MODIFIED*/){
            onSuccess(code, "百度POI请求成功",response.getResult(),cache);
        } else {
            onFailure(code,"百度POI请求失败");
        }
    }

    /**
     *
     * @param error
     */
    @Override
    public void onResponseFailure(VolleyError error) {
        String errMsg = error.getMessage();
        if(TextUtils.isEmpty(errMsg)){
            errMsg = NearApplication.mInstance.getString(R.string.httpclient_fail);
        }
        onFailure(-1,errMsg);
    }


    public abstract void onSuccess(int code,String msg,T t,boolean cache);

    public abstract void onFailure(int code, String msg);
}
