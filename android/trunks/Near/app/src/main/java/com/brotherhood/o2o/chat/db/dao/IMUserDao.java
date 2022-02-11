package com.brotherhood.o2o.chat.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import com.brotherhood.o2o.chat.model.IMUserBean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/12/24 0024.
 */
public class IMUserDao extends BaseDBDao {

    private final static int TABLE_VERSION = 1;
    private static IMUserDao instance;

    public static IMUserDao getInstance(Context context) {
        if (instance == null) {
            instance = new IMUserDao(context);
        }
        return instance;
    }

    private IMUserDao(Context context) {
        super(context);
    }

    public void addMsg(IMUserBean bean) {
        if (bean == null) {
            return;
        }
        ContentValues v = new ContentValues();
        v.put(ColumnItem.UID, bean.userId);
        v.put(ColumnItem.NAME, bean.userName);
        v.put(ColumnItem.AVATAR, bean.avatar);
        v.put(ColumnItem.AGE, bean.age);
        v.put(ColumnItem.GENDER, bean.gender);
        v.put(ColumnItem.IS_ROBOT, bean.isRobot);
        v.put(ColumnItem.IS_FRIEND, bean.isFriend);
        v.put(ColumnItem.PLATFORM, bean.platform);
        long id = getWritableDB().replace(ColumnItem.TABLE, null, v);
    }

    public void updateUserToBeFriend(String taUid) {
        ContentValues values = new ContentValues();
        values.put(ColumnItem.IS_FRIEND, 1);
        getWritableDB().update(ColumnItem.TABLE, values, ColumnItem.UID + " = ?",
                new String[]{String.valueOf(taUid)});
    }

    public void updateUserName(String name, String uid) {
        ContentValues values = new ContentValues();
        values.put(ColumnItem.IS_FRIEND, 1);
        getWritableDB().update(ColumnItem.TABLE, values, ColumnItem.UID + " = ?",
                new String[]{String.valueOf(uid)});
    }

    public void updateUserAvatar(String icon, String uid) {
        ContentValues values = new ContentValues();
        values.put(ColumnItem.AVATAR, icon);
        getWritableDB().update(ColumnItem.TABLE, values, ColumnItem.UID + " = ?",
                new String[]{uid});
    }

    public void updateUserGender(int gender, String uid) {
        ContentValues values = new ContentValues();
        values.put(ColumnItem.GENDER, gender);
        getWritableDB().update(ColumnItem.TABLE, values, ColumnItem.UID + " = ?",
                new String[]{uid});
    }

    public void updateUserAge(int age, String uid) {
        ContentValues values = new ContentValues();
        values.put(ColumnItem.AGE, age);
        getWritableDB().update(ColumnItem.TABLE, values, ColumnItem.UID + " = ?",
                new String[]{uid});
    }

    public IMUserBean queryUser(String uid) {
        StringBuffer sb = new StringBuffer();
        IMUserBean bean = null;
        int count = 0;
        sb.append("select * from ").append(ColumnItem.TABLE).append(" where ");
        sb.append(ColumnItem.UID).append(" = ").append(uid);
        Cursor c = null;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            while (c.moveToNext()) {
                bean = new IMUserBean();
                bean.userId = c.getLong(c.getColumnIndex(ColumnItem.UID));
                bean.userName = c.getString(c.getColumnIndex(ColumnItem.NAME));
                bean.avatar = c.getString(c.getColumnIndex(ColumnItem.AVATAR));
                bean.age = c.getInt(c.getColumnIndex(ColumnItem.AGE));
                bean.gender = c.getInt(c.getColumnIndex(ColumnItem.GENDER));
                bean.isRobot = c.getLong(c.getColumnIndex(ColumnItem.IS_FRIEND)) == 1;
                bean.isFriend = c.getLong(c.getColumnIndex(ColumnItem.IS_ROBOT)) == 1;
                bean.platform = c.getLong(c.getColumnIndex(ColumnItem.PLATFORM)) == 1;
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

    public ArrayList<IMUserBean> queryUsers(String[] uids) {
        StringBuffer sb = new StringBuffer();
        ArrayList list = new ArrayList();

        String st = "{";
        for (int i = 0; i < uids.length; i++) {
            st += i == 0 ? uids[i] : ("," + uids[i]);
        }
        st += "}";

        sb.append("select * from ").append(ColumnItem.TABLE).append(" where ");
        sb.append(ColumnItem.UID).append(" in (").append(st).append(")");
        Cursor c = null;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            while (c.moveToNext()) {
                IMUserBean bean = new IMUserBean();
                bean.userId = c.getLong(c.getColumnIndex(ColumnItem.UID));
                bean.userName = c.getString(c.getColumnIndex(ColumnItem.NAME));
                bean.avatar = c.getString(c.getColumnIndex(ColumnItem.AVATAR));
                bean.age = c.getInt(c.getColumnIndex(ColumnItem.AGE));
                bean.gender = c.getInt(c.getColumnIndex(ColumnItem.GENDER));
                bean.isRobot = c.getLong(c.getColumnIndex(ColumnItem.IS_FRIEND)) == 1;
                bean.isFriend = c.getLong(c.getColumnIndex(ColumnItem.IS_ROBOT)) == 1;
                bean.platform = c.getLong(c.getColumnIndex(ColumnItem.PLATFORM)) == 1;
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

    public boolean checkIsFriend(String uid) {
        StringBuffer sb = new StringBuffer();
        IMUserBean bean = null;
        int count = 0;
        sb.append("select * from ").append(ColumnItem.TABLE).append(" where ");
        sb.append(ColumnItem.UID).append(" = ").append(uid).append(" and ");
        sb.append(ColumnItem.IS_FRIEND).append(" = ").append(1);
        Cursor c = null;
        boolean isFriend = false;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            isFriend = c.getCount() > 0 ? true : false;
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return isFriend;
    }


    public void deleteMsg(long uid) {
        getWritableDB().delete(ColumnItem.TABLE, ColumnItem.UID + " = ?", new String[]{String.valueOf(uid)});
    }

    public void deleteAllMsg() {
        getWritableDB().delete(ColumnItem.TABLE, null, null);
    }


    @Override
    public String getCreateTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(ColumnItem.TABLE).append(" (");
        sb.append(ColumnItem.COL_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(ColumnItem.UID).append(" INTEGER, ");
        sb.append(ColumnItem.NAME).append(" TEXT, ");
        sb.append(ColumnItem.AVATAR).append(" TEXT, ");

        sb.append(ColumnItem.AGE).append(" INTEGER, ");
        sb.append(ColumnItem.GENDER).append(" INTEGER , ");
        sb.append(ColumnItem.IS_ROBOT).append(" INTEGER , ");
        sb.append(ColumnItem.IS_FRIEND).append(" INTEGER , ");
        sb.append(ColumnItem.PLATFORM).append(" INTEGER , ");
        sb.append(ColumnItem.TABLE_VERSION).append(" INTEGER DEFAULT ").append(TABLE_VERSION);
        sb.append(")");
        return sb.toString();
    }

    @Override
    public void destroy() {
        instance = null;
    }

    private class ColumnItem {
        public static final String TABLE = "im_user";

        public static final String COL_ID = "_id";
        public static final String UID = "uid";
        public static final String NAME = "name";
        public static final String AVATAR = "avatar";
        public static final String AGE = "age";

        public static final String GENDER = "gender";
        public static final String IS_ROBOT = "is_robot";
        public static final String IS_FRIEND = "is_friend";
        public static final String PLATFORM = "platform";
        public static final String TABLE_VERSION = "table_version";
    }
}
