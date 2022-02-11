package com.brotherhood.o2o.util;

import android.app.Activity;

import com.brotherhood.o2o.config.Constants;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by ZhengYi on 15/6/11.
 */
public class UmengAnalysisUtil {
    private UmengAnalysisUtil() {
    }

    public static void init() {
        AnalyticsConfig.setChannel(Constants.APP_CHANNEL);
        MobclickAgent.openActivityDurationTrack(false);
    }

    public static void onPageStart(Object page) {
        MobclickAgent.onPageStart(page.getClass().getSimpleName());
    }

    public static void onPageEnd(Object page) {
        MobclickAgent.onPageEnd(page.getClass().getSimpleName());
    }

    public static void onActivityResume(Activity activity) {
        MobclickAgent.onResume(activity);
    }

    public static void onActivityPause(Activity activity) {
        MobclickAgent.onPause(activity);
    }
}
