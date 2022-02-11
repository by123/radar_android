/**
 * 
 */

package com.brotherhood.o2o.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/**
 * @author qinyn 获取资源res util
 */
public class Res {
    private static Resources res;

    public static void setContext(Context context) {
        Res.res = context.getResources();
    }

    public static String getString(int id) {
        return res.getString(id);
    }

    public static int getColor(int id) {
        return res.getColor(id);
    }

    public static int getDimen(int id) {
        return res.getDimensionPixelSize(id);
    }

    public static String[] getStringArray(int id) {
        return res.getStringArray(id);
    }

    public static Drawable getDrawable(int id) {
        return res.getDrawable(id);
    }

    public static int getInteger(int id) {
        return res.getInteger(id);
    }

    public static int[] getIngegerArray(int id) {
        return res.getIntArray(id);
    }

    public static boolean getBoolean(int id) {
        return res.getBoolean(id);
    }

}
