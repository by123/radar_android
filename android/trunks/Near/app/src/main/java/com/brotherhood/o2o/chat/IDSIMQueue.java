package com.brotherhood.o2o.chat;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created by Administrator on 2015/12/17 0017.
 */
public class IDSIMQueue {
    private static IDSIMQueue instance;
    private HandlerThread mThread;
    private Handler mHandler;

    private IDSIMQueue() {
        init();
    }

    public static IDSIMQueue getInstance() {
        if (instance == null) {
            instance = new IDSIMQueue();
        }
        return instance;
    }

    public void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    private void init() {
        if (mThread == null) {
            mThread = new HandlerThread("im_thread");
            mThread.start();
            mHandler = new Handler(mThread.getLooper());
        }
    }

}
