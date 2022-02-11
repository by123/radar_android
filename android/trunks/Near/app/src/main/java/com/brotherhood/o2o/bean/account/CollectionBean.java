package com.brotherhood.o2o.bean.account;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 收藏数据
 * Created by jl.zhang on 2015/12/25.
 * "id": 17172056,
 "name": "筷子私房菜",
 "category": "川菜",
 "photo": "http://i2.s2.dpfile.com/pc/sRgC_1fepsY-ZNjJRPUVhYgi-fZFNJtAW4OFMpJEx-Z4WSg2wEFmRTPCS29UXt8vTYGVDmosZWTLal1WbWRW3A.jpg",
 "type": 2,
 "platform": 3
 */
public class CollectionBean {

    @JSONField(name = "id")
    public String mCollectId;

    @JSONField(name = "name")
    public String mCollectName;

    @JSONField(name = "photo")
    public String mCollectPhoto;

    @JSONField(name = "type")
    public int mType;//type:收藏类型：1、想玩，2、想吃，3、想看（可选，默认为1）

    @JSONField(name = "platform")
    public int mPlatform;// 3为海外版美食

}
