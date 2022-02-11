package com.brotherhood.o2o.bean.radar;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by jl.zhang on 2015/12/4.
 */
public class RadarPeople extends RadarPoi{

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
    //@JSONField(name = "location")
    //public LocationInfo mLocation;

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

    @Override
    public boolean equals(Object o) {
        RadarPeople that = (RadarPeople) o;
        return mUid == that.mUid;
    }

    @Override
    public int hashCode() {
        return (int) (mUid ^ (mUid >>> 32));
    }

    //是否被遮盖
    public boolean isCovered = false;
    //叠加数据
    public List<RadarPeople> mCoverPeopleList;
    //是否为新增
    public boolean isAdd = true;
}
