package com.brotherhood.o2o.chat.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.TextUtils;

import com.brotherhood.o2o.chat.model.IMChatBean;

import java.util.HashMap;

/**
 * Created by Administrator on 2015/12/18 0018.
 */
public class IMSendMsgDao extends BaseDBDao {

    private static IMSendMsgDao instance;

    private IMSendMsgDao(Context context) {
        super(context);
    }

    public static IMSendMsgDao getInstance(Context context) {
        if (instance == null) {
            instance = new IMSendMsgDao(context);
        }
        return instance;
    }

    @Override
    public String getCreateTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(Column.TABLE).append(" (");
        sb.append(Column.ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(Column.USER_ID).append(" TEXT, ");
        sb.append(Column.MSG_ID).append(" INTEGER , ");
        sb.append(Column.IS_GOURP).append(" INTEGER ");
        sb.append(")");

        return sb.toString();
    }

    @Override
    public void destroy() {
        instance = null;
    }

    public void addMsg(IMChatBean bean) {
        ContentValues v = new ContentValues();
        v.put(Column.MSG_ID, bean.msgId);
        v.put(Column.USER_ID, bean.receiverId + "");
        v.put(Column.IS_GOURP, bean.groupId == 0 ? false : true);
        long id = getWritableDB().insert(Column.TABLE, null, v);
    }

    public Bundle getUIDFromMsgId(long msgId) {
        Bundle b = new Bundle();
        StringBuffer sb = new StringBuffer();
        sb.append("select * from ").append(Column.TABLE).append(" where ");
        sb.append(Column.MSG_ID).append(" = ").append(msgId);
        Cursor c = null;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            while (c.moveToNext()) {
                String uid = c.getString(c.getColumnIndex(Column.USER_ID));
                int isGroup = c.getInt(c.getColumnIndex(Column.IS_GOURP));
                b.putLong("uid", Long.valueOf(uid));
                b.putBoolean("isGroup", isGroup == 1);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return b;
    }


    private class Column {
        public static final String TABLE = "send_msg_table";
        public static final String ID = "_id";
        public static final String USER_ID = "user_id";
        public static final String MSG_ID = "msg_id";
        public static final String IS_GOURP = "is_group";
    }

}
