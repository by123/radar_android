package com.brotherhood.o2o.util;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by Administrator on 2016/1/15 0015.
 */
public class ProcessUtil {

    /**
     * 得到进程名称
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
