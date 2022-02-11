package com.brotherhood.o2o.manager;


import android.text.TextUtils;

import com.brotherhood.o2o.config.SharePrefConstant;

public class IDSEnvManager {

    // MARK: 渠道号

    private static final String CHANNEL_SAMSUNG = "near_samsung";
    private static final String CHANNEL_TEST = "near_test";
    private static final String CHANNEL_DEV = "near_dev";
    private static final String CHANNEL_DEFAULT = "near_unknown";

    // 正式
    private final static String URL_OFFICIAL_IM_SERVER = "message.near2.gxpan.cn";
    // private final static String URL_OFFICIAL_IM_HTTP_SERVER = "http://msg.uu.cc/";
    private final static String URL_OFFICIAL_IM_HTTP_SERVER = "http://api-gate.near2.gxpan.cn:8000/";
    private final static int PORT_OFFICIAL = 4430;
    // private final static String URL_OFFICIAL_HTTP = "http://msg.uu.cc/v1";
    private final static String URL_OFFICIAL_HTTP = "http://api-gate.near2.gxpan.cn:8000/v1";

    // 开发
    private final static String URL_DEV_IM_SERVER = "openapi.ids111.com";
    private final static String URL_DEV_IM_HTTP_SERVER = "http://openapi.ids111.com:86/";
    private final static int PORT_DEV = 4430;
    private final static String URL_DEV_HTTP = "http://openapi.ids111.com:86/v1";
    private final static String URL_DEV_NO_GATE_HOST = "http://192.168.94.56:8080/";

    // TEST
    private final static String URL_TEST_IM_SERVER = "msg.ids111.com";
    private final static String URL_TEST_IM_HTTP_SERVER = "http://openapi.ids111.com:82/";
    private final static int PORT_TEST = 4430;
    private final static String URL_TEST_HTTP = "http://openapi.ids111.com:82/v1";
    private final static String URL_TEST_NO_GATE_HOST = "http://192.168.4.89:8080/";

    public enum IDSEnv {
        DEV,//开发
        TEST,//测试
        OFFICIAL,//正式
    }

    private IDSEnv mEnv;

    private static IDSEnvManager instance = null;

    public static synchronized IDSEnvManager getInstance() {
        if (instance == null) {
            instance = new IDSEnvManager();
        }

        return instance;
    }

    private IDSEnvManager() {
        String env = DefaultSharePrefManager.getString(SharePrefConstant.IDSENV, "");
        if (TextUtils.isEmpty(env)) {
            mEnv = IDSEnv.TEST;
        } else if (TextUtils.equals(env, "TEST")) {
            mEnv = IDSEnv.TEST;
        } else if (TextUtils.equals(env, "DEV")) {
            mEnv = IDSEnv.DEV;
        } else if (TextUtils.equals(env, "OFFICIAL")) {
            mEnv = IDSEnv.OFFICIAL;
        }
    }

    public IDSEnv getEnv() {
        return mEnv;
    }

    public void setEnv(IDSEnv env) {
        this.mEnv = env;
    }

    public String getImServer() {
        String url = URL_DEV_IM_SERVER;
        switch (mEnv) {
            case TEST:
                url = URL_TEST_IM_SERVER;
                break;

            case DEV:
                url = URL_DEV_IM_SERVER;
                break;

            case OFFICIAL:
                url = URL_OFFICIAL_IM_SERVER;
                break;
        }
        return url;
    }

    public String getImServerHttp() {
        String url = URL_DEV_IM_HTTP_SERVER;
        switch (mEnv) {
            case TEST:
                url = URL_TEST_IM_HTTP_SERVER;
                break;

            case DEV:
                url = URL_DEV_IM_HTTP_SERVER;
                break;

            case OFFICIAL:
                url = URL_OFFICIAL_IM_HTTP_SERVER;
                break;
        }
        return url;
    }

    public int getPort() {
        int port = PORT_TEST;
        switch (mEnv) {
            case TEST:
                port = PORT_TEST;
                break;

            case DEV:
                port = PORT_DEV;
                break;

            case OFFICIAL:
                port = PORT_OFFICIAL;
                break;
        }

        return port;
    }

    public String getApiUrl() {
        String url = URL_DEV_HTTP;

        switch (mEnv) {
            case TEST:
                url = URL_TEST_HTTP;
                break;

            case DEV:
                url = URL_DEV_HTTP;
                break;

            case OFFICIAL:
                url = URL_OFFICIAL_HTTP;
                break;
        }

        return url;
    }

    /**
     * 获取渠道号.
     * @return  对应的渠道号.
     */
    public String getChannel() {
        switch (mEnv) {
            case TEST:
                return CHANNEL_TEST;
            case DEV:
                return CHANNEL_DEV;
            case OFFICIAL:
                return CHANNEL_SAMSUNG;
            default:
                return CHANNEL_DEFAULT;
        }
    }
}
