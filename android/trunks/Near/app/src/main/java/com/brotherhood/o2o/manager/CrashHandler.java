package com.brotherhood.o2o.manager;

import android.content.Context;

import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.util.ActivityUtils;

import java.lang.Thread.UncaughtExceptionHandler;

public class CrashHandler implements UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    private static CrashHandler INSTANCE = new CrashHandler();
    private UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context ctx) {
        mContext = ctx.getApplicationContext();
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return true;
        }

        ex.printStackTrace();
        //quit();

        ActivityUtils.getScreenManager().popAllActivity();
        NearApplication.mInstance.getMessagePump().destroyMessagePump();
        System.exit(0);

        return false;
    }

    // MARK: 废弃

    /*
    private void quit() {
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, R.string.app_crash, Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(500);//wait一下，等待下效果，再蹦
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ActivityUtils.getScreenManager().popAllActivity();
                NearApplication.mInstance.getMessagePump().destroyMessagePump();
                System.exit(0);
                Looper.loop();
            }
        }.start();
    }
    */
}
