package com.brotherhood.o2o.bean.account;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * 用户自身信息
 * Created by jl.zhang on 2015/12/8.
 */
public class Profile implements Serializable{

    @JSONField(name = "order_num")
    public int mOrderNum;

    @JSONField(name = "visit_total")
    public int mVisitTotal;

    @JSONField(name = "friend_total")
    public int mFriendTotal;

    @JSONField(name = "profile_complete")
    public int mProfileComplete;//资料完成度，完成1，没完成0

}
