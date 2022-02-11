package com.brotherhood.o2o.listener;

import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.brotherhood.o2o.R;
import com.brotherhood.o2o.application.MyApplication;
import com.brotherhood.o2o.model.ResponseResult;

/**
 * Created with Android Studio.
 * <p/>
 * Author:Lw
 * <p/>
 * Data:2015/9/16.
 */

public abstract class OnResponseListener<T> implements OnBaseResponseListener<ResponseResult<T>> {

    private static final int SUCCESS_CODE = 0;//请求成功返回码
    /**
     *
     * @param response
     * @param cache    是否缓存response
     */
    @Override
    public void onResponseSuccess(ResponseResult<T> response, boolean cache) {
        int code = response.getCode();
        if (code == SUCCESS_CODE /*code == HttpStatus.SC_OK || code == HttpStatus.SC_NOT_MODIFIED*/){
            onSuccess(code,response.getMsg(),response.getData(),cache);
        } else {
            onFailure(code,response.getMsg());
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
            errMsg = MyApplication.mApplication.getString(R.string.httpclient_fail);
        }
        onFailure(-1,errMsg);
    }


    public abstract void onSuccess(int code,String msg,T t,boolean cache);

    public abstract void onFailure(int code, String msg);
}
