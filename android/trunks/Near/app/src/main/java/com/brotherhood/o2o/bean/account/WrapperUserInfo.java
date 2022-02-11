package com.brotherhood.o2o.bean.account;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 用户信息包装类(方便自动解析获取自身信息和获取其他用户信息)
 * Created by jl.zhang on 2015/12/8.
 */
public class WrapperUserInfo {
    @JSONField(name = "me")
    public UserInfo mMyInfo;

    @JSONField(name = "user")
    public UserInfo mOtherInfo;

}
