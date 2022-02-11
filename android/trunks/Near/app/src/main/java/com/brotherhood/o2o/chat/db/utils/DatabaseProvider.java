package com.brotherhood.o2o.chat.db.utils;

import android.database.sqlite.SQLiteDatabase;

abstract class DatabaseProvider {

	abstract SQLiteDatabase getWritableDbSafely();

	abstract SQLiteDatabase getReadableDbSafely();
}
