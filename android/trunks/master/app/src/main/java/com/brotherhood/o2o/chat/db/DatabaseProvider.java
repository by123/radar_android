package com.brotherhood.o2o.chat.db;

import android.database.sqlite.SQLiteDatabase;

abstract class DatabaseProvider {

	abstract SQLiteDatabase getWritableDbSafely();

	abstract SQLiteDatabase getReadableDbSafely();
}
