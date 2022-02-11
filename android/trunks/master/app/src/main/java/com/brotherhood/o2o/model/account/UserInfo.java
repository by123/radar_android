package com.brotherhood.o2o.model.account;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.utils.Utils;
import com.brotherhood.o2o.extensions.fresco.ImageLoader;

/**
 * Created by by.huang on 2015/7/21.
 */
public class UserInfo {

    public long mUid;
    public String mNickName;
    public String mAvatarURL;
    public String mAvatarPath;
    public String mPhone;
    public int mGenger=-1;
    public String mLoginType;
    public String mGenderTxt;
    public String mVerifyCode;


    public UserInfo(long mUid, String mNickName, String mAvatarURL, String mAvatarPath,String mPhone, int mGenger, String mLoginType) {
        this.mUid = mUid;
        this.mNickName = mNickName;
        this.mAvatarURL = mAvatarURL;
        this.mAvatarPath = mAvatarPath;
        this.mPhone = mPhone;
        this.mGenger = mGenger;
        this.mLoginType = mLoginType;
        if (mGenger == 0) {
            mGenderTxt = com.brotherhood.o2o.utils.Utils.getString(R.string.sex_male);
        } else {
            mGenderTxt = com.brotherhood.o2o.utils.Utils.getString(R.string.sex_female);
        }
    }


}
