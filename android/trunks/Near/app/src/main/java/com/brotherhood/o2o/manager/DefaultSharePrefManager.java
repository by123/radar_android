package com.brotherhood.o2o.manager;

import com.brotherhood.o2o.config.SharePrefConstant;
import com.brotherhood.o2o.util.SharePrefUtil;

/**
 * Created with Android Studio.
 */
public class DefaultSharePrefManager {

	public static void putString(String key, String value) { SharePrefUtil.putString(SharePrefConstant.NAME_DEFAULT, key, value); }

	public static void putInt(String key, int value) {
		SharePrefUtil.putInt(SharePrefConstant.NAME_DEFAULT, key, value);
	}

	public static void putLong(String key, long value) {
		SharePrefUtil.putLong(SharePrefConstant.NAME_DEFAULT, key, value);
	}

	public static void putFloat(String key, float value) {
		SharePrefUtil.putFloat(SharePrefConstant.NAME_DEFAULT, key, value);
	}

	public static void putBoolean(String key, boolean value) {
		SharePrefUtil.putBoolean(SharePrefConstant.NAME_DEFAULT, key, value);
	}

	public static String getString(String key, String defaultValue) {
		return SharePrefUtil.getString(SharePrefConstant.NAME_DEFAULT, key, defaultValue);
	}

	public static int getInt(String key, int defaultValue) {
		return SharePrefUtil.getInt(SharePrefConstant.NAME_DEFAULT, key, defaultValue);

	}

	public static long getLong(String key, long defaultValue) {
		return SharePrefUtil.getLong(SharePrefConstant.NAME_DEFAULT, key, defaultValue);

	}

	public static float getFloat(String key, float defaultValue) {
		return SharePrefUtil.getFloat(SharePrefConstant.NAME_DEFAULT, key, defaultValue);

	}

	public static boolean getBoolean(String key, boolean defaultValue) {
		return SharePrefUtil.getBoolean(SharePrefConstant.NAME_DEFAULT, key, defaultValue);
	}

	public static void remove(String key) {
		SharePrefUtil.remove(SharePrefConstant.NAME_DEFAULT, key);
	}

}
