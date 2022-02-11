package com.brotherhood.o2o.bean.nearby;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by jl.zhang on 2015/12/29.
 */
public class FoodTime {

    @JSONField(name = "days")
    public String mDate;

    @JSONField(name = "open")
    public String mWorkTime;

    @JSONField(name = "isOpenNow")
    public String isOpen;
}
