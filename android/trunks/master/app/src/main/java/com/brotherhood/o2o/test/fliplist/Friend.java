package com.brotherhood.o2o.test.fliplist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by by.huang on 2015/7/7.
 */
public class Friend {
    private int avatar;
    private String nickname;
    private int background;

    public Friend(int avatar, String nickname, int background) {
        this.avatar = avatar;
        this.nickname = nickname;
        this.background = background;
    }

    public int getAvatar() {
        return avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public int getBackground() {
        return background;
    }

}