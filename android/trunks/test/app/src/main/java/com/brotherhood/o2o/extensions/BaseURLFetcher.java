package com.brotherhood.o2o.extensions;

import android.os.Handler;

import com.brotherhood.o2o.utils.ContextUtils;

import java.util.List;

/**
 * Created by ZhengYi on 15/6/2.
 */
public class BaseURLFetcher {
    private static Handler sHandler;

    protected BaseURLFetcher() {
    }

    protected static String parseErrorOrNil(String responseData) {
        return null;
    }

    protected static <T> void postCallback(final Callback<T> callbackOrNil, final T dataOrNil, final String errorOrNil) {
        if (callbackOrNil == null)
            return;

        getHandler().post(new Runnable() {
            @Override
            public void run() {
                callbackOrNil.onCallback(dataOrNil, errorOrNil);
            }
        });
    }

    protected static <T> void postCallback(final Callback_v2<T> callbackOrNil, final List<T> dataSetOrNil, final String errorOrNil) {
        if (callbackOrNil == null)
            return;

        getHandler().post(new Runnable() {
            @Override
            public void run() {
                callbackOrNil.onCallback(dataSetOrNil, errorOrNil);
            }
        });
    }

    protected static void postCallback(final SimpleCallback callbackOrNil, final boolean isSuccess, final String errorOrNil) {
        if (callbackOrNil == null)
            return;

        getHandler().post(new Runnable() {
            @Override
            public void run() {
                callbackOrNil.onCallback(isSuccess, errorOrNil);
            }
        });
    }

    protected static Handler getHandler() {
        if (sHandler == null) {
            sHandler = new Handler(ContextUtils.context().getMainLooper());
        }
        return sHandler;
    }

    public interface Callback<T> {
        void onCallback(T dataOrNil, String errorOrNil);
    }

    public interface Callback_v2<T> {
        void onCallback(List<T> dataSetOrNil, String errorOrNil);
    }

    public interface SimpleCallback {
        void onCallback(boolean isSuccess, String errorOrNil);
    }
}
