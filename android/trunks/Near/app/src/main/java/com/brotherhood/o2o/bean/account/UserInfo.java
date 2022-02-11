package com.brotherhood.o2o.bean.account;

import com.alibaba.fastjson.annotation.JSONField;
import com.brotherhood.o2o.bean.location.LocationInfo;

import java.io.Serializable;

/**
 * Created by by.huang on 2015/7/21.
 */
public class UserInfo implements Serializable{

    @JSONField(name = "id")
    public String mUid;

    @JSONField(name = "profile")
    public Profile mProfile;    //me信息

    @JSONField(name = "location")
    public LocationInfo mLocationInfo;// user信息

    @JSONField(name = "platform")
    public String mPlatform;

    @JSONField(name = "residence")
    public String mResidence;

    @JSONField(name = "is_friend")
    public int isFriend;

    @JSONField(name = "signature")
    public String mSignature;

    @JSONField(name = "nickname")
    public String mNickName;

    @JSONField(name = "avatar")
    public String mIcon;

    @JSONField(name = "cell_phone")
    public String mPhone;

    @JSONField(name = "create_time")
    public String mCreateTime;

    @JSONField(name = "update_time")
    public String mUpdateTime;

    @JSONField(name = "gender")
    public int mGenger;// 0男   1女

    @JSONField(name = "birthday")
    public String mBirthday;

    @JSONField(name = "likes")
    public int mLikeCount;//赞的数量

    @JSONField(name = "is_liked")
    public int mIsLike;// 0 我没有赞他  1 我已点赞

}
