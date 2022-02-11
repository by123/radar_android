package com.brotherhood.o2o.chat.helper;

import android.util.Log;

import com.brotherhood.o2o.bean.account.UserInfo;
import com.brotherhood.o2o.chat.IDSIMManager;
import com.brotherhood.o2o.chat.db.service.IMDBService;
import com.brotherhood.o2o.chat.model.IMChatBean;
import com.brotherhood.o2o.chat.model.IMUserExtraBean;
import com.brotherhood.o2o.manager.AccountManager;
import com.brotherhood.o2o.util.CoderUtil;
import com.skynet.library.message.MessageManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2015/12/16 0016.
 */
public class ChatSenderHelper {

    private long mUid;
    private OnDBChangeListener mListener;
    private ChatMode mMode;
    private boolean mIsKickOut;

    public static enum ChatMode {
        MODE_PRIVATE, MODE_GROUP, MODE_GROUP_ACTIVITY
    }

    public ChatSenderHelper(long uid, ChatMode mode, boolean isKickOut, OnDBChangeListener listener) {
        mUid = uid;
        mMode = mode;
        mIsKickOut = isKickOut;
        this.mListener = listener;
    }

    public interface OnDBChangeListener {

        public void onAddChange(IMChatBean bean);
    }

    public void sendText(String obj) {
        final IMChatBean bean = createBean(obj, MessageManager.MessageEntity.MsgType.TEXT.getValue(),
                IMChatBean.SendState.STATUS_SENDING);
        IMDBService.addMsg(bean, new IMDBService.DBListener() {
            @Override
            public void onResult(Object obj) {
                long rowId = (long) obj;
                bean.id = rowId;
                addSendDB(bean);
                if (!mIsKickOut) {
                    IDSIMManager.getInstance().sendMsg(bean);
                }
                onAddChange(bean);
            }
        });
    }

    public void sendImage(String path) {
        final IMChatBean bean = createBean(path, MessageManager.MessageEntity.MsgType.IMAGE.getValue(),
                IMChatBean.SendState.STATUS_SENDING);
        IMDBService.addMsg(bean, new IMDBService.DBListener() {
            @Override
            public void onResult(Object obj) {
                long rowId = (long) obj;
                bean.id = rowId;
                addSendDB(bean);
                if (!mIsKickOut) {
                    IDSIMManager.getInstance().sendMsg(bean);
                }
                onAddChange(bean);
            }
        });
    }

    public void sendVoice(String path, long sec) {
        final IMChatBean bean = createBean(path, MessageManager.MessageEntity.MsgType.VOICE.getValue(),
                IMChatBean.SendState.STATUS_SENDING);
        bean.duration = sec;
        IMDBService.addMsg(bean, new IMDBService.DBListener() {
            @Override
            public void onResult(Object obj) {
                long rowId = (long) obj;
                bean.id = rowId;
                addSendDB(bean);
                if (!mIsKickOut) {
                    IDSIMManager.getInstance().sendMsg(bean);
                }
                onAddChange(bean);
            }
        });
    }

    public void reSendMsg(IMChatBean bean) {
        if (!mIsKickOut) {
            IDSIMManager.getInstance().sendMsg(bean);
        }
    }

    private IMChatBean createBean(String content, int msgType, IMChatBean.SendState state) {
        IMChatBean bean = new IMChatBean();
        bean.time = System.currentTimeMillis() / 1000L;
        bean.msgType = msgType;
        bean.content = content;
        bean.isHello = false;
        bean.hasRead = true;

        bean.receiverId = mUid;
        bean.sender = Long.valueOf(AccountManager.getInstance().getUser().mUid);
        bean.msgId = IDSIMManager.getInstance().getMsgId();

        switch (mMode) {
            case MODE_PRIVATE:
                bean.subType = MessageManager.MessageEntity.ChatType.SINGLE_CHAT.getValue();
                break;
            case MODE_GROUP:
                bean.subType = MessageManager.MessageEntity.ChatType.GROUP_CHAT.getValue();
                bean.groupId = mUid;
                break;
            case MODE_GROUP_ACTIVITY:
                bean.subType = MessageManager.MessageEntity.ChatType.FEED_FROM_GROUP_ACTIVITY.getValue();
                bean.groupId = mUid;
                break;
        }
        if (msgType == MessageManager.MessageEntity.MsgType.TEXT.getValue()) {
        } else if (msgType == MessageManager.MessageEntity.MsgType.VOICE.getValue()) {
            bean.contentFilePath = content;
        } else if (msgType == MessageManager.MessageEntity.MsgType.IMAGE.getValue()) {
            bean.contentFilePath = content;
        } else if (msgType == MessageManager.MessageEntity.MsgType.SHARE_LINK.getValue()) {
        }

        if (!mIsKickOut) {
            bean.sendStatus = state.getValue();
        } else {
            bean.sendStatus = IMChatBean.SendState.STATUS_SEND_FAILED.getValue();
        }

        UserInfo info = AccountManager.getInstance().getUser();
        String extra = IMUserExtraBean.createUserExtraData(info);
        try {
            bean.extra = new String(extra.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // bean.extra = extra;
        return bean;
    }

    public void setIsKickOut(boolean isKickOut) {
        this.mIsKickOut = isKickOut;
    }

    private void onAddChange(IMChatBean bean) {
        if (mListener != null) {
            mListener.onAddChange(bean);
        }
    }

    private void addSendDB(IMChatBean bean) {
        IMDBService.addSendMsg(bean);
    }

}
