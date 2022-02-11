package com.brotherhood.o2o.request.wrapper;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.account.UserInfo;
import com.brotherhood.o2o.bean.account.VerifyCode;
import com.brotherhood.o2o.bean.account.WrapperUserInfo;
import com.brotherhood.o2o.chat.IDSIMManager;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnCommonResponseListener;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.manager.AccountManager;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.request.GetUserInfoRequest;
import com.brotherhood.o2o.ui.activity.EmailLoginActivity;
import com.brotherhood.o2o.ui.activity.MainActivity;
import com.brotherhood.o2o.ui.activity.SetPersonInfoActivity;
import com.brotherhood.o2o.ui.activity.SplashActivity;
import com.brotherhood.o2o.ui.activity.SplashLoginActivity;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.brotherhood.o2o.util.ActivityUtils;
import com.brotherhood.o2o.util.DeviceUtil;
import com.skynet.library.login.net.LoginCallBack;
import com.skynet.library.login.net.LoginError;
import com.skynet.library.login.net.LoginListener;
import com.skynet.library.login.net.LoginReq;

import org.apache.commons.lang3.RandomStringUtils;

//import com.brotherhood.o2o.wrapper.DLOGWrapper;

/**
 * 登录请求包装类
 * 对SDK中带有的登录相关方法的包装
 */
public class AccountWrapperRequest {

    private static final String TAG = "AccountWrapperRequest";
    private static GetUserInfoRequest mGetUserInfoRequest;

    /**
     * 以最后一次登录信息，作为自动登录的依据
     */
    public static void loginWithLastData(final Context context) {
        LoginReq.loginWithLastData(new LoginListener() {
            @Override
            public void onSuccess(LoginCallBack.LoginCallBackInfo loginCallBackInfo) {
                    onLoginSuccess(context, loginCallBackInfo, null);
            }

            @Override
            public void onFail(LoginError loginError) {
                if(loginError.code == 0) {
                    ColorfulToast.orange(context, context.getString(R.string.connect_network_timeout), Toast.LENGTH_SHORT);
                } else {
                    ColorfulToast.orange(context, loginError.msg, Toast.LENGTH_SHORT);
                }

               // SplashLoginActivity.show(context);
                EmailLoginActivity.show(context);
                AccountManager.getInstance().removeUser();
            }
        });
    }

    /**
     * 获取用户信息
     *
     * @param context
     */
    public static GetUserInfoRequest getUserInfo(final Context context, final String uid, final OnResponseListener<UserInfo> listener) {
        GetUserInfoRequest userInfoRequest = GetUserInfoRequest.createGetUserInfoRequest(uid, new OnResponseListener<WrapperUserInfo>() {
            @Override
            public void onSuccess(int code, String msg, WrapperUserInfo wrapperUserInfo, boolean cache) {
                if (uid == null) {
                    if (wrapperUserInfo != null && wrapperUserInfo.mMyInfo != null) {
                        if (wrapperUserInfo.mMyInfo.mProfile.mProfileComplete == 0) {//资料未完善
                            SetPersonInfoActivity.show(context);
                        } else {//主页入口*/
                            Activity activity = ActivityUtils.getScreenManager().getActivity(MainActivity.class);
                            if (activity == null){
                                MainActivity.show(context);
                            }
                            AccountManager.getInstance().setUser(wrapperUserInfo.mMyInfo, true);
                        }
                    }

                } else {
                    if (listener != null) {
                        listener.onSuccess(code, msg, wrapperUserInfo.mMyInfo, cache);
                    }
                }
            }

            @Override
            public void onFailure(int code, String msg) {//获取用户信息失败，跳转登录页
                ColorfulToast.orange(context, msg, Toast.LENGTH_SHORT);
                LogManager.w("getUserInfo", msg);
                AccountManager.getInstance().logout();
                SplashActivity.show(context);
                DeviceUtil.rebootApp(context);

                if (listener != null) {
                    listener.onFailure(code, msg);
                }
            }
        });
        userInfoRequest.sendRequest();
        return userInfoRequest;
    }





    /**
     * 登录
     *
     * @param phone      手机号码
     * @param verifyCode 验证码
     */
    public static void loginWithVerifyCode(final Context context, final String phone, String verifyCode, final OnCommonResponseListener<String> listener) {
        LoginReq.loginWithVerifyCode(phone, verifyCode, new LoginListener() {
            @Override
            public void onSuccess(LoginCallBack.LoginCallBackInfo loginCallBackInfo) {
                onLoginSuccess(context, loginCallBackInfo, listener);
            }

            @Override
            public void onFail(LoginError loginError) {
                String error = loginError.msg;
                if (listener != null) {
                    listener.onFailed(error);
                }
            }
        });
    }

    /**
     * 注册
     *
     * @param phone
     * @param verifyCode
     */
    public static void registerWithVerifyCode(final String phone, String verifyCode, final OnCommonResponseListener<String> listener) {
        String randomPassword = RandomStringUtils.random(8);
        LoginReq.registerPhone(phone, randomPassword, verifyCode, new LoginListener() {
            @Override
            public void onSuccess(LoginCallBack.LoginCallBackInfo loginCallBackInfo) {
                LoginCallBack.LoginAccountInfo accountInfo = (LoginCallBack.LoginAccountInfo) loginCallBackInfo;
                if (listener != null) {
                    listener.onSuccess(accountInfo.player_id);
                }
            }

            @Override
            public void onFail(LoginError loginError) {
                String error = loginError.msg;
                if (listener != null) {
                    listener.onFailed(error);
                }
            }
        });
    }


    private static UserInfo getLoginSuccessUserInfo(LoginCallBack.LoginCallBackInfo loginCallBackInfo) {
        LoginCallBack.LoginAccountInfo accountInfo = (LoginCallBack.LoginAccountInfo) loginCallBackInfo;
        UserInfo user = new UserInfo();
        user.mUid = accountInfo.player_id;
        user.mNickName = accountInfo.player_nickname;
        user.mIcon = accountInfo.player_avatar_url;
        user.mPhone = accountInfo.phone;
        return user;
    }

    /**
     * 微信登录
     *
     * @param code 微信授权后返回的Code
     */
    public static void loginWithWechatCallback(final Context context, String code, final OnCommonResponseListener<String> listener) {
        LoginReq.loginWeiXin(Constants.WEXIN_APP_ID, Constants.WEIXIN_APP_SECRET, code, new LoginListener() {
            @Override
            public void onSuccess(LoginCallBack.LoginCallBackInfo loginCallBackInfo) {
                onLoginSuccess(context, loginCallBackInfo, listener);
            }

            @Override
            public void onFail(LoginError loginError) {
                String error = loginError.msg;
                if (listener != null) {
                    listener.onFailed(error);
                }
            }
        });
    }

    /**
     * QQ登录
     *
     * @param callbackURL QQ登录成功后的回调URL
     */
    public static void loginWithQQCallback(final Context context, final String callbackURL, final OnCommonResponseListener<String> listener) {
        LoginReq.loginQQ(callbackURL, new LoginListener() {
            @Override
            public void onSuccess(LoginCallBack.LoginCallBackInfo loginCallBackInfo) {
                onLoginSuccess(context, loginCallBackInfo, listener);
            }

            @Override
            public void onFail(LoginError loginError) {
                String error = loginError.msg;
                if (listener != null) {
                    listener.onFailed(error) ;
                }
            }
        });
    }

    /**
     * 注册
     *
     * @param phone 手机号码
     * @param code  验证码
     */
    public static void register(final String phone, String code, final OnCommonResponseListener<String> listener) {
        String randomPassword = RandomStringUtils.random(8);
        LoginReq.registerPhone(phone, randomPassword, code, new LoginListener() {
            @Override
            public void onSuccess(LoginCallBack.LoginCallBackInfo loginCallBackInfo) {
            }

            @Override
            public void onFail(LoginError loginError) {
                String error = loginError.msg;
                if (listener != null) {
                    listener.onFailed(error);
                }
            }
        });
    }

    /**
     * 发送注册/登录验证码
     */
    public static void sendLoginOrRegistryVerifyCode(final String phoneNumber, final OnCommonResponseListener<VerifyCode> listener) {
        LoginReq.getVerifyCode(LoginReq.TYPE_REGISTER, phoneNumber, new LoginListener() {
            @Override
            public void onSuccess(LoginCallBack.LoginCallBackInfo loginCallBackInfo) {
                VerifyCode data = new VerifyCode();
                data.mType = VerifyCode.TYPE_REGISTRY;
                if (listener != null) {
                    listener.onSuccess(data);
                }
            }

            @Override
            public void onFail(LoginError loginError) {
                if (loginError.code == 201) {
                    //发送短信登录验证码
                    LoginReq.getVerifyCode(LoginReq.TYPE_LOGIN, phoneNumber, new LoginListener() {
                        @Override
                        public void onSuccess(LoginCallBack.LoginCallBackInfo loginCallBackInfo) {
                            VerifyCode data = new VerifyCode();
                            data.mType = VerifyCode.TYPE_LOGIN;
                            if (listener != null) {
                                listener.onSuccess(data);
                            }
                        }

                        @Override
                        public void onFail(LoginError loginError) {
                            if (listener != null) {
                                listener.onFailed(loginError.msg);
                            }
                        }
                    });
                } else {
                    if (listener != null) {
                        listener.onFailed(loginError.msg);
                    }
                }
            }
        });
    }

    /**
     * Email登录
     *
     * @param
     */
    public static void loginWithEmailCallback(final Context context, final String loginEmail, final String loginPassword, final OnCommonResponseListener<String> listener) {
        LoginReq.login(loginEmail, loginPassword, new LoginListener() {
            @Override
            public void onSuccess(LoginCallBack.LoginCallBackInfo loginCallBackInfo) {
                LogManager.w(TAG, "email login success");
                onLoginSuccess(context, loginCallBackInfo, listener);
                if (listener != null) {
                    listener.onSuccess("");
                }
            }

            @Override
            public void onFail(LoginError loginError) {
                String error = loginError.msg;
                int code = loginError.code;
                if (code == 701) {//账号密码错误
                    ColorfulToast.orange(context, context.getString(R.string.email_key_or_password_error), Toast.LENGTH_SHORT);
                } else if (code == -58) {//server busy
                    ColorfulToast.orange(context, loginError.msg, Toast.LENGTH_SHORT);
                } else {
                    ColorfulToast.orange(context, context.getString(R.string.connect_network_timeout), Toast.LENGTH_SHORT);
                }
                LogManager.w(TAG, ":::" + code);
                if (listener != null) {
                    listener.onFailed(error);
                }
            }
        });
    }

    /**
     * 登陆成功走这里,做有所的初始化
     *
     * @param context
     * @param loginCallBackInfo
     * @param listener
     */
    private static void onLoginSuccess(Context context, LoginCallBack.LoginCallBackInfo loginCallBackInfo,
                                       final OnCommonResponseListener<String> listener) {
        final LoginCallBack.LoginAccountInfo accountInfo = (LoginCallBack.LoginAccountInfo) loginCallBackInfo;
        UserInfo userInfo = getLoginSuccessUserInfo(loginCallBackInfo);
        AccountManager.getInstance().setUser(userInfo, false);

        mGetUserInfoRequest = getUserInfo(context, null, new OnResponseListener<UserInfo>() {
            @Override
            public void onSuccess(int code, String msg, UserInfo userInfo, boolean cache) {
                if (listener != null) {
                    listener.onSuccess(accountInfo.player_id);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (listener != null) {
                    listener.onFailed(msg);
                }
            }
        });
        IDSIMManager.getInstance().init();
    }

    /**
     * 取消获取用户信息请求
     */
    public static void cancelGetUserInfoRequest(){
        if (mGetUserInfoRequest != null){
            mGetUserInfoRequest.cancel();
            mGetUserInfoRequest = null;
        }
    }
}
