package com.brotherhood.o2o.manager;

import android.text.TextUtils;

import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.bean.account.UserInfo;
import com.brotherhood.o2o.cache.impl.SerializationCache;
import com.brotherhood.o2o.chat.IDSIMManager;
import com.brotherhood.o2o.message.Message;
import com.brotherhood.o2o.wrapper.NearBugtagsWrapper;
import com.skynet.library.login.net.LoginManager;

import java.io.File;

//import com.brotherhood.o2o.wrapper.DLOGWrapper;

//import com.brotherhood.o2o.wrapper.NearBugtagsWrapper;
//import com.bugtags.library.Bugtags;


/**
 * 用户管理
 */
public class AccountManager {

    private static AccountManager instance;
    private static final String USER_FILE_NAME = "user";
    private static final int USER_OUTDATE_TIME = 7 * 24 * 60 * 60 * 1000;//过期时间

    private UserInfo user;

    public synchronized static AccountManager getInstance() {
        if (instance == null) {
            instance = new AccountManager();
        }
        return instance;
    }

    private AccountManager(){

    }

    public UserInfo getUser() {
        if (user == null) {
            //从文件读取
            readUser();
        }
        return user;
    }

    public void destroy() {
        user = null;
    }

    private void saveUser(UserInfo user) {
        SerializationCache<UserInfo> cache = new SerializationCache<>(DirManager.getUserFileDir());
        try {
            cache.asyncPut(USER_FILE_NAME, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readUser() {
        File userDir = DirManager.getUserFileDir();
        File userFile = new File(userDir, USER_FILE_NAME);
        if (userFile.exists() && userFile.isFile()) {
            if (System.currentTimeMillis() - userFile.lastModified() > USER_OUTDATE_TIME) {//用户信息过期
                removeUser();
                user = null;
            } else {
                SerializationCache<UserInfo> cache = new SerializationCache<>(DirManager.getUserFileDir());
                try {
                    UserInfo userInfo = cache.get(USER_FILE_NAME);
                    user = userInfo;
                    if (user != null) {
                        LogManager.d("===========uid=========:" + user.mUid);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void removeUser() {
        SerializationCache<UserInfo> cache = new SerializationCache<>(DirManager.getUserFileDir());
        try {
            cache.asyncRemove(USER_FILE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param user 用户信息
     * @param notify 是否通知登录成功
     */
    public void setUser(UserInfo user, boolean notify) {
        if (user != null && !TextUtils.isEmpty(user.mUid)) {
            this.user = user;
            if (notify){
                NearApplication.mInstance.getMessagePump().broadcastMessage(Message.Type.USER_LOGIN_SUCCESS, user);
            }
            // 设置自定义数据
            NearBugtagsWrapper.setUserData(user);
            //存储用户
            saveUser(user);
        }
    }

    public void updateUser(UserInfo user) {
        if (user != null) {
            this.user = user;
            NearApplication.mInstance.getMessagePump().broadcastMessage(Message.Type.USER_DATA_CHANGE);
            //存储用户
            saveUser(user);
        }
    }

    public void updateUser(UserInfo user, boolean isModify) {
        if (isModify) {
            user.mProfile.mFriendTotal = this.user.mProfile.mFriendTotal;
            user.mProfile.mVisitTotal = this.user.mProfile.mVisitTotal;
        }
        updateUser(user);

    }

    /**
     * 注销
     */

    public void logout() {
        if (user == null) {
            getUser();
        }
//        DLOGWrapper.onUserLogout(user.mUid);
        LoginManager.getInstance().logout();
        IDSIMManager.getInstance().destroy();
        this.user = null;
        removeUser();
        NearApplication.mInstance.getMessagePump().broadcastMessage(Message.Type.USER_LOGOUT_SUCCESS);
    }

    /**
     * 判断是否已登录
     *
     * @return
     */

    public boolean isLogin() {
        if (user == null) {
            //获取用户
            getUser();
        }
        if (user != null) {
            if (TextUtils.isEmpty(user.mUid)) {
                AccountManager.getInstance().removeUser();
                return false;
            }
            return true;
        }
        return false;
    }


    public String getToken() {
        return LoginManager.getInstance().getToken();
    }

    public String getTokenSecret() {
        return LoginManager.getInstance().getTokenSecret();
    }

}
