package com.brotherhood.o2o.wrapper;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;

import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.bean.account.UserInfo;
import com.brotherhood.o2o.manager.IDSEnvManager;
import com.brotherhood.o2o.util.DeviceUtil;
import com.bugtags.library.Bugtags;
import com.bugtags.library.BugtagsOptions;

/**
 * Created by mark on 12/31/15.
 */
public class NearBugtagsWrapper {

    private static final String APP_KEY = "8e91a89d8702585e6038009e0e3c9b9e";

    // MARK: 自定义数据

    private static final String BCustomUserID = "data_user_id";
    private static final String BCustomAppChannel = "data_app_channel";

    private NearBugtagsWrapper() {
        // No permission to create
    }

    public static void init() {
        IDSEnvManager.IDSEnv env = IDSEnvManager.getInstance().getEnv();
        if (IDSEnvManager.IDSEnv.DEV != env) {
            Context context = NearApplication.mInstance;
            BugtagsOptions.Builder builder = new BugtagsOptions.Builder();
            // builder.trackingLocation(true);//是否获取位置
            builder.trackingCrashLog(true);//是否收集crash
            builder.trackingConsoleLog(true);//是否收集console log
            builder.trackingUserSteps(true);//是否收集用户操作步骤
            //自定义版本名称
            builder.versionName(DeviceUtil.getAppVersionName(context, context.getPackageName()));
            builder.versionCode(DeviceUtil.getVersionCode(context));//自定义版本号
            BugtagsOptions options = builder.build();

            if (IDSEnvManager.IDSEnv.TEST == env) {
                Bugtags.start(APP_KEY, NearApplication.mInstance, Bugtags.BTGInvocationEventShake, options);
            } else  if (IDSEnvManager.IDSEnv.OFFICIAL == env) {
                Bugtags.start(APP_KEY, NearApplication.mInstance, Bugtags.BTGInvocationEventNone, options);
            }

            String channel = IDSEnvManager.getInstance().getChannel();
            Bugtags.setUserData(BCustomAppChannel, channel);
        }
    }

    public static void resume(Activity activity) {
        IDSEnvManager.IDSEnv env = IDSEnvManager.getInstance().getEnv();
        if (IDSEnvManager.IDSEnv.DEV == env) {
            return;
        }

        Bugtags.onResume(activity);
    }

    public static void pause(Activity activity) {
        IDSEnvManager.IDSEnv env = IDSEnvManager.getInstance().getEnv();
        if (IDSEnvManager.IDSEnv.DEV == env) {
            return;
        }

        Bugtags.onPause(activity);
    }

    public static void touchDidpatcher(Activity activity, MotionEvent event) {
        IDSEnvManager.IDSEnv env = IDSEnvManager.getInstance().getEnv();
        if (IDSEnvManager.IDSEnv.DEV == env) {
            return;
        }

        Bugtags.onDispatchTouchEvent(activity, event);
    }

    /**
     * 设置自定义的用户数据.
     * @param user  当前登录的用户.
     */
    public static void setUserData(UserInfo user) {
        IDSEnvManager.IDSEnv env = IDSEnvManager.getInstance().getEnv();
        if (IDSEnvManager.IDSEnv.DEV == env) {
            return;
        }

        if (null != user) {
            // 用户 id
            Bugtags.setUserData(BCustomUserID, user.mUid);
        }
    }

    /**
     * 统计日志上报到 Bugtags 后台, 便于分析.特别是网络数据.
     * @param log 需要收集的信息.
     */
    public static void bugtagsLog(String log) {
        IDSEnvManager.IDSEnv env = IDSEnvManager.getInstance().getEnv();
        if (IDSEnvManager.IDSEnv.DEV == env) {
            return;
        }

        if (null == log || log.length() <=0) {
            return;
        }

        boolean canLog = false;

        switch (env) {
            case DEV:
                canLog = false;
                break;

            case TEST:
            case OFFICIAL:
                canLog = true;
                break;
        }

        //Bugtags日志工具，添加自定义日志，不会在控制台输出
        if (canLog) {
            Bugtags.log(log);
        }
    }
}
