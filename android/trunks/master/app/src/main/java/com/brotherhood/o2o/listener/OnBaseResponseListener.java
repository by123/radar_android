package com.brotherhood.o2o.listener;

import com.android.volley.VolleyError;

/**
 * Created with Android Studio.
 * <p/>
 * Author:Lw
 * <p/>
 * Data:2015/9/16.
 */


/**
 * 用来替代Response.Listener和Response.ErrorListener接口
 *
 * @param <T>
 */
public interface OnBaseResponseListener<T> {


    /**
     *
     * @param response
     * @param cache    是否缓存response
     */
    public void onResponseSuccess(T response, boolean cache);

    /**
     * 请求失败
     *
     * @param error
     */
    public void onResponseFailure(VolleyError error);


}
