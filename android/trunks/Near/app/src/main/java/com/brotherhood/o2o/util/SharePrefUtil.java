package com.brotherhood.o2o.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.brotherhood.o2o.application.NearApplication;

/**
 * Created with Android Studio.
 */
public class SharePrefUtil {

	public static SharedPreferences getSharedPreferences(String name){
		return NearApplication.mInstance.getSharedPreferences(name, Context.MODE_PRIVATE);
	}

	public static void putString(String name, String key, String value){
		SharedPreferences.Editor editor = getSharedPreferences(name).edit();
		editor.putString(key, value);
		editor.apply();
	}

	public static void putInt(String name, String key, int value) {
		SharedPreferences.Editor editor = getSharedPreferences(name).edit();
		editor.putInt(key, value);
		editor.apply();
	}

	public static void putLong(String name, String key, long value) {
		SharedPreferences.Editor editor = getSharedPreferences(name).edit();
		editor.putLong(key, value);
		editor.apply();
	}

	public static void putFloat(String name, String key, float value) {
		SharedPreferences.Editor editor = getSharedPreferences(name).edit();
		editor.putFloat(key, value);
		editor.apply();
	}

	public static void putBoolean(String name, String key, boolean value) {
		SharedPreferences.Editor editor = getSharedPreferences(name).edit();
		editor.putBoolean(key, value);
		editor.apply();
	}

	public static String getString(String name, String key, String defaultValue) {
		SharedPreferences settings = getSharedPreferences(name);
		return settings.getString(key, defaultValue);
	}

	public static int getInt(String name, String key, int defaultValue) {
		SharedPreferences settings = getSharedPreferences(name);
		return settings.getInt(key, defaultValue);
	}

	public static long getLong(String name, String key, long defaultValue) {
		SharedPreferences settings = getSharedPreferences(name);
		return settings.getLong(key, defaultValue);
	}

	public static float getFloat(String name, String key, float defaultValue) {
		SharedPreferences settings = getSharedPreferences(name);
		return settings.getFloat(key, defaultValue);
	}

	public static boolean getBoolean(String name, String key, boolean defaultValue) {
		SharedPreferences settings = getSharedPreferences(name);
		return settings.getBoolean(key, defaultValue);
	}

	public static void remove(String name, String key){
		SharedPreferences.Editor editor = getSharedPreferences(name).edit();
		editor.remove(key).apply();
	}
}
