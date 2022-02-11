package com.brotherhood.o2o.chat;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.brotherhood.o2o.application.MyApplication;
import com.brotherhood.o2o.chat.db.DatabaseHandler;
import com.brotherhood.o2o.chat.model.ChatListBean;
import com.brotherhood.o2o.chat.utils.ChatAPI;
import com.brotherhood.o2o.chat.utils.ChatDbHelper;
import com.skynet.library.message.MessageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by by.huang on 2015/7/14.
 */
public class ChatCompent {

    private static byte[] sync = new byte[0];
    private static ChatCompent mInstance;
    private Context mContext;
    public final static String RECEIVE_NEW_MSG = "ACTION.RECEIVE.MSG";
    public final static String EXTRA_RECEIVER = "extra_receiver";

    public ChatCompent(Context context) {
        mContext = context;
    }

    public static ChatCompent shareCompent(Context context) {
        if (mInstance == null) {
            synchronized (sync) {
                if (mInstance == null) {
                    mInstance = new ChatCompent(context);
                }
            }
        }
        return mInstance;
    }

    public void sendMsgBroadCast(long receiverID) {
        Intent intent = new Intent();
        intent.setAction(RECEIVE_NEW_MSG);
        intent.putExtra(EXTRA_RECEIVER, receiverID);
        mContext.sendBroadcast(intent);
    }

    /**
     * 查询历史消息
     *
     * @param mReceiverUid
     * @return
     */
    public ChatListBean queryChatList(long mReceiverUid) {
        ChatListBean mChatListBean = new ChatListBean();
        DatabaseHandler mDbHandler = ChatAPI.get(MyApplication.mApplication).getDbHandler();

        String[] args = new String[]{MessageManager.MessageEntity.ChatType.SINGLE_CHAT.getValue() + "",
                String.valueOf(mReceiverUid)};
        try {
            Cursor c = mDbHandler.query("SELECT * FROM " + ChatDbHelper.SessionItem.TABLE
                    + " WHERE " + ChatDbHelper.SessionItem.COL_TYPE + " = ? AND "
                    + ChatDbHelper.SessionItem.COL_TARGET + " = ?", args);
            if (c != null) {
                if (c.moveToNext()) {
                    long oldSessionId = c.getLong(c.getColumnIndex(ChatDbHelper.SessionItem.COL_ID));
                    mChatListBean.mLastContent = c.getString(c.getColumnIndex(ChatDbHelper.SessionItem.COL_MSG));
                    mChatListBean.mLastUpdateTime = c.getLong(c.getColumnIndex(ChatDbHelper.SessionItem.COL_MODIFIED_DATE));
                    mChatListBean.mTargetId = c.getLong(c.getColumnIndex(ChatDbHelper.SessionItem.COL_TARGET));
                    mChatListBean.mType = c.getInt(c.getColumnIndex(ChatDbHelper.SessionItem.COL_TYPE));
                    mChatListBean.mUnread = c.getInt(c.getColumnIndex(ChatDbHelper.SessionItem.COL_UNREAD_COUNT));
                    mChatListBean.mId = oldSessionId;
                    mChatListBean.mAvatarUrl = c.getString(c.getColumnIndex(ChatDbHelper.SessionItem.COL_AVATAR_URL));
                    mChatListBean.mNickName = c.getString(c.getColumnIndex(ChatDbHelper.SessionItem.COL_NICKNAME));
                    mChatListBean.mGender = c.getInt(c.getColumnIndex(ChatDbHelper.SessionItem.COL_GENDER));
                }
                try {
                    c.close();
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {

        }
        return mChatListBean;
    }

    /**
     * 查询历史消息
     *
     * @return
     */


    public List<ChatListBean> queryChatList() {
        List<ChatListBean> mChatListBeans = new ArrayList<ChatListBean>();
        DatabaseHandler mDbHandler = ChatAPI.get(MyApplication.mApplication).getDbHandler();
        String[] args = new String[]{MessageManager.MessageEntity.ChatType.SINGLE_CHAT.getValue() + ""};
        try {
            Cursor c = mDbHandler.query("SELECT * FROM " + ChatDbHelper.SessionItem.TABLE
                    + " WHERE " + ChatDbHelper.SessionItem.COL_TYPE + " = ?", args);
            if (c != null) {
                while (c.moveToNext()) {
                    ChatListBean mChatListBean = new ChatListBean();
                    long oldSessionId = c.getLong(c.getColumnIndex(ChatDbHelper.SessionItem.COL_ID));
                    mChatListBean.mLastContent = c.getString(c.getColumnIndex(ChatDbHelper.SessionItem.COL_MSG));
                    mChatListBean.mLastUpdateTime = c.getLong(c.getColumnIndex(ChatDbHelper.SessionItem.COL_MODIFIED_DATE));
                    mChatListBean.mTargetId = c.getLong(c.getColumnIndex(ChatDbHelper.SessionItem.COL_TARGET));
                    mChatListBean.mType = c.getInt(c.getColumnIndex(ChatDbHelper.SessionItem.COL_TYPE));
                    mChatListBean.mUnread = c.getInt(c.getColumnIndex(ChatDbHelper.SessionItem.COL_UNREAD_COUNT));
                    mChatListBean.mId = oldSessionId;
                    mChatListBean.mAvatarUrl = c.getString(c.getColumnIndex(ChatDbHelper.SessionItem.COL_AVATAR_URL));
                    mChatListBean.mNickName = c.getString(c.getColumnIndex(ChatDbHelper.SessionItem.COL_NICKNAME));
                    mChatListBean.mGender = c.getInt(c.getColumnIndex(ChatDbHelper.SessionItem.COL_GENDER));
                    mChatListBeans.add(mChatListBean);
                }
                try {
                    c.close();
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {

        }
        return mChatListBeans;
    }

    /**
     * 将消息标记为已读
     *
     * @param mReceiverUid
     */
    public void updateHasRead(long mReceiverUid) {
        DatabaseHandler mDbHandler = ChatAPI.get(MyApplication.mApplication).getDbHandler();
        String[] args = new String[]{String.valueOf(mReceiverUid)};

        String where = ChatDbHelper.SessionItem.COL_TARGET + " = ?";
        ContentValues values = new ContentValues();
        values.put(ChatDbHelper.SessionItem.COL_UNREAD_COUNT, 0);
        mDbHandler.updateAndWait(ChatDbHelper.SessionItem.TABLE, values, where, args);
    }

    /**
     * 聊天会话是否被创建
     *
     * @return
     */
    public boolean isSessionIdCreated(long mReceiverUid) {
        List<ChatListBean> datas = queryChatList();
        for (ChatListBean data : datas) {
            if (data.mTargetId == mReceiverUid) {
                return true;
            }
        }
        return false;
    }

    /**
     * 保存聊天用户头像到数据库
     *
     * @param avatarUrl
     */
    public void updateSessionInfo(long mReceiverUid, String nickname, String avatarUrl,int gender) {
        DatabaseHandler mDbHandler = ChatAPI.get(MyApplication.mApplication).getDbHandler();
        String[] args = new String[]{String.valueOf(mReceiverUid)};

        String where = ChatDbHelper.SessionItem.COL_TARGET + " = ?";
        ContentValues values = new ContentValues();
        values.put(ChatDbHelper.SessionItem.COL_AVATAR_URL, avatarUrl);
        values.put(ChatDbHelper.SessionItem.COL_NICKNAME, nickname);
        values.put(ChatDbHelper.SessionItem.COL_GENDER, gender);
        mDbHandler.updateAndWait(ChatDbHelper.SessionItem.TABLE, values, where, args);
    }

    /**
     * 删除会话列表
     */
    public void deleteSession(long mReceiverUid) {
        DatabaseHandler mDbHandler = ChatAPI.get(MyApplication.mApplication).getDbHandler();
        String[] args = new String[]{MessageManager.MessageEntity.ChatType.SINGLE_CHAT.getValue() + "",
                String.valueOf(mReceiverUid)};
        String where = ChatDbHelper.SessionItem.COL_TYPE + " = ? AND "
                + ChatDbHelper.SessionItem.COL_TARGET + " = ?";
        mDbHandler.deleteAndWait(ChatDbHelper.SessionItem.TABLE, where, args);
    }
}
