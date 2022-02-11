package com.brotherhood.o2o.component;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.brotherhood.o2o.ui.activity.MainActivity;
import com.brotherhood.o2o.application.MyApplication;
import com.brotherhood.o2o.ui.activity.LoginActivity;
import com.brotherhood.o2o.account.helper.AccountURLFetcher;
import com.brotherhood.o2o.model.account.LoginUserInfo;
import com.brotherhood.o2o.model.account.UserInfo;
import com.brotherhood.o2o.chat.ChatCompent;
import com.brotherhood.o2o.extensions.http.HttpClient;
import com.brotherhood.o2o.utils.BaseRequestParams;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.utils.CacheUtils;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.utils.ContextUtils;
import com.brotherhood.o2o.utils.Utils;
import com.skynet.library.login.net.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLDecoder;

/**
 * Created by ZhengYi on 15/6/4.
 */
public class AccountComponent {
    public static final String ACTION_USER_LOGIN = "ACTION.ACCOUNT.USER_LOGIN";
    public static final String ACTION_USER_NO_LOGIN = "ACTION.ACCOUNT.USER_NO_LOGIN";
    public static final String ACTION_USER_LOGOUT = "ACTION.ACCOUNT.USER_LOGOUT";
    public static final String ACTION_USERINFO_UPDATE = "ACTION.ACCOUNT.USER_INFO_UPDATE";


    private static AccountComponent sInstance;
    private LoginUserInfo mLoginUserInfo;
    private UserInfo mUserInfo;
    private Uri mHeadUri;

    private AccountComponent() {
    }

    public static AccountComponent shareComponent() {
        if (sInstance == null) {
            sInstance = new AccountComponent();
        }
        return sInstance;
    }

    /**
     * 显示登录页面
     *
     * @param context 上下文
     */
    public void showLoginPage(Activity context) {
//        if (LoginActivity.mBackgroundOrNil == null) {
//            View rootView = context.getWindow().getDecorView();
//
//            /**
//             * compute contentRect
//             */
//            Rect contentRect = new Rect();
//            rootView.getWindowVisibleDisplayFrame(contentRect);
//
//            boolean previewState = rootView.willNotCacheDrawing();
//            int previousColor = rootView.getDrawingCacheBackgroundColor();
//            rootView.setWillNotCacheDrawing(false);
//            rootView.setDrawingCacheBackgroundColor(0);
//            if (rootView.getDrawingCacheBackgroundColor() != 0)
//                rootView.destroyDrawingCache();
//            rootView.buildDrawingCache();
//            Bitmap cacheBitmap = Bitmap.createBitmap(rootView.getDrawingCache(), contentRect.left, contentRect.top, contentRect.width(), contentRect.height());
//            rootView.destroyDrawingCache();
//            cacheBitmap = Bitmap.createScaledBitmap(cacheBitmap, cacheBitmap.getWidth() / 8, cacheBitmap.getHeight() / 8, false);
//            ExplperHelper.getInstance().doBlurJniArray(cacheBitmap, 2, true);
//            rootView.setWillNotCacheDrawing(previewState);
//            rootView.setDrawingCacheBackgroundColor(previousColor);
//            LoginActivity.mBackgroundOrNil = cacheBitmap;
//        }
        LoginActivity.show(context);
    }


    public void autoLogin(Activity caller) {
        if (mLoginUserInfo == null) {
            AccountURLFetcher.loginWithLastData(caller);
        }
    }

    /**
     * 登出当前账号
     */
    public void logout() {
        if (mLoginUserInfo != null) {
            mLoginUserInfo = null;
            sendLogoutNotification();
            saveIsAutoLoginNextTime(false);
        }
    }

    /**
     * 获取AccessToken，未登录时返回空
     */
    public String getAccessTokenOrNil() {
        return LoginManager.getInstance().getToken();
    }

    /**
     * 获取AccessTokenSecret，未登录时返回空
     */
    public String getAccessTokenSecretOrNil() {
        return LoginManager.getInstance().getTokenSecret();
    }

    /**
     * 获取已登录用户的信息，可能为空
     */
    public LoginUserInfo getLoginUserInfoOrNil() {
        return mLoginUserInfo;
    }

    /**
     * 设置已登录用户信息
     */
    public void setLoginUserInfo(LoginUserInfo userInfo) {
        mLoginUserInfo = userInfo;
    }

    /**
     * 发送用户登录的通知
     */
    public void sendLoginNotification() {
        Intent intent = new Intent(ACTION_USER_LOGIN);
        getContext().sendBroadcast(intent);
    }

    /**
     * 发送用户未登录的通知（包含登录失败）
     */
    public void sendNoLoginNotification() {
        Intent intent = new Intent(ACTION_USER_NO_LOGIN);
        getContext().sendBroadcast(intent);
    }

    /**
     * 发送用户登出的通知
     */
    public void sendLogoutNotification() {
        Intent intent = new Intent(ACTION_USER_LOGOUT);
        getContext().sendBroadcast(intent);
    }

    /**
     * 保存头像uri
     *
     * @return
     */
    public void saveHeadUri(Uri uri) {
        mHeadUri = uri;
    }

    /**
     * 获得头像uri
     *
     * @return
     */
    public Uri getHeadUri() {
        return mHeadUri;
    }

    private Context getContext() {
        return ContextUtils.context();
    }

    /**
     * 保存下次自动登录状态
     *
     * @param value
     */
    private void saveIsAutoLoginNextTime(boolean value) {
        getContext().getSharedPreferences(null, Context.MODE_APPEND).edit().putBoolean("account.component.is_auto_login_next_time", value).commit();
    }

    /**
     * 是否自动登录
     *
     * @return
     */
    private boolean loadIsAutoLogin() {
        return getContext().getSharedPreferences(null, Context.MODE_APPEND).getBoolean("account.component.is_auto_login_next_time", true);
    }

    /**
     * 保存登录用户信息
     *
     * @param jsonStr
     */
    public void setUserInfo(String jsonStr) {
        if (Utils.isRequestValid(jsonStr)) {
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                JSONObject dataObject = jsonObject.getJSONObject("data");
                long mUid = dataObject.optLong("id");
                String mPhone = dataObject.optString("cell_phone");
                String mNickName = URLDecoder.decode(dataObject.optString("nickname"));
                String mAvatarURL = dataObject.optString("avatar");
                ByLogout.out("头像地址 ->" + mAvatarURL);
                String mLoginType = Constants.LOGIN_TYOE;
                CacheUtils.get(MyApplication.mApplication).put(Constants.PREFER_LOGIN_TYPE, mLoginType);
                int mGenger = dataObject.getInt("gender");
                this.mUserInfo = new UserInfo(mUid, mNickName, mAvatarURL, null, mPhone, mGenger, mLoginType);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(ACTION_USERINFO_UPDATE);
        MyApplication.mApplication.getApplicationContext().sendBroadcast(intent);
        ByLogout.out("用户信息已经设置");
    }

    /**
     * 保存登录用户信息
     *
     * @param userInfo
     */
    public void setUserInfo(UserInfo userInfo) {
        this.mUserInfo = userInfo;
    }

    /**
     * 获取登录用户信息
     *
     * @return
     */
    public UserInfo getmUserInfo() {
        return mUserInfo;
    }

    /**
     * 网络获取登录用户信息
     */
    public void getUserInfo() {
        BaseRequestParams params = new BaseRequestParams();
        HttpClient.getInstance().get_v2(Constants.URL_GET_USERINFO, params, new HttpClient.OnHttpListener() {
            @Override
            public void OnStart() {
            }

            @Override
            public void OnSuccess(HttpClient.RequestStatu statu, Object respondObject) {
                String jsonStr = respondObject.toString();
                AccountComponent.shareComponent().setUserInfo(jsonStr);
                ByLogout.out("获取用户信息成功->" + jsonStr);
            }

            @Override
            public void OnFail(HttpClient.RequestStatu statu, String resons) {
                ByLogout.out("获取用户信息失败");
            }
        });
    }

    /**
     * 保存聊天用户信息
     */
    public void saveUserAvatar(final long uid) {
        BaseRequestParams params = new BaseRequestParams();
        params.put("uid", uid);
        HttpClient.getInstance().get_v2(Constants.URL_GET_USERINFO, params, new HttpClient.OnHttpListener() {
            @Override
            public void OnStart() {
            }

            @Override
            public void OnSuccess(HttpClient.RequestStatu statu, Object respondObject) {
                String jsonStr = respondObject.toString();
                if (Utils.isRequestValid(jsonStr)) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonStr);
                        JSONObject dataObject = jsonObject.optJSONObject("data");
                        String avatarurl = dataObject.optString("avatar");
                        String nickname = dataObject.optString("nickname");
                        int gender=dataObject.optInt("gender");
                        ChatCompent.shareCompent(MyApplication.mApplication).updateSessionInfo(uid, nickname, avatarurl,gender);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ByLogout.out("获取用户信息成功->" + jsonStr);
            }

            @Override
            public void OnFail(HttpClient.RequestStatu statu, String resons) {
                ByLogout.out("获取用户信息失败");
            }
        });
    }

//    /**
//     * 直接下载用户头像保存到本地
//     *
//     * @param uid
//     * @param nickname
//     * @param url
//     */
//    private void dowload(final long uid, final String nickname, final String url) {
//        Context context = MyApplication.mApplication.getApplicationContext();
//        if (url == null) {
//            ChatCompent.shareCompent(MyApplication.mApplication).updateSessioAvatar(uid, nickname, url);
//            return;
//        }
//        final String path = Constants.ImageDir + File.separator + System.currentTimeMillis();
//        com.github.snowdream.android.app.DownloadManager downloadManager = new com.github.snowdream.android.app.DownloadManager(context);
//        final DownloadTask downloadTask = new DownloadTask(context);
//        downloadTask.setUrl(url);
//        downloadTask.setId(url.hashCode());
//        downloadTask.setPath(path);
//        downloadManager.start(downloadTask, new DownloadListener() {
//
//            @Override
//            public void onStart() {
//                super.onStart();
//            }
//
//            @Override
//            public void onProgressUpdate(Object[] values) {
//                super.onProgressUpdate(values);
//
//            }
//
//            @Override
//            public void onSuccess(Object o) {
//                super.onSuccess(o);
//                ByLogout.out("下载头像成功");
//                ChatCompent.shareCompent(MyApplication.mApplication).updateSessinAvatar(uid, nickname, url, path);
//            }
//
//            @Override
//            public void onError(Throwable thr) {
//                super.onError(thr);
//            }
//        });
//    }

    /**
     * 上报用户信息
     *
     * @param userInfo
     */
    public void UpLoadUserInfo(final Activity context, final UserInfo userInfo, final int type) {
        BaseRequestParams params = new BaseRequestParams();
        if (type == Constants.UPLOAD_NICKNAME || type == Constants.UPLOAD_ALL) {
            params.put("nickname", userInfo.mNickName);
        }
        if (type == Constants.UPLOAD_GENDER || type == Constants.UPLOAD_ALL) {
            params.put("gender", userInfo.mGenger);
        }
        if (type == Constants.UPLOAD_PHONE || type == Constants.UPLOAD_ALL) {
            params.put("cell_phone", userInfo.mPhone);
            params.put("verify_code", userInfo.mVerifyCode);
        }
        if (type == Constants.UPLOAD_AVATAR || type == Constants.UPLOAD_ALL) {
            if (userInfo.mAvatarPath != null) {
                File file = new File(userInfo.mAvatarPath);
                if (file.exists()) {
                    try {
                        params.put("avatar", file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        HttpClient.getInstance().post_v2(Constants.URL_POST_USERINFO, params, new HttpClient.OnHttpListener() {
            @Override
            public void OnStart() {
            }

            @Override
            public void OnSuccess(HttpClient.RequestStatu statu, Object respondObject) {
                Utils.showShortToast("修改成功");
                if (type == Constants.UPLOAD_ALL) {
                    MainActivity.show(context);
                    context.finish();
                } else {
                    Intent intent = new Intent(AccountComponent.ACTION_USERINFO_UPDATE);
                    if (type == Constants.UPLOAD_PHONE) {
                        intent.putExtra("phone", userInfo.mPhone);
                    }
                    else if(type == Constants.UPLOAD_NICKNAME)
                    {
                        intent.putExtra("nickname", userInfo.mNickName);
                    }
                    MyApplication.mApplication.getApplicationContext().sendBroadcast(intent);
                }
            }

            @Override
            public void OnFail(HttpClient.RequestStatu statu, String resons) {
                ByLogout.out("上报失败" + resons);

            }
        });

    }

    /**
     * 是否登录
     *
     * @return
     */
    public boolean isLogin() {
        if (getLoginUserInfoOrNil() == null) {
            return false;
        }
        return true;
    }
}
