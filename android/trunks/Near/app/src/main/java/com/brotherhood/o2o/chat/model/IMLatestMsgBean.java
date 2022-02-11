package com.brotherhood.o2o.chat.model;

import com.brotherhood.o2o.chat.IMContants;
import com.skynet.library.message.MessageManager;

/**
 * Created by Administrator on 2015/12/23 0023.
 */
public class IMLatestMsgBean {

    public long taUid;
    public long senderId;
    public String content;
    public long time;
    public int msgType;
    public int subType;

    public long msgId;
    public int sendState;
    public boolean isHelloMsg;
    public String extra;
    public boolean isSticky;

    public String avatar;
    public String nickname;

    public static IMLatestMsgBean getBean(IMSystemMsgBean bean) {
        IMLatestMsgBean b = new IMLatestMsgBean();
        b.content = bean.content;
        b.time = bean.time;
        b.taUid = IMContants.SYSTEM_ID;
        b.senderId = IMContants.SYSTEM_ID;
        b.msgId = bean.msgId;
        b.isHelloMsg = false;
        b.isSticky = false;
        return b;
    }

    public static IMLatestMsgBean getBean(IMChatBean bean) {
        IMLatestMsgBean b = new IMLatestMsgBean();
        b.content = bean.content;
        b.time = bean.time;
        b.taUid = bean.receiverId;
        b.senderId = bean.sender;
        b.msgType = bean.msgType;
        b.subType = bean.subType;
        b.extra = bean.extra;
        return b;
    }

    public boolean isPrivateMsg() {
        if (subType == MessageManager.MessageEntity.ChatType.SINGLE_CHAT.getValue()
                || subType == MessageManager.MessageEntity.ChatType.FEED_ADD_FRIEND_ACCEPTED.getValue()) {
            return true;
        }
        return false;
    }


    public boolean isGroupMsg() {
        if (subType == MessageManager.MessageEntity.ChatType.GROUP_CHAT.getValue()
                || subType == MessageManager.MessageEntity.ChatType.FEED_MODIFY_CHANNEL_NAME.getValue()
                || subType == MessageManager.MessageEntity.ChatType.FEED_ENTER_GROUP.getValue()
                || subType == MessageManager.MessageEntity.ChatType.FEED_QUIT_GROUP.getValue()
                || subType == MessageManager.MessageEntity.ChatType.FEED_KICK_BY_CRREATOR.getValue()
                // 活动群
                || subType == MessageManager.MessageEntity.ChatType.FEED_GREEY_GROUP_ACTIVITY.getValue()
                || subType == MessageManager.MessageEntity.ChatType.FEED_FROM_GROUP_ACTIVITY.getValue()) {
            return true;
        }
        return false;
    }

    public boolean isSystemMsg() {
        return IMContants.SYSTEM_ID == taUid;
    }

    public boolean isRequestMsg() {
        if (subType == MessageManager.MessageEntity.ChatType.FEED_ADD_FRIEND.getValue()
                && IMContants.ACK_ID == taUid) {
            return true;
        }
        return false;
    }

    public boolean isAckMsg() {
        if (subType == MessageManager.MessageEntity.ChatType.FEED_ADD_FRIEND_ACCEPTED.getValue()) {
            return true;
        }
        return false;
    }
}
