package com.brotherhood.o2o.chat.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.brotherhood.o2o.chat.model.IMSystemMsgBean;

import java.util.LinkedList;

/**
 * Created by Administrator on 2015/12/23 0023.
 */
public class IMSystemMsgDao extends BaseDBDao {

    private static IMSystemMsgDao instance;

    public static IMSystemMsgDao getInstance(Context context) {
        if (instance == null) {
            instance = new IMSystemMsgDao(context);
        }
        return instance;
    }

    private IMSystemMsgDao(Context context) {
        super(context);
    }


    public long queryAllUnReadMsgNum() {
        StringBuffer sb = new StringBuffer();
        int count = 0;
        sb.append("select * from ").append(ColumnItem.TABLE).append(" where ");
        sb.append(ColumnItem.READ_STATE).append(" = ").append(0);
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

    public IMSystemMsgBean queryLatestMsg() {
        IMSystemMsgBean bean = null;
        StringBuffer sb = new StringBuffer();
        int count = 0;
        sb.append("select * from ").append(ColumnItem.TABLE).append(" order by ");
        sb.append(ColumnItem.READ_STATE).append(" asc ,").append(ColumnItem.TIME).append(" desc");
        Cursor c = null;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            while (c.moveToNext()) {
                bean = new IMSystemMsgBean();
                bean.title = c.getString(c.getColumnIndex(ColumnItem.TITLE));
                bean.content = c.getString(c.getColumnIndex(ColumnItem.CONTENT));
                bean.time = c.getLong(c.getColumnIndex(ColumnItem.TIME));
                bean.hasRead = c.getInt(c.getColumnIndex(ColumnItem.READ_STATE)) == 1;
                bean.msgId = c.getLong(c.getColumnIndex(ColumnItem.MSG_ID));
                bean._id = c.getInt(c.getColumnIndex(ColumnItem.COL_ID));
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

    public LinkedList<IMSystemMsgBean> queryMsg() {
        LinkedList list = new LinkedList();
        StringBuffer sb = new StringBuffer();
        sb.append("select * from ").append(ColumnItem.TABLE).append(" order by ");
        sb.append(ColumnItem.READ_STATE).append(" desc ,").append(ColumnItem.TIME);
        sb.append(" desc ");
        Cursor c = null;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            while (c.moveToNext()) {
                IMSystemMsgBean bean = new IMSystemMsgBean();
                bean.content = c.getString(c.getColumnIndex(ColumnItem.CONTENT));
                bean.time = c.getLong(c.getColumnIndex(ColumnItem.TIME));
                bean.msgId = c.getLong(c.getColumnIndex(ColumnItem.MSG_ID));

                bean.hasRead = c.getInt(c.getColumnIndex(ColumnItem.READ_STATE)) == 1;
                bean.title = c.getString(c.getColumnIndex(ColumnItem.TITLE));
                bean._id = c.getInt(c.getColumnIndex(ColumnItem.COL_ID));
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

    public LinkedList<IMSystemMsgBean> queryMsg(int pageSize, int page) {
        LinkedList list = new LinkedList();
        StringBuffer sb = new StringBuffer();
        sb.append("select * from ").append(ColumnItem.TABLE).append(" limit ");
        sb.append(pageSize).append(" offset ").append(page);
        Cursor c = null;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            while (c.moveToNext()) {
                IMSystemMsgBean bean = new IMSystemMsgBean();
                bean.content = c.getString(c.getColumnIndex(ColumnItem.CONTENT));
                bean.time = c.getLong(c.getColumnIndex(ColumnItem.TIME));
                bean.msgId = c.getLong(c.getColumnIndex(ColumnItem.MSG_ID));

                bean.hasRead = c.getInt(c.getColumnIndex(ColumnItem.READ_STATE)) == 1;
                bean.title = c.getString(c.getColumnIndex(ColumnItem.TITLE));
                bean._id = c.getInt(c.getColumnIndex(ColumnItem.COL_ID));
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

    // ===============================================

    public boolean addMsg(IMSystemMsgBean bean) {
        ContentValues v = new ContentValues();
        v.put(ColumnItem.CONTENT, bean.content);
        v.put(ColumnItem.TIME, bean.time);

        v.put(ColumnItem.MSG_ID, bean.msgId);
        v.put(ColumnItem.TITLE, bean.title);
        v.put(ColumnItem.READ_STATE, bean.hasRead);
        long id = getWritableDB().insert(ColumnItem.TABLE, null, v);
        return id != -1;
    }

    public void addMsgs(LinkedList<IMSystemMsgBean> beans) {
        SQLiteDatabase db = getWritableDB();
        db.beginTransaction();
        for (IMSystemMsgBean bean : beans) {
            ContentValues v = new ContentValues();
            v.put(ColumnItem.CONTENT, bean.content);
            v.put(ColumnItem.TIME, bean.time);

            v.put(ColumnItem.MSG_ID, bean.msgId);
            v.put(ColumnItem.TITLE, bean.title);
            v.put(ColumnItem.READ_STATE, bean.hasRead);
            db.insert(ColumnItem.TABLE, null, v);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void deleteMsg(long _id) {
        getWritableDB().delete(ColumnItem.TABLE, ColumnItem.COL_ID + " = ?", new String[]{String.valueOf(_id)});
    }

    public void deleteAllMsg() {
        getWritableDB().delete(ColumnItem.TABLE, null, null);
    }

    public void updateMsgToRead() {
        ContentValues values = new ContentValues();
        values.put(ColumnItem.READ_STATE, 1);
        getWritableDB().update(ColumnItem.TABLE, values, ColumnItem.READ_STATE + " = ?",
                new String[]{String.valueOf(0)});
    }

    public void updateMsgToRead(long _id) {
        ContentValues values = new ContentValues();
        values.put(ColumnItem.READ_STATE, 1);
        getWritableDB().update(ColumnItem.TABLE, values, ColumnItem.COL_ID + " = ?",
                new String[]{String.valueOf(_id)});
    }

    @Override
    public String getCreateTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(ColumnItem.TABLE).append(" (");
        sb.append(ColumnItem.COL_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(ColumnItem.MSG_ID).append(" INTEGER, ");
        sb.append(ColumnItem.TITLE).append(" TEXT, ");
        sb.append(ColumnItem.READ_STATE).append(" INTEGER, ");

        sb.append(ColumnItem.CONTENT).append(" TEXT, ");
        sb.append(ColumnItem.TIME).append(" INTEGER DEFAULT 0 ");
        sb.append(")");
        return sb.toString();
    }

    @Override
    public void destroy() {
        instance = null;
    }

    private class ColumnItem {

        public static final String TABLE = "im_system_msg";

        public static final String COL_ID = "_id";
        public static final String MSG_ID = "msg_id";
        public static final String TITLE = "title";

        public static final String CONTENT = "content";
        public static final String TIME = "time";
        public static final String READ_STATE = "read_state";
    }

}
