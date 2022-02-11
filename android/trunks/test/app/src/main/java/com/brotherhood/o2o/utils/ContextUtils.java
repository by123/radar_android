package com.brotherhood.o2o.utils;

import android.content.Context;

import com.brotherhood.o2o.MyApplication;

/**
 * Created by ZhengYi on 15/6/2.
 */
public class ContextUtils {
    private ContextUtils() {
    }

    public static Context context() {
        return MyApplication.mApplication.getApplicationContext();
    }
}
