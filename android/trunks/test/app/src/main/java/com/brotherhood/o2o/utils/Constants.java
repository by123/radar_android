package com.brotherhood.o2o.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by by.huang on 2015/6/1.
 */
public class Constants {
    public static final String DGC_GAME_ID = "10939";
    public static final String DGC_APP_KEY = "988aac7440194b606ba5";
    public static final String DGC_APP_SECRET = "aa2ef24d5e2971534dce";
    public static final String APP_CHANNEL = "official";
    public static final boolean IS_DEBUG = true;

    public static final String URL_ROOT="http://192.168.4.88:8081";
    public static final String URL_APP_UPDATE=URL_ROOT+"/versions/info.json";

    /**
     * 发送验证码的时间间隔，以秒为单位
     */
    public static final int SEND_VERIFY_CODE_DURATION_IN_SECOND = 10;

    //屏幕
    public static int ScreenWidth = 720;
    public static int ScreenHeight = 1280;
    public final static String ROOTDIR=Environment.getExternalStorageDirectory().getPath()+"/o2o";
    //缓存地址
    public final static String CacheDir = ROOTDIR+"/cache";
    //存储更新包路径
    public final static String ApkDir = ROOTDIR+"/apk";
    //雷达使用
    public final static int RADARVIEW_DOWN = 101;
    public final static int RADARVIEW_MOVE = 102;
    public final static int RADARVIEW_UP = 103;
    public final static int SHOW_RADAR_PULL_DOWM = 104;
    public final static int SHOW_CHANGE_POSITION = 105;
    public final static int HIDE_GALLERY_VIEW = 106;
    public final static int SHOW_RADAR_PULL_UP = 107;
    public final static int RADAR_PULL_DOWN_DISTANCE_SHORT = 108;
    public final static int RADAR_FINDING_AROUND_PEOPLE = 109;
    public final static int UPDATE_ALP_VALUE = 110;

    //
    public final static String PREFER_BOTTOMBAR="bottombar";

    //动画相关
    public final static String SCALE_X="scaleX";
    public final static String SCALE_Y="scaleY";
    public final static String TRANSLATION_X="translationX";
    public final static String TRANSLATION_Y="translationY";
    public final static String ALPHA="alpha";

    //客服电话
    public final static String CALL="8888 8878";
    //系统消息缓存
    public final static String SYSTEM_MSG="system_msg";



}
