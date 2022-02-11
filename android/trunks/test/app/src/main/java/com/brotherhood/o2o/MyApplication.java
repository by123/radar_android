package com.brotherhood.o2o;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.brotherhood.o2o.extensions.DLOGWrapper;


/**
 * Created by by.huang on 2015/5/29.
 */
public class MyApplication extends Application {

    public static MyApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        initFresco();
//        DLOGWrapper.init();
    }

    private void initFresco() {
        //初始化fresco
        Fresco.initialize(this);
    }
}
