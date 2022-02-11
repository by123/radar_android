package com.brotherhood.o2o.config;

import android.os.Environment;

/**
 * Created by by.huang on 2015/6/1.
 */
public class Constants {
    /**
     * DGC后台分配的AppId
     */
    public static final String DGC_GAME_ID = "10939";

    /**
     * DGC后台分配的AppKey
     */
    public static final String DGC_APP_KEY = "988aac7440194b606ba5";

    /**
     * DGC后台分配的AppSecret
     */
    public static final String DGC_APP_SECRET = "aa2ef24d5e2971534dce";

    /**
     * 微信开发者平台分配的AppId
     */
    public static final String WEXIN_APP_ID = "wx06465888377012dd";

    /**
     * 微信开发者平台分配的AppSecret
     */
    public static final String WEIXIN_APP_SECRET = "1dcea83850044bae5ba07994ab69889f";

    /**
     * 微信支付的AppKey
     */
    public static final String WEIXIN_APP_PAY_KEY = "wx3ca536a95915520b";

    /**
     * 百度LSB服务的ApiKey
     */
    public static final String BAIDU_LBS_APP_KEY = "0t0oIayPSGfK7k3A9nxt4lEa";

    public static final String BAIDU_LBS_SECURITY_KEY = "EE:C9:7B:20:2B:18:DC:36:33:E3:8D:F3:6F:CD:D5:71:4F:AC:EB:D4;com.brotherhood.o2o";

    /**
     * 渠道号
     */
    public static final String APP_CHANNEL = "official";

    /**
     * 是否是DEBUG模式
     */
    public static final boolean IS_DEBUG = true;

    /**
     * 发送验证码的时间间隔，以秒为单位
     */
    public static final int SEND_VERIFY_CODE_DURATION_IN_SECOND = 180;

    /**
     * 屏幕
     */
    public static int SCREEN_WIDTH = 720;
    public static int SCREEN_HEIGHT = 1280;
    public static final double DEFAULT_HEIGHT = 1280.00;
    public static double proportion = 1.0;

    /**
     * 本地地址相关
     */
    public final static String ROOTDIR = Environment.getExternalStorageDirectory().getPath() + "/near";
    //缓存地址
    public final static String CacheDir = ROOTDIR + "/cache";
    //保存图片地址
    public final static String ImageDir = "/near/img";
    //保存小图片地址
    public final static String ImageSmallDir = "/near/smallImg";
    //临时图片
    public final static String TEMP_ImageDir = ImageDir + "/temp.jpg";
    //存储更新包路径
    public final static String ApkDir = ROOTDIR + "/apk";

    /**
     * 雷达使用
     */
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
    public static double dLargestDistance = 0;
    public static double dSmallestDistance = 0;
    public static int HEAD_DELAY = 600;
    public static int VALID_VALUE = 60;

    public static double toRad(double X) {
        return X * Math.PI / 180.0;
    }

    public static double toDeg(double X) {
        return X * 180.0 / Math.PI;
    }

    public final static String PREFER_BOTTOMBAR = "bottombar";
    public final static String PREFER_LOGIN_TYPE = "login_type";

    /**
     * 动画相关
     */
    public final static String SCALE_X = "scaleX";
    public final static String SCALE_Y = "scaleY";
    public final static String TRANSLATION_X = "translationX";
    public final static String TRANSLATION_Y = "translationY";
    public final static String ALPHA = "alpha";

    /**
     * 客服电话
     */
    public final static String CALL = "8888 8878";
    /**
     * 系统消息缓存
     */
    public final static String SYSTEM_MSG = "system_msg";
    public final static long SYSTEM_ID = 10000L;

    /**
     * 首次打开配置
     */
    public static String FirstRun = "first_run";

    /**
     * 地址缓存
     */
    public static String LocationCche = "location_cache";

    /**
     * 相册获取
     */
    public static int REQUEST_CODE_PICK_IMAGE = 0;
    /**
     * 拍照获取
     */
    public static int REQUEST_CODE_CAPTURE_CAMEIA = 1;


    /**
     * 网络请求相关
     */
    public static final String URL_ROOT = "http://192.168.4.89:8081";
    //public static String URL_ROOT_V1 = "http://openapi.ids111.com:88/v1";
    public static String URL_ROOT_V1 = "http://openapi.ids111.com:86/v1";
    // public static final String URL_ROOT_V1 = "http://openapi.ids111.com:82/v1";
    public static final String URL_GET_APP_UPDATE = URL_ROOT_V1 + "/versions/info.json";
    public static final String URL_GET_USERINFO = URL_ROOT_V1 + "/users/profile.json";
    public static final String URL_POST_USERINFO = URL_ROOT_V1 + "/users/update.json";
    public static final String URL_POST_VERIFY_CODE = URL_ROOT_V1 + "/verify/index.json";
    public static final String URL_GET_RADAR = URL_ROOT_V1 + "/radar/discover.json";
    public static final String URL_POST_LOCATION=URL_ROOT_V1+"/radar/check.json";
    public static final String URL_POST_FEEDBACK = URL_ROOT_V1 + "/feedbacks/report.json";
    public static final String URL_GET_PRODUCT = URL_ROOT_V1 + "/items/info.json";
    public static final String URL_GET_ADDRESS_LIST = URL_ROOT_V1 + "/addresses/index.json";
    public static final String URL_POST_ADD_ADDRESS = URL_ROOT_V1 + "/addresses/add.json";
    public static final String URL_POST_CONFIRM_ADDRESS = URL_ROOT_V1 + "/orders/confirm.json";
    public static final String URL_POST_DELETE_ADDRESS = URL_ROOT_V1 + "/addresses/delete.json";
    public static final String URL_POST_CREATE_ORDER = URL_ROOT_V1 + "/orders/create.json";
    public static final String URL_POST_CONSUME_CODE = URL_ROOT_V1 + "/orders/consume.json";
    public static final String URL_POST_ORDER_LIST = URL_ROOT_V1 + "/orders/index.json";
    public static final String URL_POST_ORDER_DETAIL = URL_ROOT_V1 + "/orders/detail.json";


    /**
     * 登录类型
     */
    public static final String NO_LOGIN = "no_login";
    public static final String LOGIN_TYPE_WX = "wx";
    public static final String LOGIN_TYPE_QQ = "qq";
    public static final String LOGIN_TYPE_M = "m";
    public static String LOGIN_TYOE = NO_LOGIN;

    /**
     * 上传类型
     */
    public static final int UPLOAD_ALL = 0;
    public static final int UPLOAD_NICKNAME = 1;
    public static final int UPLOAD_AVATAR = 2;
    public static final int UPLOAD_PHONE = 3;
    public static final int UPLOAD_GENDER = 4;

    /**
     * 是否信息被更新
     */
    public static boolean IS_UPDATED = false;

    /**
     * 系统消息变更广播
     */
     public static final String SYSTEM_MSG_CHANGED ="system_msg_changed";
}
