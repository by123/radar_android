package com.brotherhood.o2o.chat.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.brotherhood.o2o.chat.IMContants;
import com.brotherhood.o2o.chat.model.IMChatBean;
import com.brotherhood.o2o.chat.model.IMLatestMsgBean;
import com.skynet.library.message.MessageManager;

import java.util.LinkedList;

/**
 * Created by Administrator on 2015/12/23 0023.
 */
public class IMLatestMsgDao extends BaseDBDao {

    private static final int TABLE_VERSION = 1;
    private static IMLatestMsgDao instance;

    private IMLatestMsgDao(Context context) {
        super(context);
    }

    public static IMLatestMsgDao getInstance(Context context) {
        if (instance == null) {
            instance = new IMLatestMsgDao(context);
        }
        return instance;
    }

    public boolean addLatestMsg(IMLatestMsgBean bean) {
        if (bean == null) {
            return false;
        }
        SQLiteDatabase db = getWritableDB();
        db.beginTransaction();

        long unReadNum = 0;
        if (bean.isSystemMsg()) {
            unReadNum = IMSystemMsgDao.getInstance(mContext).queryAllUnReadMsgNum();
        } else if (bean.isRequestMsg()) {
            unReadNum = IMNewFriendDao.getInstance(mContext).queryAllUnReadMsgNum();
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append("select ").append(ColumnItem.MSG_COUNT).append(" from ").append(ColumnItem.TABLE).append(" where ");
            sb.append(ColumnItem.TA_USER_ID).append(" = ").append(bean.taUid);
            Cursor c = null;
            try {
                c = db.rawQuery(sb.toString(), null);
                while (c.moveToNext()) {
                    unReadNum = c.getInt(c.getColumnIndex(ColumnItem.MSG_COUNT));
                    break;
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }
        ContentValues v = new ContentValues();
        v.put(ColumnItem.MSG_TYPE, bean.msgType);
        v.put(ColumnItem.SUB_TYPE, bean.subType);
        v.put(ColumnItem.TIME, bean.time);

        v.put(ColumnItem.MSG_ID, bean.msgId);
        v.put(ColumnItem.TA_USER_ID, bean.taUid);
        v.put(ColumnItem.SENDER_USER_ID, bean.senderId);
        v.put(ColumnItem.CONTENT, bean.content);

        v.put(ColumnItem.IS_HELLO, bean.isHelloMsg);
        v.put(ColumnItem.IS_STICKY, bean.isSticky);
        v.put(ColumnItem.MSG_COUNT, IMContants.SYSTEM_ID == bean.taUid ? unReadNum : unReadNum + 1);

        v.put(ColumnItem.MSG_EXTRA, bean.extra);
        v.put(ColumnItem.SEND_STATE, bean.sendState);

        long id = db.replace(ColumnItem.TABLE, null, v);
        db.setTransactionSuccessful();
        db.endTransaction();
        return id != -1;
    }

    public void updateUnReadNumsAndInsertLastMsg(LinkedList<LinkedList> lists) {
        SQLiteDatabase db = getWritableDB();
        db.beginTransaction();
        for (LinkedList<IMChatBean> list : lists) {
            IMChatBean bean = list.getLast();
            int subType = bean.subType;
            if (subType == MessageManager.MessageEntity.ChatType.FEED_ADD_FRIEND.getValue()) {
                bean.receiverId = IMContants.ACK_ID;
            } else if (subType == MessageManager.MessageEntity.ChatType.FEED_MODIFY_CHANNEL_NAME.getValue()
                    || subType == MessageManager.MessageEntity.ChatType.FEED_ENTER_GROUP.getValue()
                    || subType == MessageManager.MessageEntity.ChatType.FEED_QUIT_GROUP.getValue()
                    || subType == MessageManager.MessageEntity.ChatType.FEED_KICK_BY_CRREATOR.getValue()
                    || subType == MessageManager.MessageEntity.ChatType.FEED_GREEY_GROUP_ACTIVITY.getValue()
                    ) {
                bean.content = "有群消息";
            } else if (subType == MessageManager.MessageEntity.ChatType.FEED_ADD_FRIEND_ACCEPTED.getValue()) {
                bean.content = "我们已经是好友了";
            }

            IMLatestMsgBean latestMsgBean = IMLatestMsgBean.getBean(bean);
            int count = list.size();
            long unReadCount = 0;
            if (latestMsgBean.taUid == IMContants.SYSTEM_ID) {
                unReadCount = IMSystemMsgDao.getInstance(mContext).queryAllUnReadMsgNum();
            } else {
                StringBuffer sb = new StringBuffer();
                sb.append("select ").append(ColumnItem.MSG_COUNT).append(" from ").append(ColumnItem.TABLE).append(" where ");
                sb.append(ColumnItem.TA_USER_ID).append(" = ").append(latestMsgBean.taUid);
                Cursor c = null;
                try {
                    c = db.rawQuery(sb.toString(), null);
                    while (c.moveToNext()) {
                        unReadCount = c.getInt(c.getColumnIndex(ColumnItem.MSG_COUNT));
                        break;
                    }
                } catch (SQLiteException e) {
                    e.printStackTrace();
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }

            long rowId = -1;
            ContentValues values = new ContentValues();
            values.put(ColumnItem.MSG_COUNT, IMContants.SYSTEM_ID == latestMsgBean.taUid ? unReadCount + count - 1 : unReadCount + count);
            getWritableDB().update(ColumnItem.TABLE, values, ColumnItem.TA_USER_ID + " = ?",
                    new String[]{String.valueOf(latestMsgBean.taUid)});


            ContentValues v = new ContentValues();
            v.put(ColumnItem.MSG_TYPE, latestMsgBean.msgType);
            v.put(ColumnItem.SUB_TYPE, latestMsgBean.subType);
            v.put(ColumnItem.TIME, latestMsgBean.time);

            v.put(ColumnItem.MSG_ID, latestMsgBean.msgId);
            v.put(ColumnItem.TA_USER_ID, latestMsgBean.taUid);
            v.put(ColumnItem.SENDER_USER_ID, latestMsgBean.senderId);
            v.put(ColumnItem.CONTENT, latestMsgBean.content);

            v.put(ColumnItem.IS_HELLO, latestMsgBean.isHelloMsg);
            v.put(ColumnItem.IS_STICKY, latestMsgBean.isSticky);
            v.put(ColumnItem.MSG_COUNT, IMContants.SYSTEM_ID == latestMsgBean.taUid ? unReadCount + count - 1 : unReadCount + count);

            v.put(ColumnItem.MSG_EXTRA, latestMsgBean.extra);
            v.put(ColumnItem.SEND_STATE, latestMsgBean.sendState);

            long id = db.replace(ColumnItem.TABLE, null, v);

        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void deleteSystemMsg(long msgId) {
        String wh = ColumnItem.MSG_ID + " = ? and " + ColumnItem.TA_USER_ID + " = ?";
        getWritableDB().delete(ColumnItem.TABLE, wh,
                new String[]{String.valueOf(msgId), String.valueOf(IMContants.SYSTEM_ID)});
    }


    public void deleteMsg(long uid) {
        getWritableDB().delete(ColumnItem.TABLE, ColumnItem.TA_USER_ID + " = ?",
                new String[]{String.valueOf(uid)});
    }

    public void deleteRequestMsg(long sendId) {
        getWritableDB().delete(ColumnItem.TABLE, ColumnItem.SENDER_USER_ID + " = ?",
                new String[]{String.valueOf(sendId)});
    }


    public boolean updateRequestMsg(String content, long time) {
        long rowId = -1;
        ContentValues values = new ContentValues();
        values.put(ColumnItem.CONTENT, content);
        values.put(ColumnItem.TIME, time);
        rowId = getWritableDB().update(ColumnItem.TABLE, values, ColumnItem.TA_USER_ID + " = ?",
                new String[]{String.valueOf(IMContants.ACK_ID)});
        return rowId != -1;
    }

    public boolean updateSystemMsg(String content, long time) {
        long rowId = -1;
        ContentValues values = new ContentValues();
        values.put(ColumnItem.CONTENT, content);
        //values.put(ColumnItem.TIME, time);
        rowId = getWritableDB().update(ColumnItem.TABLE, values, ColumnItem.TA_USER_ID + " = ?",
                new String[]{String.valueOf(IMContants.SYSTEM_ID)});
        return rowId != -1;
    }


    public void deleteMeAndTaMsg(long uid, long myUid) {
        StringBuffer sb = new StringBuffer();
        sb.append(ColumnItem.TA_USER_ID).append(" = ? and (").append(ColumnItem.SENDER_USER_ID);
        sb.append(" = ? or ").append(ColumnItem.SENDER_USER_ID).append("=? )");
        getWritableDB().delete(ColumnItem.TABLE, sb.toString(),
                new String[]{String.valueOf(uid), String.valueOf(uid), String.valueOf(myUid)});
    }

    public boolean updateSystemUnReadNum(long num) {
        long rowId = -1;
        ContentValues values = new ContentValues();
        values.put(ColumnItem.MSG_COUNT, num);
        rowId = getWritableDB().update(ColumnItem.TABLE, values, ColumnItem.TA_USER_ID + " = ?",
                new String[]{String.valueOf(IMContants.SYSTEM_ID)});
        return rowId != -1;
    }

    public boolean updateToHasRead(long uid) {
        long rowId = -1;
        ContentValues values = new ContentValues();
        values.put(ColumnItem.MSG_COUNT, 0);
        rowId = getWritableDB().update(ColumnItem.TABLE, values, ColumnItem.TA_USER_ID + " = ?",
                new String[]{String.valueOf(uid)});
        return rowId != -1;
    }

    public LinkedList<IMLatestMsgBean> queryAllLatestMsg(int limit, int page) {
        LinkedList list = new LinkedList();
        StringBuffer sb = new StringBuffer();
        sb.append("select * from ").append(ColumnItem.TABLE).append(" order by ");
        sb.append(ColumnItem.MSG_COUNT).append(" desc , ").append(ColumnItem.TIME);
        sb.append(" desc limit ").append(limit).append(" offset ").append(page);
        Cursor c = null;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            while (c.moveToNext()) {
                IMLatestMsgBean bean = new IMLatestMsgBean();
                bean.taUid = c.getLong(c.getColumnIndex(ColumnItem.TA_USER_ID));
                bean.senderId = c.getLong(c.getColumnIndex(ColumnItem.SENDER_USER_ID));
                bean.content = c.getString(c.getColumnIndex(ColumnItem.CONTENT));
                bean.time = c.getLong(c.getColumnIndex(ColumnItem.TIME));

                bean.msgId = c.getLong(c.getColumnIndex(ColumnItem.MSG_ID));

                bean.msgType = c.getInt(c.getColumnIndex(ColumnItem.MSG_TYPE));
                bean.subType = c.getInt(c.getColumnIndex(ColumnItem.SUB_TYPE));
                bean.sendState = c.getInt(c.getColumnIndex(ColumnItem.SEND_STATE));
                bean.isSticky = c.getInt(c.getColumnIndex(ColumnItem.IS_STICKY)) == 1;

                bean.isHelloMsg = c.getInt(c.getColumnIndex(ColumnItem.IS_HELLO)) == 1;
                bean.extra = c.getString(c.getColumnIndex(ColumnItem.MSG_EXTRA));

                list.add(bean);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return list;
    }

    public LinkedList<IMLatestMsgBean> queryAllLatestMsg() {
        LinkedList list = new LinkedList();
        StringBuffer sb = new StringBuffer();
        sb.append("select * from ").append(ColumnItem.TABLE).append(" order by ");
        sb.append(ColumnItem.MSG_COUNT).append(" desc ,").append(ColumnItem.TIME).append(" desc");
        Cursor c = null;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            while (c.moveToNext()) {
                IMLatestMsgBean bean = new IMLatestMsgBean();
                bean.taUid = c.getLong(c.getColumnIndex(ColumnItem.TA_USER_ID));
                bean.senderId = c.getLong(c.getColumnIndex(ColumnItem.SENDER_USER_ID));
                bean.content = c.getString(c.getColumnIndex(ColumnItem.CONTENT));
                bean.time = c.getLong(c.getColumnIndex(ColumnItem.TIME));

                bean.msgId = c.getLong(c.getColumnIndex(ColumnItem.MSG_ID));
                bean.msgType = c.getInt(c.getColumnIndex(ColumnItem.MSG_TYPE));
                bean.subType = c.getInt(c.getColumnIndex(ColumnItem.SUB_TYPE));
                bean.sendState = c.getInt(c.getColumnIndex(ColumnItem.SEND_STATE));
                bean.isSticky = c.getInt(c.getColumnIndex(ColumnItem.IS_STICKY)) == 1;

                bean.isHelloMsg = c.getInt(c.getColumnIndex(ColumnItem.IS_HELLO)) == 1;
                bean.extra = c.getString(c.getColumnIndex(ColumnItem.MSG_EXTRA));
                list.add(bean);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return list;
    }

    public long queryUnReadCount(long taUid) {
        long num = 0;
        StringBuffer sb = new StringBuffer();
        sb.append("select * from ").append(ColumnItem.TABLE).append(" where ");
        sb.append(ColumnItem.TA_USER_ID).append(" = ").append(taUid);
        Cursor c = null;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            while (c.moveToNext()) {
                num = c.getLong(c.getColumnIndex(ColumnItem.MSG_COUNT));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return num;
    }

    public boolean hasUnReadMsg() {
        boolean hasUnReadMsg = false;
        StringBuffer sb = new StringBuffer();
        sb.append("select * from ").append(ColumnItem.TABLE).append(" where ");
        sb.append(ColumnItem.MSG_COUNT).append(" > ").append(0);
        Cursor c = null;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            while (c.moveToNext()) {
                //String content = c.getString(c.getColumnIndex(ColumnItem.CONTENT));
                //Log.e("content:",content);
                hasUnReadMsg = true;
                break;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return hasUnReadMsg;
    }


    // =====================================================

    @Override
    public String getCreateTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(ColumnItem.TABLE).append(" (");
        sb.append(ColumnItem.COL_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(ColumnItem.MSG_ID).append(" INTEGER, ");
        sb.append(ColumnItem.TA_USER_ID).append(" INTEGER UNIQUE, ");
        sb.append(ColumnItem.SENDER_USER_ID).append(" INTEGER DEFAULT 0, ");

        sb.append(ColumnItem.MSG_TYPE).append(" INTEGER DEFAULT 0, ");
        sb.append(ColumnItem.SUB_TYPE).append(" INTEGER DEFAULT 0, ");
        sb.append(ColumnItem.CONTENT).append(" TEXT, ");
        sb.append(ColumnItem.TIME).append(" INTEGER DEFAULT 0, ");
        sb.append(ColumnItem.MSG_COUNT).append(" INTEGER DEFAULT 0, ");

        sb.append(ColumnItem.SEND_STATE).append(" INTEGER DEFAULT -1, ");
        sb.append(ColumnItem.MSG_EXTRA).append(" TEXT, ");

        sb.append(ColumnItem.IS_HELLO).append(" INTEGER, ");
        sb.append(ColumnItem.IS_STICKY).append(" INTEGER, ");
        sb.append(ColumnItem.TABLE_VERSION).append(" INTEGER DEFAULT ").append(TABLE_VERSION);
        sb.append(")");
        return sb.toString();
    }

    @Override
    public void destroy() {
        instance = null;
    }


    private class ColumnItem {

        public static final String TABLE = "im_latest_msg";

        public static final String COL_ID = "_id";
        public static final String MSG_ID = "msg_id";
        public static final String TA_USER_ID = "ta_user_id";
        public static final String SENDER_USER_ID = "user_id";
        public static final String MSG_TYPE = "msg_type";

        public static final String SUB_TYPE = "sub_type";
        public static final String CONTENT = "content";
        public static final String TIME = "msg_time";
        public static final String MSG_COUNT = "msg_unread_num";

        public static final String SEND_STATE = "send_state";
        public static final String MSG_EXTRA = "msg_extra";
        public static final String IS_HELLO = "is_hello";
        public static final String IS_STICKY = "is_sticky";
        public static final String TABLE_VERSION = "table_version";
    }

}
