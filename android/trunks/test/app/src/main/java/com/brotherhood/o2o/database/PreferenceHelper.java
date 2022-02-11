package com.brotherhood.o2o.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 用于设置偏好设置
 * 
 * @author by.huang
 * 
 */
public class PreferenceHelper {

    public static final String PREFERENCE = "_preference";

    public static final int PREFERENCE_CODE = 0;

    private static PreferenceHelper instance = null;

    private SharedPreferences preferences;

    private Editor editor;

    public PreferenceHelper(Context context) {
        super();
        preferences = context.getSharedPreferences(PREFERENCE, PREFERENCE_CODE);
        editor = preferences.edit();
    }

    public static PreferenceHelper sharePreference(Context context) {
        if (context == null) {
            return null;
        }
        if (instance == null) {
            instance = new PreferenceHelper(context.getApplicationContext());
        }
        return instance;
    }

    public void setInt(String key, int value) {
        editor.putInt(key, value).commit();
    }

    public int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    public void setFloat(String key, float value) {
        editor.putFloat(key, value).commit();
    }

    public float getFloat(String key, float defaultValue) {
        return preferences.getFloat(key, defaultValue);
    }

    public void setString(String key, String value) {
        editor.putString(key, value).commit();
    }

    public String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    public void setBoolean(String key, Boolean value) {
        editor.putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key, Boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    public void setLong(String key, long value) {
        editor.putLong(key, value);
    }

    public long getLong(String key, long defaultValue) {
        return preferences.getLong(key, defaultValue);
    }

}
