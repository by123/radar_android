package com.brotherhood.o2o.chat.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库操作器
 * 
 * @author David
 * 
 */
public abstract class DatabaseHandler {

	/**
	 * Insert a record into a table, returns immediately
	 */
	public abstract void insert(String table, ContentValues values);

	/**
	 * Insert a record into a table and wait.
	 * 
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public abstract long insertAndWait(String table, ContentValues values);

	/**
	 * 更新数据库，立即返回
	 * 
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public abstract void update(String table, ContentValues values,
			String where, String[] whereArgs);

	public abstract long updateAndWait(String table, ContentValues values,
			String where, String[] whereArgs);

	public abstract void delete(String table, String where, String[] whereArgs);

	public abstract long deleteAndWait(String table, String where,
			String[] whereArgs);

	public abstract void replace(String table, ContentValues values);

	public abstract long replaceAndWait(String table, ContentValues values);

	public abstract Cursor query(String sql, String[] whereArgs);

	/**
	 * Execute a single SQL statement that is not a query.
	 * 
	 * @param sql
	 */
	public abstract void execSQL(String sql, Object[] whereArgs);

	/**
	 * Close this database, and all of the resources related will be released.
	 * <p>
	 * <b>Once closed, the database helper can not be used any more.</b>
	 */
	public abstract void close();

	public abstract SQLiteDatabase getDatabase();
}
