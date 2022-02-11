package com.brotherhood.o2o.application;

import android.app.Application;

import com.brotherhood.o2o.extensions.fresco.ImageConfig;
import com.brotherhood.o2o.message.MessagePump;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.skynet.library.message.MessageManager;

//import com.squareup.leakcanary.LeakCanary;


/**
 * Created by by.huang on 2015/5/29.
 */
public class MyApplication extends Application {

    public static MyApplication mApplication;
    private MessagePump mMessagePump;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        mMessagePump = new MessagePump();
        //LeakCanary.install(this);
        initFresco();
//        DLOGWrapper.init();
        initMesseage();
    }

    private void initFresco() {
        Fresco.initialize(this, ImageConfig.configureCaches(this));
    }

    private void initMesseage() {
        //测试环境 ：msg.ids111.com:4430(内网，外网均可以访问)
        MessageManager.getDefault(this).setIpAndPort("msg.ids111.com", 4430);
//        //开发环境(内网访问)
//        MessageManager.getDefault(this).setIpAndPort("msg.ids111.com", 8000);
//        //中山正式环境
//        MessageManager.getDefault(this).setIpAndPort("long.uu.cc", 4430);

    }

    public MessagePump getMessagePump() {
        if(mMessagePump == null)
            mMessagePump = new MessagePump();
        return mMessagePump;
    }
}
