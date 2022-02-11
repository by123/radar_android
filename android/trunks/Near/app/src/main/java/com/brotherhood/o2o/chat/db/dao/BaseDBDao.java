package com.brotherhood.o2o.chat.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.brotherhood.o2o.chat.IDSIMManager;
import com.brotherhood.o2o.chat.db.helper.BaseSQLiteHelper;
import com.brotherhood.o2o.manager.AccountManager;

/**
 * Created by Administrator on 2015/12/17 0017.
 */
public abstract class BaseDBDao {

    private BaseSQLiteHelper mHelper;
    protected Context mContext;

    protected BaseDBDao(Context context) {
        mHelper = BaseSQLiteHelper.getInstance(context);
        mContext = context.getApplicationContext();
    }

    protected SQLiteDatabase getWritableDB() {
        return mHelper.getWritableDatabase();
    }

    protected SQLiteDatabase getReadableDB() {
        return mHelper.getReadableDatabase();
    }

    public abstract String getCreateTableSQL();

    public abstract void destroy();

//    public abstract String createIndex();
}
