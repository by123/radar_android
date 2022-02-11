package com.brotherhood.o2o.application;

import android.app.Application;
import android.content.ServiceConnection;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.brotherhood.o2o.chat.IDSIMManager;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.config.GlideConfigConstant;
import com.brotherhood.o2o.manager.CrashHandler;
import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.manager.StethoManager;
import com.brotherhood.o2o.message.MessagePump;
import com.brotherhood.o2o.service.NearService;
import com.brotherhood.o2o.util.ProcessUtil;
import com.brotherhood.o2o.util.Res;
import com.brotherhood.o2o.wrapper.DGCPassWrapper;
import com.brotherhood.o2o.wrapper.NearBugtagsWrapper;
import com.skynet.library.message.Logger;

/**
 * Created by by.huang on 2015/5/29.
 */
public class NearApplication extends Application {

    private static final String tag = "NearApplication";
    public static NearApplication mInstance;
    private MessagePump mMessagePump;
    private ServiceConnection mServiceConnection;
    private NearService mNearService;
    private Handler mHandler;

    /**
     * 当app到background，内存占过高时，会被系统回收；
     * 这时再次进入app，在某些机器上，系统会再次启动application，并启动栈顶activity，
     * 这时一些数据没有经过初始化，就会出现问题；
     * 这里使用一个标记位，若没经过初始化进入，直接再进行一次初始化流程
     */
    private static boolean mInitFromLogin = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mHandler = new Handler(getMainLooper());
        mMessagePump = new MessagePump();

        initGlide();
        bindNearService();
        //LeakCanary.install(this);

        Res.setContext(this);
        initStetho();
        CrashHandler.getInstance().init(this);
        String pn = ProcessUtil.getCurProcessName(this);
        Log.i(tag, "===process name ====" + pn);
        if (!TextUtils.isEmpty(pn) && pn.contains(":bd_remote")) {
        } else {
            DGCPassWrapper.init();
            IDSIMManager.getInstance().initIP();
        }

        // 初始化Bugtags
        NearBugtagsWrapper.init();
    }

    public boolean getInitFromLogin() {
        return mInitFromLogin;
    }

    public void setInitFromLogin(boolean hasLogin) {
        mInitFromLogin = hasLogin;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private void initGlide() {
        ImageLoaderManager.init(this);
        ImageLoaderManager.setExternalCacheDiskCache(this, Constants.IMAGE_DIR, GlideConfigConstant.MAX_DISK_CACHE_SIZE);
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void bindNearService() {
//        mServiceConnection = new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder binder) {
//                try {
//                    if (binder instanceof NearService.Binder) {
//                        mNearService = ((NearService.Binder) binder).getNearService();
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//                mNearService = null;
//            }
//        };
//        bindService(new Intent(this, NearService.class), mServiceConnection, BIND_AUTO_CREATE);
    }

    /**
     * 方便程序操作该服务
     *
     * @return
     */
    public NearService getNearService() {
//        if (mServiceConnection == null)
//            bindNearService();
//        return mNearService;
        return null;
    }

    public MessagePump getMessagePump() {
        if (mMessagePump == null)
            mMessagePump = new MessagePump();
        return mMessagePump;
    }

    private void initStetho() {
        StethoManager.getInstance().init();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Logger.w(tag, "=====onLowMemory=====");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        switch (level) {
            case TRIM_MEMORY_COMPLETE://内存不足，并且该进程在后台进程列表最后一个，马上就要被清理
                Logger.w(tag, "=====TRIM_MEMORY_COMPLETE=====");
                break;
            case TRIM_MEMORY_MODERATE://内存不足，并且该进程在后台进程列表的中部。
                Logger.w(tag, "=====TRIM_MEMORY_MODERATE=====");
                break;
            case TRIM_MEMORY_BACKGROUND://内存不足，并且该进程是后台进程。
                Logger.w(tag, "=====TRIM_MEMORY_BACKGROUND=====");
                break;
            case TRIM_MEMORY_UI_HIDDEN://内存不足，并且该进程的UI已经不可见了。
                Logger.w(tag, "=====TRIM_MEMORY_UI_HIDDEN=====");
                break;
        }
    }

}
