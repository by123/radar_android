package com.brotherhood.o2o.bean.nearby;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;

/**
 * Created by jl.zhang on 2015/12/29.
 */
public class FoodPhoto {

    @JSONField(name = "count")
    public String mPhotoCount;

    @JSONField(name = "photos")
    public ArrayList<String> mPhotoList;

}
