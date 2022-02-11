package com.brotherhood.o2o.model.explore;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by jl.zhang on 2015/12/4.
 */
public class RadarItem {
    @JSONField(name = "people")
    public List<RadarPeople> mPeopleList;

    @JSONField(name = "radius")
    public long mRadius;
}
