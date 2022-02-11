package com.brotherhood.o2o.chat.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.brotherhood.o2o.chat.model.IMChatBean;

import java.util.LinkedList;

/**
 * Created by Administrator on 2015/12/18 0018.
 */
public class IDSGroupChatDao extends BaseDBDao {
    private static IDSGroupChatDao instance;
    private static final int TABLE_VERSION = 1;

    private IDSGroupChatDao(Context cxt) {
        super(cxt);
    }

    public static IDSGroupChatDao getInstance(Context context) {
        if (instance == null) {
            instance = new IDSGroupChatDao(context);
        }
        return instance;
    }

    @Override
    public String getCreateTableSQL() {
        return null;// 这里不要加，每次使用聊天表都需要确认聊天表是否创建
    }

    @Override
    public void destroy() {
        instance = null;
    }

    private void checkDB(long uid) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(getTableName(uid)).append(" (");
        sb.append(ColumnItem.COL_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(ColumnItem.TA_USER_ID).append(" INTEGER, ");
        sb.append(ColumnItem.SEND_USER_ID).append(" INTEGER, ");
        sb.append(ColumnItem.GROUP_ID).append(" INTEGER, ");

        sb.append(ColumnItem.MSG_TYPE).append(" INTEGER DEFAULT 0, ");
        sb.append(ColumnItem.MSG_SUB_TYPE).append(" INTEGER DEFAULT 0, ");

        sb.append(ColumnItem.CONTENT).append(" TEXT, ");
        sb.append(ColumnItem.TIME).append(" INTEGER DEFAULT 0, ");
        sb.append(ColumnItem.READ_STATE).append(" INTEGER DEFAULT 0, ");
        sb.append(ColumnItem.SEND_STATE).append(" INTEGER DEFAULT 0, ");
        sb.append(ColumnItem.FILE_PATH).append(" TEXT, ");

        sb.append(ColumnItem.DOWNLOAD_STATE).append(" INTEGER DEFAULT -1, ");
        sb.append(ColumnItem.DURATION).append(" INTEGER DEFAULT 1, ");

        sb.append(ColumnItem.EXTRA).append(" TEXT, ");
        sb.append(ColumnItem.IS_HELLO).append(" INTEGER, ");
        sb.append(ColumnItem.MSG_ID).append(" INTEGER, ");
        sb.append(ColumnItem.TA_MSG_ID).append(" INTEGER, ");
        sb.append(ColumnItem.TABLE_VERSION).append(" INTEGER DEFAULT ").append(TABLE_VERSION);
        sb.append(")");

//        String rowIndex = uid + "_rowid_index_cmsg";
//        StringBuffer sbIndexRow = new StringBuffer();
//        sbIndexRow.append("CREATE INDEX IF NOT EXIT").append(rowIndex).append(" ON ");
//        sbIndexRow.append(getTableName(uid)).append(" (").append(ColumnItem.COL_ID).append(")");
//
//        String readStateIndex = uid + "_read_state_index";
//        StringBuffer sbIndexReadState = new StringBuffer();
//        sbIndexReadState.append("CREATE INDEX IF NOT EXIT").append(readStateIndex).append(" ON ");
//        sbIndexReadState.append(getTableName(uid)).append(" (").append(ColumnItem.READ_STATE).append(")");

        try {
            SQLiteDatabase db = getWritableDB();
//            db.beginTransaction();
            db.execSQL(sb.toString());
//            db.execSQL(sbIndexRow.toString());
//            db.execSQL(sbIndexReadState.toString());
//            db.endTransaction();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public long addMsg(IMChatBean bean) {
        checkDB(bean.receiverId);
        ContentValues v = new ContentValues();
        v.put(ColumnItem.MSG_TYPE, bean.msgType);
        v.put(ColumnItem.MSG_SUB_TYPE, bean.subType);
        v.put(ColumnItem.CONTENT, bean.content);
        v.put(ColumnItem.TIME, bean.time);

        v.put(ColumnItem.TA_USER_ID, bean.receiverId);
        v.put(ColumnItem.SEND_USER_ID, bean.sender);
        v.put(ColumnItem.MSG_ID, bean.msgId);
        v.put(ColumnItem.FILE_PATH, bean.contentFilePath);

        v.put(ColumnItem.EXTRA, bean.extra);
        v.put(ColumnItem.READ_STATE, bean.hasRead);
        v.put(ColumnItem.SEND_STATE, bean.sendStatus);
        v.put(ColumnItem.MSG_ID, bean.msgId);

        v.put(ColumnItem.DOWNLOAD_STATE, bean.downloadStatus);
        v.put(ColumnItem.DURATION, bean.duration);

        v.put(ColumnItem.TA_MSG_ID, bean.msgIdOther);
        v.put(ColumnItem.IS_HELLO, bean.isHello);
        v.put(ColumnItem.GROUP_ID, bean.groupId);

        long id = getWritableDB().insert(getTableName(bean.receiverId), null, v);
        return id;
    }

    public LinkedList<IMChatBean> addMsgs(LinkedList<IMChatBean> beans) {
        checkDB(beans.get(0).receiverId);
        SQLiteDatabase db = getWritableDB();
        db.beginTransaction();
        LinkedList linkedList = new LinkedList();
        for (IMChatBean bean : beans) {
            ContentValues v = new ContentValues();
            v.put(ColumnItem.MSG_TYPE, bean.msgType);
            v.put(ColumnItem.MSG_SUB_TYPE, bean.subType);
            v.put(ColumnItem.CONTENT, bean.content);
            v.put(ColumnItem.TIME, bean.time);

            v.put(ColumnItem.TA_USER_ID, bean.receiverId);
            v.put(ColumnItem.SEND_USER_ID, bean.sender);
            v.put(ColumnItem.MSG_ID, bean.msgId);
            v.put(ColumnItem.FILE_PATH, bean.contentFilePath);

            v.put(ColumnItem.EXTRA, bean.extra);
            v.put(ColumnItem.READ_STATE, bean.hasRead);
            v.put(ColumnItem.SEND_STATE, bean.sendStatus);
            v.put(ColumnItem.MSG_ID, bean.msgId);

            v.put(ColumnItem.DOWNLOAD_STATE, bean.downloadStatus);
            v.put(ColumnItem.DURATION, bean.duration);

            v.put(ColumnItem.TA_MSG_ID, bean.msgIdOther);
            v.put(ColumnItem.IS_HELLO, bean.isHello);
            v.put(ColumnItem.GROUP_ID, bean.groupId);

            long id = getWritableDB().insert(getTableName(bean.receiverId), null, v);
            bean.id = id;
            linkedList.add(bean);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return linkedList;
    }

    public long getLastRowId(long uid) {
        checkDB(uid);
        long rowId = 0;
        StringBuffer sb = new StringBuffer();
        sb.append("select * from ").append(getTableName(uid)).append(" order by ");
        sb.append(ColumnItem.COL_ID).append(" desc limit 1");
        Cursor c = null;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            while (c.moveToNext()) {
                rowId = c.getLong(c.getColumnIndex(ColumnItem.COL_ID));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return rowId;
    }

    public LinkedList<IMChatBean> queryLimitLastMsg(long uid, int pageSize, long startRowId) {
        checkDB(uid);
        LinkedList<IMChatBean> list = new LinkedList<>();
        StringBuffer sb = new StringBuffer();
        sb.append("select * from ").append(getTableName(uid)).append(" where ").append(ColumnItem.COL_ID);
        sb.append(" <= ").append(startRowId).append(" order by ");
        sb.append(ColumnItem.COL_ID).append(" desc limit ").append(pageSize);
        Cursor c = null;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            while (c.moveToNext()) {
                IMChatBean bean = new IMChatBean();
                bean.id = c.getLong(c.getColumnIndex(ColumnItem.COL_ID));
                bean.sender = c.getLong(c.getColumnIndex(ColumnItem.SEND_USER_ID));
                bean.receiverId = c.getLong(c.getColumnIndex(ColumnItem.TA_USER_ID));

                bean.content = c.getString(c.getColumnIndex(ColumnItem.CONTENT));
                bean.time = c.getLong(c.getColumnIndex(ColumnItem.TIME));

                int sendState = c.getInt(c.getColumnIndex(ColumnItem.SEND_STATE));
                if (sendState == IMChatBean.SendState.STATUS_SENDING.getValue()) {
                    sendState = IMChatBean.SendState.STATUS_SEND_FAILED.getValue();
                }
                bean.sendStatus = sendState;

                bean.hasRead = c.getInt(c.getColumnIndex(ColumnItem.READ_STATE)) == 0 ? false : true;

                bean.msgType = c.getInt(c.getColumnIndex(ColumnItem.MSG_TYPE));
                bean.contentFilePath = c.getString(c.getColumnIndex(ColumnItem.FILE_PATH));
                bean.isHello = c.getInt(c.getColumnIndex(ColumnItem.IS_HELLO)) == 0 ? false : true;
                bean.subType = c.getInt(c.getColumnIndex(ColumnItem.MSG_SUB_TYPE));

                bean.downloadStatus = c.getInt(c.getColumnIndex(ColumnItem.DOWNLOAD_STATE));
                bean.duration = c.getInt(c.getColumnIndex(ColumnItem.DURATION));

                bean.extra = c.getString(c.getColumnIndex(ColumnItem.EXTRA));
                bean.msgId = c.getLong(c.getColumnIndex(ColumnItem.MSG_ID));
                bean.msgIdOther = c.getLong(c.getColumnIndex(ColumnItem.TA_MSG_ID));
                list.addFirst(bean);
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

    public boolean updateMsgSendState(long uid, long msgId, IMChatBean.SendState sendState) {
        checkDB(uid);
        long rowId = -1;
        ContentValues values = new ContentValues();
        values.put(ColumnItem.SEND_STATE, sendState.getValue());
        rowId = getWritableDB().update(getTableName(uid), values, ColumnItem.MSG_ID + " = ?",
                new String[]{String.valueOf(msgId)});
        return rowId != -1;
    }

    public void updateAllMsgToRead(long uid) {
        checkDB(uid);
        ContentValues values = new ContentValues();
        values.put(ColumnItem.READ_STATE, true);
        getWritableDB().update(getTableName(uid), values, null, null);
    }

    public void updateMsgToRead(IMChatBean bean) {
        checkDB(bean.receiverId);
        ContentValues values = new ContentValues();
        values.put(ColumnItem.READ_STATE, true);
        getWritableDB().update(getTableName(bean.receiverId), values, ColumnItem.COL_ID + " = ?",
                new String[]{String.valueOf(bean.id)});
    }

    public void updateMsgDownloadState(IMChatBean bean) {
        checkDB(bean.receiverId);
        ContentValues values = new ContentValues();
        values.put(ColumnItem.DOWNLOAD_STATE, bean.downloadStatus);
        getWritableDB().update(getTableName(bean.receiverId), values, ColumnItem.COL_ID + " = ?",
                new String[]{String.valueOf(bean.id)});
    }

    public void updateMsgDuration(IMChatBean bean) {
        checkDB(bean.receiverId);
        ContentValues values = new ContentValues();
        values.put(ColumnItem.DURATION, bean.duration);
        getWritableDB().update(getTableName(bean.receiverId), values, ColumnItem.COL_ID + " = ?",
                new String[]{String.valueOf(bean.id)});
    }

    public void updateMsgContent(IMChatBean bean) {
        checkDB(bean.receiverId);
        ContentValues values = new ContentValues();
        values.put(ColumnItem.CONTENT, bean.content);
        getWritableDB().update(getTableName(bean.receiverId), values, ColumnItem.COL_ID + " = ?",
                new String[]{String.valueOf(bean.id)});
    }

    public void deleteTable(long gid) {
        try {
            getWritableDB().execSQL("DROP TABLE " + getTableName(gid));
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public void emptyTable(long gid) {
        try {
            getWritableDB().execSQL("DELETE FROM " + getTableName(gid));
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    private String getTableName(long uid) {
        return ColumnItem.TABLE + uid;
    }

    private class ColumnItem {
        public static final String TABLE = "group_table_";

        public static final String COL_ID = "_id";
        public static final String TA_USER_ID = "ta_user_id";
        public static final String SEND_USER_ID = "send_user_id";
        public static final String GROUP_ID = "group_id";


        public static final String MSG_TYPE = "msg_type";
        public static final String MSG_SUB_TYPE = "msg_sub_type";
        public static final String CONTENT = "content";
        public static final String TIME = "time";

        public static final String READ_STATE = "read_state";
        public static final String SEND_STATE = "send_state";
        public static final String FILE_PATH = "file_path";

        public static final String EXTRA = "extra";
        public static final String IS_HELLO = "is_hello";
        public static final String DOWNLOAD_STATE = "download_state";
        public static final String DURATION = "duration";

        public static final String MSG_ID = "msg_id";
        public static final String TA_MSG_ID = "ta_msg_id";
        public static final String TABLE_VERSION = "table_version";
    }

}
