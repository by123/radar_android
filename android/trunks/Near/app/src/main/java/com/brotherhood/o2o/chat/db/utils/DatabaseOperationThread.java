package com.brotherhood.o2o.chat.db.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

class DatabaseOperationThread extends DatabaseHandler {

	private HandlerThread mDbThread;

	static final String TAG = "DatabaseOperationThread";

	private DatabaseProvider mProvider;

	String mPath;

	DatabaseOperationThread(String path, DatabaseProvider dp) {
		mPath = path;
		mDbThread = new HandlerThread("db_thread[" + path + "]");
		mDbThread.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		mProvider = dp;
	}

	private static class InternalHandler extends Handler {

		private DatabaseProvider mProvider;

		InternalHandler(Looper looper, DatabaseProvider dp) {
			super(looper);
			mProvider = dp;
		}

		private static final int MSG_QUERY = 1;

		private static final int MSG_INSERT = 2;
		private static final int MSG_INSERT_UNBLOCKLY = 3;

		private static final int MSG_DELETE = 4;
		private static final int MSG_DELETE_UNBLOCKLY = 5;

		private static final int MSG_UPDATE = 6;
		private static final int MSG_UPDATE_UNBLOCKLY = 7;

		private static final int MSG_REPLACE = 8;
		private static final int MSG_REPLACE_UNBLOCKLY = 9;

		private static final int MSG_CLOSE = 10;
		private static final int MSG_EXEC = 11;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			final int what = msg.what;
			switch (what) {
			case MSG_INSERT:
			case MSG_INSERT_UNBLOCKLY:
				InsertArgs insert = (InsertArgs) msg.obj;
				SQLiteDatabase insertDb = mProvider.getWritableDbSafely();
				if (insertDb == null) {
					insert.returnValue = -1L;
				} else {
					insert.returnValue = insertDb.insert(insert.table, null,
							insert.values);
				}
				if (what == MSG_INSERT) {
					synchronized (insert) {
						insert.notify();
					}
				}
				break;
			case MSG_DELETE:
			case MSG_DELETE_UNBLOCKLY:
				DeleteArgs delete = (DeleteArgs) msg.obj;
				SQLiteDatabase deleteDb = mProvider.getWritableDbSafely();
				if (deleteDb == null) {
					delete.returnValue = -1L;

				} else {
					delete.returnValue = deleteDb.delete(delete.table,
							delete.where, delete.whereArgs);
				}
				if (what == MSG_DELETE) {
					synchronized (delete) {
						delete.notify();
					}
				}
				break;
			case MSG_UPDATE:
			case MSG_UPDATE_UNBLOCKLY:
				UpdateArgs updateObj = (UpdateArgs) msg.obj;
				SQLiteDatabase updateDb = mProvider.getWritableDbSafely();
				if (updateDb == null) {
					updateObj.returnValue = -1L;
				} else {
					updateObj.returnValue = updateDb.update(updateObj.table,
							updateObj.values, updateObj.where,
							updateObj.whereArgs);
				}
				if (what == MSG_UPDATE) {
					synchronized (updateObj) {
						updateObj.notify();
					}
				}
				break;
			case MSG_REPLACE:
			case MSG_REPLACE_UNBLOCKLY:
				ReplaceArgs replaceObj = (ReplaceArgs) msg.obj;
				SQLiteDatabase replaceDb = mProvider.getWritableDbSafely();
				if (replaceDb == null) {
					replaceObj.returnValue = -1L;
				} else {
					replaceObj.returnValue = replaceDb.replace(
							replaceObj.table, null, replaceObj.values);
				}
				if (what == MSG_REPLACE) {
					synchronized (replaceObj) {
						replaceObj.notify();
					}
				}
				break;
			case MSG_QUERY:
				QueryArgs query = (QueryArgs) msg.obj;
				SQLiteDatabase queryDb = mProvider.getWritableDbSafely();
				if (queryDb != null) {
					query.returnValue = queryDb.rawQuery(query.sql,
							query.whereArgs);
				}
				synchronized (query) {
					query.notifyAll();
				}
				break;
			case MSG_EXEC:
				ExeArgs args = (ExeArgs) msg.obj;
				SQLiteDatabase execDb = mProvider.getWritableDbSafely();
				if (execDb != null) {
					if (args.whereArgs == null) {
						args.whereArgs = new Object[] {};
					}
					execDb.execSQL(args.sql, args.whereArgs);
				}
				break;
			case MSG_CLOSE:
				SQLiteDatabase db = mProvider.getReadableDbSafely();
				if (db != null) {
					try {
						db.close();
						Log.e(TAG, "Database closed.");
					} catch (Exception e) {
						Log.e(TAG, "Close database error.");
					}
				}
				break;
			}
		}
	}

	private static class InsertArgs {
		String table;
		ContentValues values;
		long returnValue;
	}

	private static class UpdateArgs {
		String table;
		ContentValues values;
		String where;
		String[] whereArgs;
		long returnValue;
	}

	private static class DeleteArgs {
		String table;
		String where;
		String[] whereArgs;
		long returnValue;
	}

	private static class ReplaceArgs {
		String table;
		ContentValues values;
		long returnValue;
	}

	private static class QueryArgs {
		String sql;
		String[] whereArgs;
		Cursor returnValue;
	}

	private static class ExeArgs {
		String sql;
		Object[] whereArgs;
	}

	private Handler mPrivateHandler;

	private int messageId;

	synchronized void activeSelf() {
		mDbThread.start();
		mPrivateHandler = new InternalHandler(mDbThread.getLooper(), mProvider);
	}

	synchronized void detroySelf() {
		if (mPrivateHandler != null) {
			mDbThread.getLooper().quit();
		}
	}

	/**
	 * Obtain synchronously a message.
	 * 
	 * @param what
	 * @return
	 */
	private Message syncMessage(int what, Object obj) {
		synchronized (this) {
			Message msg = Message.obtain();
			msg.arg1 = messageId;
			msg.what = what;
			msg.obj = obj;
			messageId++;
			return msg;
		}
	}

	@Override
	public void insert(String table, ContentValues values) {
		InsertArgs obj = new InsertArgs();
		obj.table = table;
		obj.values = values;
		mPrivateHandler.sendMessage(syncMessage(
				InternalHandler.MSG_INSERT_UNBLOCKLY, obj));
	}

	@Override
	public long insertAndWait(String table, ContentValues values) {
		InsertArgs obj = new InsertArgs();
		obj.table = table;
		obj.values = values;
		synchronized (obj) {
			mPrivateHandler.sendMessage(syncMessage(InternalHandler.MSG_INSERT,
					obj));
			try {
				obj.wait();
			} catch (InterruptedException e) {
			}
		}
		return obj.returnValue;
	}

	@Override
	public void update(String table, ContentValues values, String where,
			String[] whereArgs) {
		UpdateArgs obj = new UpdateArgs();
		obj.table = table;
		obj.values = values;
		obj.where = where;
		obj.whereArgs = whereArgs;
		mPrivateHandler.sendMessage(syncMessage(
				InternalHandler.MSG_UPDATE_UNBLOCKLY, obj));
	}

	@Override
	public long updateAndWait(String table, ContentValues values, String where,
			String[] whereArgs) {
		UpdateArgs obj = new UpdateArgs();
		obj.table = table;
		obj.values = values;
		obj.where = where;
		obj.whereArgs = whereArgs;
		synchronized (obj) {
			mPrivateHandler.sendMessage(syncMessage(InternalHandler.MSG_UPDATE,
					obj));
			try {
				obj.wait();
			} catch (InterruptedException e) {
			}
		}
		return obj.returnValue;
	}

	@Override
	public void delete(String table, String where, String[] whereArgs) {
		DeleteArgs obj = new DeleteArgs();
		obj.table = table;
		obj.where = where;
		obj.whereArgs = whereArgs;
		mPrivateHandler.sendMessage(syncMessage(
				InternalHandler.MSG_DELETE_UNBLOCKLY, obj));
	}

	@Override
	public long deleteAndWait(String table, String where, String[] whereArgs) {
		DeleteArgs obj = new DeleteArgs();
		obj.table = table;
		obj.where = where;
		obj.whereArgs = whereArgs;
		synchronized (obj) {
			mPrivateHandler.sendMessage(syncMessage(InternalHandler.MSG_DELETE,
					obj));
			try {
				obj.wait();
			} catch (InterruptedException e) {
			}
		}
		return obj.returnValue;
	}

	@Override
	public void replace(String table, ContentValues values) {
		ReplaceArgs obj = new ReplaceArgs();
		obj.table = table;
		obj.values = values;
		mPrivateHandler.sendMessage(syncMessage(
				InternalHandler.MSG_REPLACE_UNBLOCKLY, obj));
	}

	@Override
	public long replaceAndWait(String table, ContentValues values) {
		ReplaceArgs obj = new ReplaceArgs();
		obj.table = table;
		obj.values = values;
		synchronized (obj) {
			mPrivateHandler.sendMessage(syncMessage(
					InternalHandler.MSG_REPLACE, obj));
			try {
				obj.wait();
			} catch (InterruptedException e) {
			}
		}
		return obj.returnValue;
	}

	@Override
	public Cursor query(String sql, String[] whereArgs) {
		QueryArgs obj = new QueryArgs();
		obj.sql = sql;
		obj.whereArgs = whereArgs;
		synchronized (obj) {
			mPrivateHandler.sendMessage(syncMessage(InternalHandler.MSG_QUERY,
					obj));
			try {
				obj.wait();
			} catch (InterruptedException e) {
			}
		}
		return obj.returnValue;
	}

	@Override
	public void execSQL(String sql, Object[] whereArgs) {
		ExeArgs args = new ExeArgs();
		args.sql = sql;
		args.whereArgs = whereArgs;
		mPrivateHandler
				.sendMessage(syncMessage(InternalHandler.MSG_EXEC, args));
	}

	public void close() {
		mPrivateHandler
				.sendMessage(syncMessage(InternalHandler.MSG_CLOSE, null));
		mDbThread.quit();
		mDbThread = null;
	}

	@Override
	public SQLiteDatabase getDatabase() {
		return mProvider.getWritableDbSafely();
	}

}
