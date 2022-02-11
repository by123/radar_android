package com.brotherhood.o2o.bean.nearby;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by jl.zhang on 2015/12/29.
 */
public class FoodComment {

    @JSONField(name = "user")
    public User user;

    @JSONField(name = "text")
    public String mContent;

    @JSONField(name = "photo")
    public String mCommentImage;

    @JSONField(name = "createdAT")
    public long mCommentTime;

    @JSONField(name = "from")
    public String from;

    @JSONField(name = "rating")
    public String rating;

    public class User {
        @JSONField(name = "name")
        public String name;

        @JSONField(name = "avatar")
        public String avatar;
    }
}
