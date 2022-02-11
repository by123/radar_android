package com.brotherhood.o2o.chat.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import com.brotherhood.o2o.chat.model.IMGroupInfoBean;

/**
 * Created by Administrator on 2015/12/24 0024.
 */
public class IMGroupDao extends BaseDBDao {

    private final static int TABLE_VERSION = 1;
    private static IMGroupDao instance;

    public static IMGroupDao getInstance(Context context) {
        if (instance == null) {
            instance = new IMGroupDao(context);
        }
        return instance;
    }

    private IMGroupDao(Context context) {
        super(context);
    }

    public boolean add(IMGroupInfoBean bean) {
        ContentValues v = new ContentValues();
        v.put(ColumnItem.GID, bean.gid);

        v.put(ColumnItem.IS_STICKY, bean.isSticky);
        v.put(ColumnItem.IS_MUTE, bean.isMute);
        v.put(ColumnItem.NAME, bean.name);
        v.put(ColumnItem.IS_SHOW_NICK_NAME, bean.showMemberName);

        v.put(ColumnItem.CREATE_TIME, bean.createTime);
        v.put(ColumnItem.AVATAR, bean.avatar);

        v.put(ColumnItem.CREATEOR_ID, bean.creatorUid);
        v.put(ColumnItem.MEM_NUM, bean.memberCount);
        v.put(ColumnItem.MEMBERS, bean.memberIds);
        v.put(ColumnItem.IS_KICK_OUT, bean.isKickOut);
        long id = getWritableDB().replace(ColumnItem.TABLE, null, v);
        return id != -1;
    }


    public void add(long gid, String groupName, long createTime) {
        if (has(gid)) {
            ContentValues values = new ContentValues();
            values.put(ColumnItem.NAME, groupName);
            values.put(ColumnItem.CREATE_TIME, createTime);
            getWritableDB().update(ColumnItem.TABLE, values, ColumnItem.GID + " = ?",
                    new String[]{String.valueOf(gid)});
        } else {
            final IMGroupInfoBean groupInfoBean = new IMGroupInfoBean();
            groupInfoBean.gid = String.valueOf(gid);
            groupInfoBean.name = groupName;
            groupInfoBean.createTime = createTime;
            add(groupInfoBean);
        }
    }


    public void add(long gid, String groupName, String avatar) {
        if (has(gid)) {
            ContentValues values = new ContentValues();
            values.put(ColumnItem.NAME, groupName);
            values.put(ColumnItem.AVATAR, avatar);
            getWritableDB().update(ColumnItem.TABLE, values, ColumnItem.GID + " = ?",
                    new String[]{String.valueOf(gid)});
        } else {
            final IMGroupInfoBean groupInfoBean = new IMGroupInfoBean();
            groupInfoBean.gid = String.valueOf(gid);
            groupInfoBean.name = groupName;
            groupInfoBean.avatar = avatar;
            add(groupInfoBean);
        }
    }


    public void add(long gid, long createTime, long creatorUid) {
        if (has(gid)) {
            ContentValues values = new ContentValues();
            values.put(ColumnItem.CREATE_TIME, createTime);
            values.put(ColumnItem.CREATEOR_ID, creatorUid);
            getWritableDB().update(ColumnItem.TABLE, values, ColumnItem.GID + " = ?",
                    new String[]{String.valueOf(gid)});
        } else {
            final IMGroupInfoBean groupInfoBean = new IMGroupInfoBean();
            groupInfoBean.gid = String.valueOf(gid);
            groupInfoBean.createTime = createTime;
            groupInfoBean.creatorUid = creatorUid;
            add(groupInfoBean);
        }
    }


    public void updateMemberAndIds(long gid, int memberNum, String members) {
        ContentValues values = new ContentValues();
        values.put(ColumnItem.MEM_NUM, memberNum);
        values.put(ColumnItem.MEMBERS, members);
        getWritableDB().update(ColumnItem.TABLE, values, ColumnItem.GID + " = ?",
                new String[]{String.valueOf(gid)});
    }


    private boolean has(long gid) {
        boolean has = false;
        StringBuffer sb = new StringBuffer();
        sb.append("select * from ").append(ColumnItem.TABLE).append(" where ");
        sb.append(ColumnItem.GID).append(" = ").append(gid);
        Cursor c = null;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            while (c.moveToNext()) {
                has = true;
                break;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return has;
    }

    public void updateName(long gid, String name) {
        ContentValues values = new ContentValues();
        values.put(ColumnItem.NAME, name);
        getWritableDB().update(ColumnItem.TABLE, values, ColumnItem.GID + " = ?",
                new String[]{String.valueOf(gid)});
    }

    public void updateAvatar(long gid, String avatar) {
        ContentValues values = new ContentValues();
        values.put(ColumnItem.AVATAR, avatar);
        getWritableDB().update(ColumnItem.TABLE, values, ColumnItem.GID + " = ?",
                new String[]{String.valueOf(gid)});
    }

    public String queryAvatar(long gid) {
        String avatar = null;
        StringBuffer sb = new StringBuffer();
        sb.append("select * from ").append(ColumnItem.TABLE).append(" where ");
        sb.append(ColumnItem.GID).append(" = ").append(gid);
        Cursor c = null;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            while (c.moveToNext()) {
                avatar = c.getString(c.getColumnIndex(ColumnItem.AVATAR));
                break;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return avatar;
    }


    public void updateShowMemberName(long gid, boolean show) {
        ContentValues values = new ContentValues();
        values.put(ColumnItem.IS_SHOW_NICK_NAME, show);
        getWritableDB().update(ColumnItem.TABLE, values, ColumnItem.GID + " = ?",
                new String[]{String.valueOf(gid)});
    }

    public void deleteBean(long gid) {
        getWritableDB().delete(ColumnItem.TABLE, ColumnItem.GID + " = ?", new String[]{String.valueOf(gid)});
    }

    public IMGroupInfoBean queryGroupInfo(long gid) {
        IMGroupInfoBean bean = null;

        StringBuffer sb = new StringBuffer();
        sb.append("select * from ").append(ColumnItem.TABLE).append(" where ");
        sb.append(ColumnItem.GID).append(" = ").append(gid);
        Cursor c = null;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            while (c.moveToNext()) {
                bean = new IMGroupInfoBean();
                bean.gid = c.getString(c.getColumnIndex(ColumnItem.GID));
                bean.creatorUid = c.getLong(c.getColumnIndex(ColumnItem.CREATEOR_ID));
                bean.memberCount = c.getInt(c.getColumnIndex(ColumnItem.MEM_NUM));
                bean.memberIds = c.getString(c.getColumnIndex(ColumnItem.MEMBERS));
                bean.createTime = c.getLong(c.getColumnIndex(ColumnItem.CREATE_TIME));

                bean.avatar = c.getString(c.getColumnIndex(ColumnItem.AVATAR));
                bean.name = c.getString(c.getColumnIndex(ColumnItem.NAME));
                bean.isMute = c.getInt(c.getColumnIndex(ColumnItem.IS_MUTE)) == 1;
                bean.isSticky = c.getInt(c.getColumnIndex(ColumnItem.IS_STICKY)) == 1;

                bean.showMemberName = c.getInt(c.getColumnIndex(ColumnItem.IS_SHOW_NICK_NAME)) == 1;
                bean.isKickOut = c.getInt(c.getColumnIndex(ColumnItem.IS_KICK_OUT)) == 1;
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

    public void setIsKickOut(long gid, boolean isKickOut) {
        ContentValues values = new ContentValues();
        values.put(ColumnItem.IS_KICK_OUT, isKickOut);
        getWritableDB().update(ColumnItem.TABLE, values, ColumnItem.GID + " = ?",
                new String[]{String.valueOf(gid)});
    }


    public boolean showMemberName(long gid) {
        boolean show = false;
        StringBuffer sb = new StringBuffer();
        sb.append("select * from ").append(ColumnItem.TABLE).append(" where ");
        sb.append(ColumnItem.GID).append(" = ").append(gid);
        Cursor c = null;
        try {
            c = getWritableDB().rawQuery(sb.toString(), null);
            while (c.moveToNext()) {
                show = c.getInt(c.getColumnIndex(ColumnItem.IS_SHOW_NICK_NAME)) == 1;
                break;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return show;
    }

    @Override
    public String getCreateTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(ColumnItem.TABLE).append(" (");
        sb.append(ColumnItem.COL_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(ColumnItem.GID).append(" INTEGER UNIQUE, ");
        sb.append(ColumnItem.IS_SHOW_NICK_NAME).append(" INTEGER, ");
        sb.append(ColumnItem.IS_STICKY).append(" INTEGER DEFAULT 0, ");

        sb.append(ColumnItem.IS_MUTE).append(" INTEGER DEFAULT 0, ");
        sb.append(ColumnItem.CREATEOR_ID).append(" INTEGER, ");
        sb.append(ColumnItem.NAME).append(" TEXT, ");
        sb.append(ColumnItem.CREATE_TIME).append(" INTEGER , ");

        sb.append(ColumnItem.AVATAR).append(" TEXT, ");
        sb.append(ColumnItem.MEM_NUM).append(" INTEGER DEFAULT 0, ");
        sb.append(ColumnItem.MEMBERS).append(" TEXT, ");
        sb.append(ColumnItem.IS_KICK_OUT).append(" INTEGER DEFAULT 0, ");
        sb.append(ColumnItem.TABLE_VERSION).append(" INTEGER DEFAULT ").append(TABLE_VERSION);
        sb.append(")");
        return sb.toString();
    }

    @Override
    public void destroy() {
        instance = null;
    }

    private class ColumnItem {

        public static final String TABLE = "im_group_detail";

        public static final String COL_ID = "_id";
        public static final String GID = "gid";
        public static final String NAME = "name";

        public static final String CREATEOR_ID = "create_id";
        public static final String AVATAR = "avatar";
        public static final String CREATE_TIME = "create_time";

        public static final String MEM_NUM = "mem_num";
        public static final String IS_SHOW_NICK_NAME = "nick_name";
        public static final String IS_STICKY = "is_sticky";
        public static final String IS_MUTE = "is_mute";
        public static final String MEMBERS = "mem_ids";
        public static final String TABLE_VERSION = "table_version";
        public static final String IS_KICK_OUT = "is_kick_out";
    }
}
