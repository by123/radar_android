package com.brotherhood.o2o.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.brotherhood.o2o.application.NearApplication;

import java.lang.reflect.Field;

/**
 * Created with Android Studio.
 */
public class DisplayUtil {

	/**
	 * 获取屏幕宽度
	 * @param activity
	 * @return
	 */
	public static int getScreenWidth(Activity activity) {
		Display display = activity.getWindow().getWindowManager().getDefaultDisplay();
		return display.getWidth();
	}

	/**
	 * 获取屏幕宽度
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		final DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		return dm.widthPixels;
	}

	/**
	 * 获取屏幕高度
	 *
	 * @param activity
	 * @return
	 */
	public static int getScreenHeight(Activity activity) {
		Display display = activity.getWindow().getWindowManager().getDefaultDisplay();
		return display.getHeight();
	}

	/**
	 * 获取屏幕高度
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		final DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		return dm.heightPixels;
	}

	/**
	 * 获取状�?栏高�?
	 * @param context
	 * @return
	 */
	public static int getBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 38;// 默认值38，貌似大部分是这样的
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = context.getResources().getDimensionPixelSize(x);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return sbar;
	}

	/**
	 * 获取屏幕密度
	 * @param context
	 * @return
	 */
	public static String getDensity(Context context) {
		float d = context.getResources().getDisplayMetrics().density;
		String density = d + "";
		return density;
	}


	/**
	 * 显示键盘
	 * @param ctx
	 */
	public static void showKeyboard(Activity ctx) {
		InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}

	/**
	 * 显示键盘
	 * @param ctx
	 */
	public static void showKeyboard(Context ctx) {
		InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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
	/**
	 * 隐藏键盘
	 * @param ctx
	 * @return
	 */
	public static boolean hideKeyboard(Activity ctx) {
		InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
		View curFocus = ctx.getCurrentFocus();
		return curFocus != null && imm.hideSoftInputFromWindow(curFocus.getWindowToken(), 0);
	}

	/**
	 * 显示Toast
	 * @param context
	 * @param resId
	 */
	public static void showToast(Context context, int resId) {
		if (context == null)
			return;

		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 显示Toast
	 * @param context
	 * @param msg
	 */
	public static void showToast(Context context, String msg) {
		if (context == null)
			return;

		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}


	/**
	 * 显示Toast
	 * @param context
	 * @param msg
	 */
	public static void showLongToast(Context context, String msg) {
		if (context == null)
			return;

		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}


	/**
	 * <p>dp转换为px</p>
	 * @param dp
	 */
	public static int dp2px(float dp) {
		final float scale = NearApplication.mInstance.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/**
	 * <p>px转换为dp</p>
	 * @return
	 */
	public static int px2dp(float px) {
		final float scale = NearApplication.mInstance.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 *
	 * @param pxValue （DisplayMetrics类中属�?scaledDensity�?
	 * @return
	 */
	public static int px2sp(float pxValue) {
		final float fontScale = NearApplication.mInstance.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 *
	 * @param spValue（DisplayMetrics类中属�?scaledDensity�?
	 * @return
	 */
	public static int sp2px(float spValue) {
		final float fontScale = NearApplication.mInstance.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}
}
