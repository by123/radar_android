package com.brotherhood.o2o.chat.db;

import java.util.HashMap;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.brotherhood.o2o.utils.ByLogout;

public abstract class SimpleDatabaseHelper extends DatabaseHelper {

	private static DatabaseOperationThread sThread;

	/*
	 * 保存所有插件的最新版本
	 */
	private static HashMap<String, HashMap<String, Integer>> sCacheVersions = new HashMap<String, HashMap<String, Integer>>();

	private static HashMap<String, Boolean> sCacheAppFirstTime = new HashMap<String, Boolean>();

	private static final String TAG = "SimpleDatabaseHelper";

	private String mPluginDbIdentifier;

	private int mPluginDbVersion;

	private String mPath;

	public SimpleDatabaseHelper(String path, String pluginDbIdentifier,
			int pluginDbVersion) {
		/*
		 * 数据库的版本为1，将永远不升级，升级的逻辑将分布在各个插件中
		 */
		super(path, null, 1);
		if (TextUtils.isEmpty(pluginDbIdentifier)) {
			throw new RuntimeException();
		}
		mPluginDbIdentifier = pluginDbIdentifier;
		mPluginDbVersion = pluginDbVersion;
		mPath = path;
		ByLogout.out("数据库地址->"+mPath);

		Log.d(TAG, "path:" + path);
		syncAllPluginVersions(path);
	}

	private void syncAllPluginVersions(String path) {
		synchronized (SimpleDatabaseHelper.class) {
			HashMap<String, Integer> cacheVersions = sCacheVersions.get(path);
			boolean appFirstTime = sCacheAppFirstTime.containsKey(path) ? sCacheAppFirstTime
					.get(path) : false;
			if (cacheVersions == null) {
				Log.i(TAG, "subclass instance allocated first time");
				cacheVersions = new HashMap<String, Integer>();
				appFirstTime = true;
			} else {
				Log.i(TAG, "new subclass created...");
				appFirstTime = false;
			}
			Integer vInCache = cacheVersions.get(mPluginDbIdentifier);
			SQLiteDatabase db = getHandler().getDatabase();
			db.beginTransaction();
			try {
				if (vInCache == null) {
					if (appFirstTime) {
						// cache里边没有该项记录，查找数据库各个插件版本的记录，并缓存在cache中
						Cursor cursor = db.rawQuery(
								"SELECT _label, _version FROM plugins", null);
						// 存储数据库中 存在的插件版本
						if (cursor != null) {
							HashMap<String, Integer> storedVersions = null;
							while (cursor.moveToNext()) {
								if (storedVersions == null) {
									storedVersions = new HashMap<String, Integer>();
								}
								String pluginName = cursor.getString(cursor
										.getColumnIndex("_label"));
								int version = cursor.getInt(cursor
										.getColumnIndex("_version"));
								storedVersions.put(pluginName, version);
							}
							cursor.close();
							if (storedVersions != null) {
								cacheVersions.putAll(storedVersions);
							}
						}
					}

					Integer vInDatabase = cacheVersions
							.get(mPluginDbIdentifier);
					if (vInDatabase == null) {
						// 数据库里也没有该项记录，那么创建插件数据库吧
						ContentValues values = new ContentValues();
						values.put("_label", mPluginDbIdentifier);
						values.put("_version", mPluginDbVersion);

						if (db.replace("plugins", null, values) != -1) {
							// 当版本号记录成功的时候才放入cache，这样的话，如果失败，下次有机会重新创建数据库
							onPluginDbCreate(db);
							cacheVersions.put(mPluginDbIdentifier,
									mPluginDbVersion);
						}
					} else {
						// 数据库里有该项信息，检查是否版本号不一致
						int versionValue = vInDatabase.intValue();
						if (versionValue != mPluginDbVersion) {
							ContentValues values = new ContentValues();
							values.put("_label", mPluginDbIdentifier);
							values.put("_version", mPluginDbVersion);
							if (db.replace("plugins", null, values) != -1) {
								// 当版本号记录成功的时候才放入cache，这样的话，如果失败，下次也将继续插入版本号
								onPluginDbUpgrade(db, versionValue,
										mPluginDbVersion);
								cacheVersions.put(mPluginDbIdentifier,
										mPluginDbVersion);
							} else {
								// 版本号记录失败，那么cache里边保持旧的版本号，下次有机会可以继续更新版本号
								cacheVersions
										.put(mPluginDbIdentifier, vInCache);
							}
						}
					}
				} else {
					// cache里边有版本号信息，检查是否不一致
					int version = vInCache.intValue();
					if (version != mPluginDbVersion) {
						ContentValues values = new ContentValues();
						values.put("_label", mPluginDbIdentifier);
						values.put("_version", mPluginDbVersion);
						if (db.replace("plugins", null, values) != -1) {
							// 当版本号记录成功的时候才放入cache，这样的话，如果失败，下次也将继续插入版本号
							onPluginDbUpgrade(db, version, mPluginDbVersion);
							cacheVersions.put(mPluginDbIdentifier,
									mPluginDbVersion);
						} else {
							// 版本号记录失败，那么cache里边保持旧的版本号，下次有机会可以继续更新版本号
							cacheVersions.put(mPluginDbIdentifier, vInCache);
						}
					}
				}
				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.endTransaction();
			}

			sCacheVersions.put(path, cacheVersions);
			sCacheAppFirstTime.put(path, appFirstTime);
		}
	}

	/**
	 * Get the database operator
	 * 
	 * @return
	 */
	public DatabaseHandler getHandler() {
		DatabaseOperationThread thread = null;
		synchronized (SimpleDatabaseHelper.class) {
			boolean needsCreate = false;
			if (sThread != null) {
				if (sThread.mPath.equals(mPath)) {
					// The same one
					return sThread;
				} else {
					sThread.detroySelf();
					needsCreate = true;
				}
			} else {
				needsCreate = true;
			}

			if (needsCreate) {
				thread = new DatabaseOperationThread(mPath, this);
				thread.activeSelf();
				sThread = thread;
			}
		}
		return thread;
	}

	@Override
	protected final void onCreate(SQLiteDatabase db) {
		// We create a table to save every plugin's version.
		db.execSQL("CREATE TABLE plugins(_label TEXT PRIMARY KEY, _version INTEGER)");
		Log.i(TAG, "onCreate");
	}

	@Override
	protected final void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		throw new RuntimeException("Unexpected upgrade...");
	}

	/**
	 * 当插件数据库版本需要升级时，被调用。无论版本号上升还是下降，该方法均会被调用。
	 * 
	 * @param db
	 * @param oldVersion
	 *            旧版本号
	 * @param newVersion
	 *            新版本号
	 */
	protected void onPluginDbUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
	}

	/**
	 * 当插件数据库版本需要创建时，被调用
	 * 
	 * @param db
	 */
	protected void onPluginDbCreate(SQLiteDatabase db) {
	}
}
