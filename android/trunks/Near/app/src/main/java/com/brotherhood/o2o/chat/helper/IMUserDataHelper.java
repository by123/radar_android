package com.brotherhood.o2o.chat.helper;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;

import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.bean.account.UserInfo;
import com.brotherhood.o2o.bean.account.WrapperUserInfo;
import com.brotherhood.o2o.chat.IDSIMQueue;
import com.brotherhood.o2o.chat.db.dao.IMUserDao;
import com.brotherhood.o2o.chat.model.IMUserBean;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.request.GetUserInfoRequest;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/12/29 0029.
 */
public class IMUserDataHelper {
    public interface IMUserDataCallback {
        public void onResult(IMUserBean userBean);
    }

    private static IMUserDataHelper instance;

    private IMUserDataHelper() {
    }

    public static IMUserDataHelper getInstance() {
        if (instance == null) {
            instance = new IMUserDataHelper();
        }
        return instance;
    }

    public void findUser(final long uid, final IMUserDataCallback callback) {
        // cache

        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMUserBean localUser = IMUserDao.getInstance(NearApplication.mInstance).queryUser(String.valueOf(uid));
                if (localUser != null) {
                    onCallback(callback, localUser);
                } else {
                    GetUserInfoRequest req = GetUserInfoRequest.createGetUserInfoRequest(String.valueOf(uid), new OnResponseListener<WrapperUserInfo>() {
                        @Override
                        public void onSuccess(int code, String msg, WrapperUserInfo wrapperUserInfo, boolean cache) {
                            UserInfo userInfo = wrapperUserInfo.mOtherInfo;
                            if (userInfo == null) {
                                LogManager.e("=========other detail userinfo is null==========");
                                return;
                            }
                            IMUserBean b = IMUserBean.getBean(userInfo);
                            onCallback(callback, b);
                        }

                        @Override
                        public void onFailure(int code, String msg) {

                        }
                    });
                    req.sendRequest();
                }
            }
        });
    }

    private void onCallback(final IMUserDataCallback callback, final IMUserBean bean) {
        if (callback != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onResult(bean);
                }
            });
        }
    }


}
