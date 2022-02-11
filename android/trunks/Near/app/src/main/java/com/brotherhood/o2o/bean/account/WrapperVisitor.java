package com.brotherhood.o2o.bean.account;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 访客接口包装对象
 * Created by jl.zhang on 2015/12/22.
 */
public class WrapperVisitor {

    @JSONField(name = "uid")
    public String mUid;

    @JSONField(name = "visit_total")
    public int mVisitCount;

    @JSONField(name = "visit_new")
    public int mNewVisitCount;

    @JSONField(name = "visitors")
    public List<Visitor> mVisitorList;

}
