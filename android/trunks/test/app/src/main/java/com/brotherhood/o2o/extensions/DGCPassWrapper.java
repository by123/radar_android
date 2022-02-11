package com.brotherhood.o2o.extensions;

import android.content.Context;

import com.brotherhood.o2o.utils.Constants;
import com.brotherhood.o2o.utils.ContextUtils;
import com.skynet.library.login.net.LoginManager;

/**
 * Created by ZhengYi on 15/6/2.
 * 乐逗通行证的包装类
 */
public class DGCPassWrapper {

    private DGCPassWrapper() {
    }

    /**
     * 初始化乐逗通行证，在主Activity调用
     */
    public static void init() {
        Context context = ContextUtils.context();
        String consumerKey = Constants.DGC_APP_KEY;
        String consumerSecret = Constants.DGC_APP_SECRET;
        LoginManager.LoginSettings settings = new LoginManager.LoginSettings() {
            @Override
            public boolean isDebugEnabled() {
                return Constants.IS_DEBUG;
            }

            @Override
            public String getChannelId() {
                return Constants.APP_CHANNEL;
            }

            @Override
            public int getServerEnvironment() {
                //noinspection ConstantConditions
                return Constants.IS_DEBUG ? SERVER_ENV_DEBUG : SERVER_ENV_OFFICIAL;
            }
        };
        LoginManager.getInstance().initApi(context, consumerKey, consumerSecret, settings);
    }
}
