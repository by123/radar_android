//package com.brotherhood.o2o.wrapper;
//
//import android.app.Activity;
//
//import com.brotherhood.o2o.application.NearApplication;
//import com.brotherhood.o2o.config.Constants;
//import com.brotherhood.o2o.util.DeviceUtil;
//import com.brotherhood.o2o.util.UDIDUtils;
//import com.dsstate.v2.DsStateV2API;
//import com.dsstate.v2.vo.RequestVo;
//
///**
// * Created by ZhengYi on 15/6/2.
// * DLOG统计服务的包装类
// */
//public class DLOGWrapper {
//    private DLOGWrapper() {
//    }
//
//    public static void init() {
//        String appId = Constants.DGC_GAME_ID;
//        String appKey = Constants.DGC_APP_KEY;
//        String channel = Constants.APP_CHANNEL;
//        String deviceId = UDIDUtils.getUdid(NearApplication.mInstance);
//        String sdkVersion = DeviceUtil.getVersionName(NearApplication.mInstance); /** 使用SDK的版本，现在是乱填的... **/
//        DsStateV2API.initApi(NearApplication.mInstance, appId, appKey, channel, deviceId, sdkVersion);
//    }
//
//    /**
//     * 当程序加载完毕时调用该函数
//     *
//     * @param loadingDurationInMills 加载消耗的时长，以毫秒为单位
//     */
//    public static void onFinishLoading(long loadingDurationInMills) {
//        DsStateV2API.LoadingCompleted((int) loadingDurationInMills);
//    }
//
//    /**
//     * 当用户注册时调用该函数
//     * @param userId 注册用户的信息
//     */
//    public static void onUserRegistry(String userId) {
//        RequestVo vo = new RequestVo();
//        vo.setGameSvrId("");
//        vo.setvUsersid(userId);
//        DsStateV2API.PlayerRegister(vo);
//    }
//
//    /**
//     * 当用户登录时调用该函数
//     * @param userId 登陆用户的信息
//     */
//    public static void onUserLogin(String userId) {
//        RequestVo vo = new RequestVo();
//        vo.setGameSvrId("");
//        vo.setvUsersid(userId);
//        DsStateV2API.PlayerLogin(vo);
//    }
//
//    /**
//     * 当用户登出时调用该函数
//     * @param userId 登出用户信息
//     */
//    public static void onUserLogout(String userId) {
//        RequestVo vo = new RequestVo();
//        vo.setGameSvrId("");
//        vo.setvUsersid(userId);
//        DsStateV2API.PlayerLogout(vo);
//    }
//
//    /**
//     * 当页面切换到暂停状态时调用该函数
//     *
//     * @param activity 当前的ctivity
//     */
//    public static void onActivityPause(Activity activity) {
//        DsStateV2API.onPause(activity);
//    }
//
//    /**
//     * 当页面切换到激活状态时调用该函数
//     *
//     * @param activity 当前的activity
//     */
//    public static void onActivityResume(Activity activity) {
//        DsStateV2API.onResume(activity);
//    }
//}
