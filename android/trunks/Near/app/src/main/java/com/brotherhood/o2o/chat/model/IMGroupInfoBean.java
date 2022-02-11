package com.brotherhood.o2o.chat.model;

import com.brotherhood.o2o.manager.AccountManager;

/**
 * Created by Administrator on 2015/12/26 0026.
 */
public class IMGroupInfoBean {

    public String gid;
    public String name;
    public String avatar;
    public int memberCount;
    public long createTime;

    public long creatorUid;

    public boolean isMute;//静音
    public boolean isSticky;

    public boolean showMemberName;
    public String memberIds;
    public boolean isKickOut;


    public boolean isCreator(){
        String myUid = AccountManager.getInstance().getUser().mUid;
        if (String.valueOf(creatorUid).equals(myUid)) {
            return true;
        }
        return false;
    }
}
