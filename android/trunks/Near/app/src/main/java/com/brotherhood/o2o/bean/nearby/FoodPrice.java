package com.brotherhood.o2o.bean.nearby;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by jl.zhang on 2015/12/29.
 */
public class FoodPrice implements Serializable{

    @JSONField(name = "tier")
    public int mLevel;

    @JSONField(name = "currency")
    public String mUnit;

    @JSONField(name = "message")
    public String mMsg;

}
