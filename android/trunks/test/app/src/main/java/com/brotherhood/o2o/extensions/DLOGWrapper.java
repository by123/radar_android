package com.brotherhood.o2o.extensions;

import android.app.Activity;
import android.content.Context;

import com.brotherhood.o2o.account.model.LoginUserInfo;
import com.brotherhood.o2o.utils.Constants;
import com.brotherhood.o2o.utils.ContextUtils;
import com.brotherhood.o2o.utils.UDIDUtils;
import com.dsstate.v2.DsStateV2API;
import com.dsstate.v2.vo.RequestVo;

/**
 * Created by ZhengYi on 15/6/2.
 * DLOG统计服务的包装类
 */
public class DLOGWrapper {
    private DLOGWrapper() {
    }

    public static void init() {
        Context context = ContextUtils.context();
        String appId = Constants.DGC_GAME_ID;
        String appKey = Constants.DGC_APP_KEY;
        String channel = Constants.APP_CHANNEL;
        String deviceId = UDIDUtils.getUdid(context);
        String sdkVersion = "1.0"; /** 使用SDK的版本，现在是乱填的... **/
        DsStateV2API.initApi(context, appId, appKey, channel, deviceId, sdkVersion);
    }

    /**
     * 当程序加载完毕时调用该函数
     *
     * @param loadingDurationInMills 加载消耗的时长，以毫秒为单位
     */
    public static void onFinishLoading(long loadingDurationInMills) {
        DsStateV2API.LoadingCompleted((int) loadingDurationInMills);
    }

    /**
     * 当用户注册时调用该函数
     *
     * @param userInfo 注册用户的信息
     */
    public static void onUserRegistry(LoginUserInfo userInfo) {
        RequestVo vo = new RequestVo();
        vo.setGameSvrId("");
        vo.setvUsersid(userInfo.mUid);
        DsStateV2API.PlayerRegister(vo);
    }

    /**
     * 当用户登录时调用该函数
     *
     * @param userInfo 登陆用户的信息
     */
    public static void onUserLogin(LoginUserInfo userInfo) {
        RequestVo vo = new RequestVo();
        vo.setGameSvrId("");
        vo.setvUsersid(userInfo.mUid);
        DsStateV2API.PlayerLogin(vo);
    }

    /**
     * 当用户登出时调用该函数
     *
     * @param userInfo 登出用户信息
     */
    public static void onUserLogout(LoginUserInfo userInfo) {
        RequestVo vo = new RequestVo();
        vo.setGameSvrId("");
        vo.setvUsersid(userInfo.mUid);
        DsStateV2API.PlayerLogout(vo);
    }

    /**
     * 当页面切换到暂停状态时调用该函数
     *
     * @param activity 当前的ctivity
     */
    public static void onActivityPause(Activity activity) {
        DsStateV2API.onPause(activity);
    }

    /**
     * 当页面切换到激活状态时调用该函数
     *
     * @param activity 当前的activity
     */
    public static void onActivityResume(Activity activity) {
        DsStateV2API.onResume(activity);
    }
}
