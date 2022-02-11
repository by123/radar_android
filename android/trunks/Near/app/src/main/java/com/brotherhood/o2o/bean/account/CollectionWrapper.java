package com.brotherhood.o2o.bean.account;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by jl.zhang on 2015/12/25.
 */
public class CollectionWrapper {
    @JSONField(name = "count")
    public int mCount;

    @JSONField(name = "collections")
    public List<CollectionBean> mCollectList;
}
