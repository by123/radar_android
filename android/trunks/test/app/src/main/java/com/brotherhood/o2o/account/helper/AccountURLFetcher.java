package com.brotherhood.o2o.account.helper;

import com.brotherhood.o2o.account.model.LoginUserInfo;
import com.brotherhood.o2o.account.model.VerifyCodeInfo;
import com.brotherhood.o2o.extensions.BaseURLFetcher;
import com.brotherhood.o2o.extensions.DLOGWrapper;
import com.skynet.library.login.net.LoginCallBack;
import com.skynet.library.login.net.LoginError;
import com.skynet.library.login.net.LoginListener;
import com.skynet.library.login.net.LoginReq;

/**
 * Created by ZhengYi on 15/6/3.
 */
public class AccountURLFetcher extends BaseURLFetcher {

    /**
     * 登录
     *
     * @param account  账号
     * @param password 密码
     * @param handler  回调处理器
     */
    public static void login(String account, String password, final Callback<LoginUserInfo> handler) {
        LoginReq.login(account, password, new LoginListener() {
            @Override
            public void onSuccess(LoginCallBack.LoginCallBackInfo loginCallBackInfo) {
                LoginCallBack.LoginAccountInfo accountInfo = (LoginCallBack.LoginAccountInfo) loginCallBackInfo;
                LoginUserInfo data = new LoginUserInfo(accountInfo);
                DLOGWrapper.onUserLogin(data);
                postCallback(handler, data, null);
            }

            @Override
            public void onFail(LoginError loginError) {
                String error = loginError.msg;
                postCallback(handler, null, error);
            }
        });
    }

    /**
     * 发送注册验证码
     *
     * @param phone   手机号
     * @param handler 回调处理器
     */
    public static void sendRegistryVerifyCode(final String phone, final SimpleCallback handler) {
        sendVerifyCode(phone, LoginReq.TYPE_REGISTER, handler);
    }

    /**
     * 发送找回密码验证码
     *
     * @param phone   手机号
     * @param handler 回调处理器
     */
    public static void sendFindPasswordVerifyCode(final String phone, final SimpleCallback handler) {
        sendVerifyCode(phone, LoginReq.TYPE_GET_PASSWORD, handler);
    }

    private static void sendVerifyCode(final String phone, final int type, final SimpleCallback handler) {
        LoginReq.getVerifyCode(type, phone, new LoginListener() {
            @Override
            public void onSuccess(LoginCallBack.LoginCallBackInfo loginCallBackInfo) {
                LoginCallBack.LoginGetVerifyCodeInfo verifyCodeInfo = (LoginCallBack.LoginGetVerifyCodeInfo) loginCallBackInfo;
                boolean isSuccess = verifyCodeInfo.success;
                if (isSuccess)
                    postCallback(handler, true, null);
                else
                    postCallback(handler, false, "发送验证码失败");
            }

            @Override
            public void onFail(LoginError loginError) {
                postCallback(handler, false, loginError.msg);
            }
        });
    }

    /**
     * 注册
     *
     * @param phone    手机号码
     * @param code     验证码
     * @param password 密码
     * @param handler  回调处理器
     */
    public static void registry(String phone, String code, String password, final SimpleCallback handler) {
        LoginReq.registerPhone(phone, password, code, new LoginListener() {
            @Override
            public void onSuccess(LoginCallBack.LoginCallBackInfo loginCallBackInfo) {
                LoginCallBack.LoginAccountInfo accountInfo = (LoginCallBack.LoginAccountInfo) loginCallBackInfo;
                LoginUserInfo userInfo = new LoginUserInfo(accountInfo);
                DLOGWrapper.onUserRegistry(userInfo);
                postCallback(handler, true, null);
            }

            @Override
            public void onFail(LoginError loginError) {
                String error = loginError.msg;
                postCallback(handler, false, error);
            }
        });
    }

    /**
     * 通过手机验证码重设密码
     *
     * @param phone       手机号码
     * @param code        验证码
     * @param newPassword 新密码
     * @param handler     回调处理器
     */
    public static void resetPassword(String phone, String code, String newPassword, final SimpleCallback handler) {
        LoginReq.updatePasswordWithVerifyCode(phone, newPassword, code, new LoginListener() {
            @Override
            public void onSuccess(LoginCallBack.LoginCallBackInfo loginCallBackInfo) {
                postCallback(handler, true, null);
            }

            @Override
            public void onFail(LoginError loginError) {
                postCallback(handler, false, loginError.msg);
            }
        });
    }

    public static void sendLoginOrRegistryVerifyCode(String phone, final Callback<VerifyCodeInfo> handler) {
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
                } else {
                    postCallback(handler, null, loginError.msg);
                }
            }
        });
    }
}
