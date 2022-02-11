package com.brotherhood.o2o.listener;

import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.brotherhood.o2o.R;
import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.bean.GoogleResponseResult;

/**
 * Created with Android Studio.
 */

public abstract class OnGooglePoiResponseListener<T> implements OnBaseResponseListener<GoogleResponseResult<T>> {

    /**
     * @param response
     * @param cache    是否缓存response
     */
    @Override
    public void onResponseSuccess(GoogleResponseResult<T> response, boolean cache) {
        String status = response.getStatus();
        if (TextUtils.isEmpty(status)){
            onFailure(-1, "谷歌POI请求失败");
        }else {
            if (status.equalsIgnoreCase("OK")){
                onSuccess(0, "谷歌POI请求成功",response.getResults(),cache);
            } else {
                onFailure(-1,"谷歌POI请求失败");
            }
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
