package com.brotherhood.o2o.chat.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.chat.db.dao.IDSChatMsgDao;
import com.brotherhood.o2o.chat.db.dao.IDSGroupChatDao;
import com.brotherhood.o2o.chat.db.dao.IMGroupDao;
import com.brotherhood.o2o.chat.db.dao.IMLatestMsgDao;
import com.brotherhood.o2o.chat.db.dao.IMNewFriendDao;
import com.brotherhood.o2o.chat.db.dao.IMSendMsgDao;
import com.brotherhood.o2o.chat.db.dao.IMSystemMsgDao;
import com.brotherhood.o2o.chat.db.dao.IMUserDao;
import com.brotherhood.o2o.manager.AccountManager;

/**
 * Created by Administrator on 2015/12/19 0019.
 */
public class BaseSQLiteHelper extends SQLiteOpenHelper {
    private final static int DB_VERSION = 1;
    private final static String DB_NAME = "_im_db";
    private static BaseSQLiteHelper instance;
    private Context mContext;

    private BaseSQLiteHelper(Context context) {
        super(context, AccountManager.getInstance().getUser().mUid + DB_NAME, null, DB_VERSION);
        this.mContext = context.getApplicationContext();
    }

    public static BaseSQLiteHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (BaseSQLiteHelper.class) {
                if (instance == null) {
                    instance = new BaseSQLiteHelper(context);
                }
            }
        }
        return instance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        db.execSQL(IMSendMsgDao.getInstance(mContext).getCreateTableSQL());
        db.execSQL(IMLatestMsgDao.getInstance(mContext).getCreateTableSQL());
        db.execSQL(IMSystemMsgDao.getInstance(mContext).getCreateTableSQL());
        db.execSQL(IMUserDao.getInstance(mContext).getCreateTableSQL());
        db.execSQL(IMNewFriendDao.getInstance(mContext).getCreateTableSQL());
        db.execSQL(IMGroupDao.getInstance(mContext).getCreateTableSQL());
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void destroy() {
        getWritableDatabase().close();
        IMSendMsgDao.getInstance(mContext).destroy();
        IMLatestMsgDao.getInstance(mContext).destroy();
        IMSystemMsgDao.getInstance(mContext).destroy();
        IMUserDao.getInstance(mContext).destroy();
        IMNewFriendDao.getInstance(mContext).destroy();
        IMGroupDao.getInstance(mContext).destroy();
        IDSChatMsgDao.getInstance(mContext).destroy();
        IDSGroupChatDao.getInstance(mContext).destroy();
        instance = null;
    }

}
