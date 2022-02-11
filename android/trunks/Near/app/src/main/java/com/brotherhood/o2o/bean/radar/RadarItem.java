package com.brotherhood.o2o.bean.radar;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by jl.zhang on 2015/12/4.
 */
public class RadarItem {
    @JSONField(name = "people")
    public List<RadarPeople> mPeopleList;//周边人物

    @JSONField(name = "radius")
    public long mRadius;

    @JSONField(name = "events")
    public List<RadarEvent> mEventList;//活动

    @JSONField(name = "web_events")
    public List<RadarEvent> mWebEventList;//优惠券(h5)

}
