package com.brotherhood.o2o.category.helper;

import android.os.Looper;

import com.brotherhood.o2o.category.model.CategoryHomeInfo;
import com.brotherhood.o2o.extensions.BaseURLFetcher;

import java.lang.ref.WeakReference;

/**
 * Created by ZhengYi on 15/6/2.
 */
public class CategoryURLFetcher extends BaseURLFetcher {
    public static void fetchCategoryHomeInfo(Callback<CategoryHomeInfo> handler) {
        final WeakReference<Callback<CategoryHomeInfo>> handlerRef = new WeakReference<>(handler);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000L);
                    CategoryHomeInfo data = new CategoryHomeInfo();
                    postCallback(handlerRef.get(), data, null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
