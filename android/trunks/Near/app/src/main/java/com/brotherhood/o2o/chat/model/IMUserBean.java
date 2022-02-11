package com.brotherhood.o2o.chat.model;

import com.brotherhood.o2o.bean.account.UserInfo;
import com.brotherhood.o2o.bean.radar.RadarPeople;

/**
 * Created by Administrator on 2015/12/24 0024.
 */
public class IMUserBean {

    public long userId;
    public String userName;
    public String avatar;

    public int gender;
    public int age;
    public boolean isRobot;

    public boolean isFriend;
    public boolean platform;

    // 讨论组
    public long discussGroupId;

    public static IMUserBean getBean(UserInfo info) {
        IMUserBean bean = new IMUserBean();
        bean.gender = info.mGenger;
        bean.isFriend = info.isFriend == 1;
//        bean.platform = info.mPlatform;
        bean.userId = Long.valueOf(info.mUid);
        bean.avatar = info.mIcon;
        bean.userName = info.mNickName;
        return bean;
    }

    public static IMUserBean getBean(RadarPeople people) {
        return null;
    }

}
