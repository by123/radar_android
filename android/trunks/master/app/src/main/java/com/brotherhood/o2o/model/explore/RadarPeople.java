package com.brotherhood.o2o.model.explore;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by jl.zhang on 2015/12/4.
 */
public class RadarPeople {

    //是否是虚拟用户
    @JSONField(name = "virtual")
    public int mVirtual;

    @JSONField(name = "is_friend")
    public int mIsFriend;

    //用户电话
    @JSONField(name = "cell_phone")
    public String mPhone;

    @JSONField(name = "birthday")
    public long mBirthday;

    //坐标信息
    @JSONField(name = "location")
    public LocationInfo mLocation;

    @JSONField(name = "nickname")
    public String mNickname;

    @JSONField(name = "avatar")
    public String mAvatar;

    //是否在线
    @JSONField(name = "online")
    public int mOnline;

    //用户uid
    @JSONField(name = "id")
    public long mUid;

    //实际距离
    @JSONField(name = "distance")
    public double mDistance;

    @JSONField(name = "residence")
    public String mResidence;

    //用户性别
    @JSONField(name = "gender")
    public int mGender;

    @JSONField(name = "refresh")
    public int mRefresh;

    @JSONField(name = "signature")
    public String mSignature;


}
