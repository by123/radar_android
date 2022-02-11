package com.brotherhood.o2o.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.brotherhood.o2o.MyApplication;
import com.brotherhood.o2o.widget.ColorfulToast;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by by.huang on 2015/5/29.
 */
public class Utils {

    public static void showShortToast(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
//        ColorfulToast.orange(MyApplication.mApplication.getApplicationContext(),content,Toast.LENGTH_SHORT);
    }

    public static void showLongToast(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
//        ColorfulToast.orange(MyApplication.mApplication.getApplicationContext(),content,Toast.LENGTH_LONG);
    }

    public static String getString(int resId) {
        return MyApplication.mApplication.getString(resId);
    }

    /**
     * Determine the current network is available ȷ����ǰ�������
     *
     * @return
     */
    public static boolean isNetWorkOk() {

        boolean result = false;
        try {
            NetworkInfo info = getNetworkInfo(MyApplication.mApplication);
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    result = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static NetworkInfo getNetworkInfo(Context context) {

        NetworkInfo info = null;
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infoArray[] = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo aItem : infoArray) {
            if (aItem != null && aItem.isConnected()) {
                if (aItem.getState() == NetworkInfo.State.CONNECTED) {
                    info = aItem;
                    break;
                }
            }
        }
        if (info == null) {
            info = connectivityManager.getActiveNetworkInfo();
        }
        return info;
    }

    /**
     * dp转px
     */
    public static int dip2px(float dpValue) {
        final float scale = MyApplication.mApplication.getResources()
                .getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     */
    public static int px2dip(float pxValue) {
        final float scale = MyApplication.mApplication.getResources()
                .getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(float pxValue) {
        final float fontScale = MyApplication.mApplication
                .getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(float spValue) {
        final float fontScale = MyApplication.mApplication
                .getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreentHeight(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    public static void hideKeyboard(EditText... controls) {
        for (EditText control : controls)
            hideKeyboard(control);
    }

    public static void hideKeyboard(EditText editText) {
        if (editText == null)
            return;

        Context context = editText.getContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void writeStringToFile(String data, File file, boolean overrideIfExisted) {
        if (file.isDirectory() || (file.exists() && !overrideIfExisted))
            return;

        file.delete();

        byte[] buffer = data.getBytes();
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
            output.write(buffer);
        } catch (Exception ignored) {
        } finally {
            closeStream(output);
        }
    }

    public static String readStringOrNilFromFile(File file) {
        if (!file.exists() || file.isDirectory())
            return null;

        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            return new String(buffer);
        } catch (Exception ignored) {
        } finally {
            closeStream(input);
        }

        return null;
    }

    public static void closeStream(Closeable streamOrNil) {
        if (streamOrNil != null) {
            try {
                streamOrNil.close();
            } catch (IOException e) {
            }
        }
    }
}
