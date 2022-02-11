package com.brotherhood.o2o.account.helper;

import android.app.Activity;
import android.text.TextUtils;

import com.brotherhood.o2o.component.AccountComponent;
import com.brotherhood.o2o.application.MyApplication;
import com.brotherhood.o2o.extensions.BaseURLFetcher;
import com.brotherhood.o2o.extensions.DLOGWrapper;
import com.brotherhood.o2o.extensions.http.HttpClient;
import com.brotherhood.o2o.model.account.LoginUserInfo;
import com.brotherhood.o2o.model.account.VerifyCodeInfo;
import com.brotherhood.o2o.ui.activity.LoginActivity;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.utils.CacheUtils;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.utils.Utils;
import com.skynet.library.login.net.LoginCallBack;
import com.skynet.library.login.net.LoginError;
import com.skynet.library.login.net.LoginListener;
import com.skynet.library.login.net.LoginReq;

import org.apache.commons.lang3.RandomStringUtils;

import java.lang.ref.WeakReference;

/**
 * Created by ZhengYi on 15/6/3.
 */
public class AccountURLFetcher extends BaseURLFetcher {

    /**
     * 自动登录
     *
     * @param caller
     */
    public static void loginWithLastData(Activity caller) {
        Constants.LOGIN_TYOE = CacheUtils.get(MyApplication.mApplication).getAsString(Constants.PREFER_LOGIN_TYPE);
        if (Constants.LOGIN_TYOE == null || Constants.LOGIN_TYOE == Constants.NO_LOGIN) {
            Utils.showShortToast("请重新登录");
            LoginActivity.show(caller);
            return;
        }

        LoginReq.loginWithLastData(new LoginListener() {
            @Override
            public void onSuccess(LoginCallBack.LoginCallBackInfo loginCallBackInfo) {
                LoginCallBack.LoginAccountInfo accountInfo = (LoginCallBack.LoginAccountInfo) loginCallBackInfo;
                LoginUserInfo userInfo = new LoginUserInfo(accountInfo);
                userInfo.mPhone = "默认号码";
                AccountComponent.shareComponent().setLoginUserInfo(userInfo);
                AccountComponent.shareComponent().sendLoginNotification();
            }

            @Override
            public void onFail(LoginError loginError) {
                ByLogout.out("登录失败");
            }
        });
    }

    /**
     * 登录
     *
     * @param phone      手机号码
     * @param verifyCode 验证码
     * @param handler    回调处理器
     */
    public static void login(final String phone, String verifyCode, Callback<LoginUserInfo> handler) {
        final WeakReference<Callback<LoginUserInfo>> handlerRef = new WeakReference<>(handler);
        LoginReq.loginWithVerifyCode(phone, verifyCode, new LoginListener() {
            @Override
            public void onSuccess(LoginCallBack.LoginCallBackInfo loginCallBackInfo) {
                LoginCallBack.LoginAccountInfo accountInfo = (LoginCallBack.LoginAccountInfo) loginCallBackInfo;
                LoginUserInfo data = new LoginUserInfo(accountInfo);
                DLOGWrapper.onUserLogin(data);
                postCallback(handlerRef.get(), data, null);
                AccountComponent.shareComponent().setLoginUserInfo(data);
                AccountComponent.shareComponent().sendLoginNotification();
            }

            @Override
            public void onFail(LoginError loginError) {
                String error = loginError.msg;
                postCallback(handlerRef.get(), null, error);
                AccountComponent.shareComponent().sendNoLoginNotification();
            }
        });
    }

    /**
     * 微信登录
     *
     * @param code    微信授权后返回的Code
     * @param handler 回调处理器
     */
    public static void loginWithWechatCallback(String code, final Callback<LoginUserInfo> handler) {
        LoginReq.loginWeiXin(Constants.WEXIN_APP_ID, Constants.WEIXIN_APP_SECRET, code, new LoginListener() {
            @Override
            public void onSuccess(LoginCallBack.LoginCallBackInfo loginCallBackInfo) {
                LoginCallBack.LoginAccountInfo accountInfo = (LoginCallBack.LoginAccountInfo) loginCallBackInfo;
                LoginUserInfo data = new LoginUserInfo(accountInfo);
                data.mPhone = "无号码";
                DLOGWrapper.onUserLogin(data);
                postCallback(handler, data, null);
                AccountComponent.shareComponent().setLoginUserInfo(data);
                AccountComponent.shareComponent().sendLoginNotification();
            }

            @Override
            public void onFail(LoginError loginError) {
                String error = loginError.msg;
                postCallback(handler, null, error);
                AccountComponent.shareComponent().sendNoLoginNotification();
            }
        });
    }

    /**
     * QQ登录
     *
     * @param callbackURL QQ登录成功后的回调URL
     * @param handler     回调处理器
     */
    public static void loginWithQQCallback(final String callbackURL, Callback<LoginUserInfo> handler) {
        final WeakReference<Callback<LoginUserInfo>> handlerRef = new WeakReference<>(handler);
        LoginReq.loginQQ(callbackURL, new LoginListener() {
            @Override
            public void onSuccess(LoginCallBack.LoginCallBackInfo loginCallBackInfo) {
                LoginCallBack.LoginAccountInfo accountInfo = (LoginCallBack.LoginAccountInfo) loginCallBackInfo;
                LoginUserInfo data = new LoginUserInfo(accountInfo);
                data.mPhone = "默认电话咯";
                DLOGWrapper.onUserLogin(data);
                postCallback(handlerRef.get(), data, null);
                AccountComponent.shareComponent().setLoginUserInfo(data);
                AccountComponent.shareComponent().sendLoginNotification();
            }

            @Override
            public void onFail(LoginError loginError) {
                String error = loginError.msg;
                postCallback(handlerRef.get(), null, error);
                AccountComponent.shareComponent().sendNoLoginNotification();
            }
        });
    }

    /**
     * 注册
     *
     * @param phone   手机号码
     * @param code    验证码
     * @param handler 回调处理器
     */
    public static void registry(final String phone, String code, Callback<LoginUserInfo> handler) {
        final WeakReference<Callback<LoginUserInfo>> handlerRef = new WeakReference<>(handler);
        String randomPassword = RandomStringUtils.random(8);
        LoginReq.registerPhone(phone, randomPassword, code, new LoginListener() {
            @Override
            public void onSuccess(LoginCallBack.LoginCallBackInfo loginCallBackInfo) {
                LoginCallBack.LoginAccountInfo accountInfo = (LoginCallBack.LoginAccountInfo) loginCallBackInfo;
                LoginUserInfo userInfo = new LoginUserInfo(accountInfo);
                userInfo.mPhone = phone;
                DLOGWrapper.onUserRegistry(userInfo);
                postCallback(handlerRef.get(), userInfo, null);
                AccountComponent.shareComponent().setLoginUserInfo(userInfo);
                AccountComponent.shareComponent().sendLoginNotification();
            }

            @Override
            public void onFail(LoginError loginError) {
                String error = loginError.msg;
                postCallback(handlerRef.get(), null, error);

            }
        });
    }

    /**
     * 发送注册/登录验证码
     *
     * @param phone   手机号码
     * @param handler 回调处理器
     */
    public static void sendLoginOrRegistryVerifyCode(final String phone, final Callback<VerifyCodeInfo> handler) {

        if (TextUtils.isEmpty(phone)) {
            return;
        }
        LoginReq.getVerifyCode(LoginReq.TYPE_REGISTER, phone, new LoginListener() {
            @Override
            public void onSuccess(LoginCallBack.LoginCallBackInfo loginCallBackInfo) {
                VerifyCodeInfo data = new VerifyCodeInfo();
                data.mType = VerifyCodeInfo.TYPE_REGISTRY;
                postCallback(handler, data, null);
            }

            @Override
            public void onFail(LoginError loginError) {
                if (loginError.code == 201) {
                    //发送短信登录验证码
                    LoginReq.getVerifyCode(LoginReq.TYPE_LOGIN, phone, new LoginListener() {
                        @Override
                        public void onSuccess(LoginCallBack.LoginCallBackInfo loginCallBackInfo) {
                            VerifyCodeInfo info = new VerifyCodeInfo();
                            info.mType = VerifyCodeInfo.TYPE_LOGIN;
                            postCallback(handler, info, null);
                        }

                        @Override
                        public void onFail(LoginError loginError) {
                            postCallback(handler, null, loginError.msg);
                        }
                    });
                } else {
                    postCallback(handler, null, loginError.msg);
                }
            }
        });
    }


    /**
     * 修改电话验证码
     * @param phone
     * @param listener
     */
    public static void requestVerifyCode(String phone, HttpClient.OnHttpListener listener) {
        //BaseRequestParams params = new BaseRequestParams();
        //params.put("cell_phone", phone);
        //HttpClient.getInstance().post_v2(Constants.URL_POST_VERIFY_CODE, params, listener);


    }
}
