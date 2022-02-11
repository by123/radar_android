package com.brotherhood.o2o.bean.nearby;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by jl.zhang on 2015/12/29.
 */
public class FoodCommentWrapper {

    @JSONField(name = "count")
    public int mCount;

    @JSONField(name = "tips")
    public List<FoodComment> mCommentList;
}
