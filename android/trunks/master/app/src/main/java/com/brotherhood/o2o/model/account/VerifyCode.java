package com.brotherhood.o2o.model.account;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by jl.zhang on 2015/12/3.
 */
public class VerifyCode {

    @JSONField(name = "code")
    public String mCode;

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }
}
