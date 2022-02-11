package com.brotherhood.o2o.model.account;

import com.skynet.library.login.net.LoginCallBack;

/**
 * Created by ZhengYi on 15/6/3.
 */

public class LoginUserInfo {
    public String mUid;
    public String mName;
    public String mAvatarURL;
    public String mPhone;

    public LoginUserInfo(LoginCallBack.LoginAccountInfo accountInfo) {
        mUid = accountInfo.player_id;
        mName = accountInfo.player_nickname;
        mAvatarURL = accountInfo.player_avatar_url;
    }
}
