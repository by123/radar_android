package com.brotherhood.o2o.chat.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import com.brotherhood.o2o.chat.model.IMApplyInfoBean;

import java.util.LinkedList;

/**
 * Created by Administrator on 2015/12/24 0024.
 */
public class IMNewFriendDao extends BaseDBDao {

    private static final int TABLE_VERSION = 1;
    private static IMNewFriendDao instance;

    public static IMNewFriendDao getInstance(Context context) {
        if (instance == null) {
            instance = new IMNewFriendDao(context);
        }
        return instance;
    }

    private IMNewFriendDao(Context context) {
        super(context);
    }

    public void add(IMApplyInfoBean bean) {
        ContentValues v = new ContentValues();
        v.put(ColumnItem.CONTENT, bean.msgContents);
        v.put(ColumnItem.TIME, bean.time);
        v.put(ColumnItem.TA_UID, bean.taUid);
        v.put(ColumnItem.SEND_UID, bean.sendId);
        v.put(ColumnItem.HAS_READ, bean.hasRead);
        v.put(ColumnItem.HAS_ACK, bean.isAck);
        long id = getWritableDB().replace(ColumnItem.TABLE, null, v);
    }

    public int queryAllUnReadNum() {
        int count = 0;
        StringBuffer sb = new StringBuffer();
        sb.append("select * from ").append(ColumnItem.TABLE).append(" where ");
        sb.append(ColumnItem.HAS_READ).append(" = ").append(0);
        Cursor c = null;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            count = c.getCount();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return count;
    }

    public int queryAllUnAckNum() {
        int count = 0;
        StringBuffer sb = new StringBuffer();
        sb.append("select * from ").append(ColumnItem.TABLE).append(" where ");
        sb.append(ColumnItem.HAS_ACK).append(" = ").append(0);
        Cursor c = null;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            count = c.getCount();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return count;
    }

    public LinkedList<IMApplyInfoBean> queryAllApplyInfo() {
        LinkedList list = new LinkedList();
        StringBuffer sb = new StringBuffer();
        sb.append("select * from ").append(ColumnItem.TABLE).append(" order by ");
        sb.append(ColumnItem.HAS_ACK).append(" asc , ").append(ColumnItem.TIME).append(" desc");
        Cursor c = null;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            while (c.moveToNext()) {
                IMApplyInfoBean bean = new IMApplyInfoBean();
                bean.sendId = c.getLong(c.getColumnIndex(ColumnItem.SEND_UID));
                bean.taUid = c.getLong(c.getColumnIndex(ColumnItem.TA_UID));
                bean.isAck = c.getInt(c.getColumnIndex(ColumnItem.HAS_ACK)) == 1;
                bean.time = c.getLong(c.getColumnIndex(ColumnItem.TIME));
                bean.msgContents = c.getString(c.getColumnIndex(ColumnItem.CONTENT));
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


    public IMApplyInfoBean queryLatestApplyInfo() {
        IMApplyInfoBean bean = null;
        StringBuffer sb = new StringBuffer();
        int count = 0;
        sb.append("select * from ").append(ColumnItem.TABLE).append(" order by ");
        sb.append(ColumnItem.HAS_ACK).append(" asc ").append(ColumnItem.TIME).append(" desc");
        Cursor c = null;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            while (c.moveToNext()) {
                bean = new IMApplyInfoBean();
                bean.sendId = c.getLong(c.getColumnIndex(ColumnItem.SEND_UID));
                bean.taUid = c.getLong(c.getColumnIndex(ColumnItem.TA_UID));
                bean.isAck = c.getInt(c.getColumnIndex(ColumnItem.HAS_ACK)) == 1;
                bean.time = c.getLong(c.getColumnIndex(ColumnItem.TIME));
                bean.msgContents = c.getString(c.getColumnIndex(ColumnItem.CONTENT));
                break;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return bean;
    }


    public boolean isAckUserRequest(long uid) {
        int count = 0;
        StringBuffer sb = new StringBuffer();
        sb.append("select * from ").append(ColumnItem.TABLE).append(" where ");
        sb.append(ColumnItem.TA_UID).append(" = ").append(uid);
        Cursor c = null;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            while (c.moveToNext()) {
                int v = c.getInt(c.getColumnIndex(ColumnItem.HAS_ACK));
                if (v >= 1) {
                    return true;
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return false;
    }

    public void updateToAck(long userId) {
        ContentValues values = new ContentValues();
        values.put(ColumnItem.HAS_ACK, 1);
        values.put(ColumnItem.HAS_READ, 1);
        getWritableDB().update(ColumnItem.TABLE, values, ColumnItem.TA_UID + " = ? ",
                new String[]{String.valueOf(userId)});
    }

    public void updateAllToAck(long userId) {
        ContentValues values = new ContentValues();
        values.put(ColumnItem.HAS_ACK, 1);
        values.put(ColumnItem.HAS_READ, 1);
        getWritableDB().update(ColumnItem.TABLE, values, null,
                null);
    }

    public void updateAllToHasRead() {
        ContentValues values = new ContentValues();
        values.put(ColumnItem.HAS_READ, 1);
        getWritableDB().update(ColumnItem.TABLE, values, null,
                null);
    }

    public void updateAllToHasRead(long userId) {
        ContentValues values = new ContentValues();
        values.put(ColumnItem.HAS_ACK, 1);
        values.put(ColumnItem.HAS_READ, 1);
        getWritableDB().update(ColumnItem.TABLE, values, ColumnItem.TA_UID + " = ? ",
                new String[]{String.valueOf(userId)});
    }

    public void deleteApplyInfo(long uid) {
        getWritableDB().delete(ColumnItem.TABLE, ColumnItem.TA_UID + " = ?", new String[]{String.valueOf(uid)});

    }

    public void deleteAllApplyInfo() {
        getWritableDB().delete(ColumnItem.TABLE, null, null);

    }

    @Override
    public String getCreateTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(ColumnItem.TABLE).append(" (");
        sb.append(ColumnItem.COL_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(ColumnItem.TA_UID).append(" INTEGER, ");
        sb.append(ColumnItem.SEND_UID).append(" INTEGER UNIQUE, ");
        sb.append(ColumnItem.HAS_ACK).append(" INTEGER, ");

        sb.append(ColumnItem.CONTENT).append(" TEXT, ");
        sb.append(ColumnItem.TIME).append(" INTEGER, ");
        sb.append(ColumnItem.HAS_READ).append(" INTEGER ");
        sb.append(ColumnItem.TABLE_VERSION).append(" INTEGER DEFAULT ").append(TABLE_VERSION);
        sb.append(")");
        return sb.toString();
    }

    @Override
    public void destroy() {
        instance = null;
    }

    public long queryAllUnReadMsgNum() {
        StringBuffer sb = new StringBuffer();
        int count = 0;
        sb.append("select * from ").append(ColumnItem.TABLE).append(" where ");
        sb.append(ColumnItem.HAS_ACK).append(" = ").append(0);
        Cursor c = null;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            count = c.getCount();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return count;
    }


    private class ColumnItem {

        public static final String TABLE = "im_new_friend";

        public static final String COL_ID = "_id";
        public static final String SEND_UID = "send_id";
        public static final String TA_UID = "ta_id";

        // 是否已经同意
        public static final String HAS_ACK = "has_ack";
        // 是否已读该消息
        public static final String HAS_READ = "has_read";

        public static final String CONTENT = "content";
        public static final String TIME = "time";
        public static final String TABLE_VERSION = "table_verison";
    }

}
