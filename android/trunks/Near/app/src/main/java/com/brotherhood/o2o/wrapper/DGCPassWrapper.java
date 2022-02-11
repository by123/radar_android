package com.brotherhood.o2o.wrapper;

import android.content.Context;

import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.manager.IDSEnvManager;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.util.DeviceUtil;
import com.bugtags.library.BugtagsOptions;
import com.skynet.library.login.net.LoginManager;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.commons.lang3.RandomStringUtils;

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
        Context context = NearApplication.mInstance.getApplicationContext();

        // init dgc pass
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
                BugtagsOptions.Builder builder = new BugtagsOptions.Builder();
                IDSEnvManager.IDSEnv env = IDSEnvManager.getInstance().getEnv();
                Context context = NearApplication.mInstance;
                int e = SERVER_ENV_OFFICIAL;
                switch (env) {
                    case DEV://开发
                        e = SERVER_ENV_DEBUG;
                        break;
                    case TEST://测试
                        e = SERVER_ENV_DEBUG;
                        break;
                    case OFFICIAL://正式
                        e = SERVER_ENV_OFFICIAL;
                        break;
                }
                return e;
            }
        };
        LoginManager.getInstance().initApi(context, consumerKey, consumerSecret, settings);
    }

    public static IWXAPI createWXAPI(Context context) {
        IWXAPI api = WXAPIFactory.createWXAPI(context, Constants.WEXIN_APP_ID, true);
        api.registerApp(Constants.WEXIN_APP_ID);
        return api;
    }

    public static void loginWithWechat(IWXAPI api) {
        LogManager.d("AAA", "startRequestWXLogin");
        SendAuth.Req req = new SendAuth.Req();
        //req.openId = Constants.WEXIN_APP_ID;
        //应用授权作用域(获取用户信息)
        req.scope = "snsapi_userinfo";
        //用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验
        req.state = RandomStringUtils.random(8);
        api.sendReq(req);
    }
}
