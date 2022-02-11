package com.brotherhood.o2o.chat.model;

import com.brotherhood.o2o.ui.widget.deletelistview.DeleteBean;

/**
 * Created by by.huang on 2015/7/15.
 */
public class ChatListBean extends DeleteBean{
    /**
     * 会话id
     */
    public long mId;
    /**
     * 未读消息数量
     */
    public int mUnread = 0;
    /**
     * 最近一条消息内容
     */
    public String mLastContent;
    /**
     * 目标uid
     */
    public long mTargetId;
    /**
     * 最后一条消息时间
     */
    public long mLastUpdateTime;
    /**
     * 消息类型，文本、图片、语音、多媒体等等
     */
    public int mType;

    /**
     * 目标用户头像网络
     */
    public String mAvatarUrl;
    /**
     * 目标用户昵称
     */
    public String mNickName;
    /**
     * 目标用户性别
     */
    public int mGender;

}
