package com.brotherhood.o2o.listener;

/**
 * Created by jl.zhang on 2015/12/16.
 */
public interface OnCommonResponseListener<T> {

    void onSuccess(T data);
    void onFailed(String errorMsg);

}
