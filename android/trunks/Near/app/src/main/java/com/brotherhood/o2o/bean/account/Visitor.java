package com.brotherhood.o2o.bean.account;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 访客信息
 */
public class Visitor {

    @JSONField(name = "uid")
    public String mUid;

    @JSONField(name = "avatar")
    public String mIcon;

    @JSONField(name = "nickname")
    public String mNickname;

    @JSONField(name = "create_time")
    public String mVisitTime;

    @JSONField(name = "gender")
    public int mGender;

    @JSONField(name = "signature")
    public String mSignature;

    @JSONField(name = "is_friend")
    public int mIsFriend;

    @JSONField(name = "birthday")
    public long mBirthday;
}
