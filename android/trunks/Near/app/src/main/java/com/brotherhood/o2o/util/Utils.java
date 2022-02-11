package com.brotherhood.o2o.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;

import com.brotherhood.o2o.manager.LogManager;

import org.apache.http.HttpEntity;

import java.io.Closeable;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created with Android Studio.
 */
public class Utils {

	private static ThreadLocal<byte[]> threadSafeByteBuf = null;

	public static byte[] getThreadSafeByteBuffer() {
		if (threadSafeByteBuf == null) {
			threadSafeByteBuf = new ThreadLocal<byte[]>();
		}

		byte[] buf = threadSafeByteBuf.get();

		if (buf == null) {
			buf = new byte[1024 * 4]; // 4kb
			threadSafeByteBuf.set(buf);
		}

		return buf;
	}

	/**
	 * double转小数
	 * @param value
	 * @param format
	 * @return
	 */
	public static String format(double value, String format){
		DecimalFormat df = new DecimalFormat(format);
		return df.format(value);
	}

	/**
	 * 关闭IO
	 * @param obj
	 */
	public static void closeCloseable(Closeable obj) {
		try {
			// 修复小米MI2的JarFile没有实现Closeable导致崩溃问题
			if (obj != null && obj instanceof Closeable)
				obj.close();

		} catch (IOException e) {
			LogManager.e(e);
		}
	}

	// 产生userAgent
	public static String gennerateUserAgent(Context context) {
		StringBuilder sb = new StringBuilder();

		sb.append("Mozilla/5.0 (Linux; U; Android");
		sb.append(Build.VERSION.RELEASE);
		sb.append("; ");
		sb.append(Locale.getDefault().toString());

		String model = Build.MODEL;
		if (!TextUtils.isEmpty(model)) {
			sb.append("; ");
			sb.append(model);
		}

		String buildId = Build.ID;
		if (!TextUtils.isEmpty(buildId)) {
			sb.append("; Build/");
			sb.append(buildId);
		}

		sb.append(") ");

		int versionCode = 0;
		String packageName = context.getPackageName();
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo packageInfo = manager.getPackageInfo(packageName, 0);
			versionCode = packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			// Keep the versionCode 0 as default.
		}

		sb.append(packageName);
		sb.append("/");
		sb.append(versionCode);

		sb.append("; ");
		return sb.toString();
	}

	public static void closeHttpEntity(HttpEntity en) {
		if (en != null) {
			try {
				en.consumeContent();
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}
	}


	/**
	 * 判断字符串中是否含有Emoji表情符(true:有)
	 * @param source
	 * @return
	 */
	public static boolean containsEmoji(String source) {
		int len = source.length();
		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);
			if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
				return true;
			}
		}
		return false;
	}

	private static boolean isEmojiCharacter(char codePoint) {
		return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
				(codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
				((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
				&& (codePoint <= 0x10FFFF));
	}

	public static boolean isOnMainThread(){
		return Looper.myLooper() != Looper.getMainLooper();
	}


}
