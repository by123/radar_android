package com.brotherhood.o2o.manager;

import android.util.Log;

import com.brotherhood.o2o.BuildConfig;

/**
 * Created with Android Studio.
 *
 * Author:xiaxf
 *
 * Date:2015/7/16.
 */
public class LogManager {
	public final static boolean DEBUG = BuildConfig.DEBUG;

	/**
	 * 根据type输出日志消息，包括方法名，方法行数，Message
	 *
	 * @param type
	 * @param message
	 */
	private static void log(int type, String message) {
		StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[4];
		String className = stackTrace.getClassName();
		String tag = className.substring(className.lastIndexOf('.') + 1);
		StringBuilder sb = new StringBuilder();

		sb.append("(")
				.append(stackTrace.getFileName())
				.append(":")
				.append(stackTrace.getLineNumber())
				.append(")")
				.append("#")
				.append(stackTrace.getMethodName())
				.append(":[")
				.append(message)
				.append("]");


		//message = stackTrace.getMethodName() + "#" + stackTrace.getLineNumber() + " [" + message + "]";
		switch (type) {
			case Log.DEBUG:
				Log.d(tag, sb.toString());
				break;
			case Log.INFO:
				Log.i(tag, sb.toString());
				break;
			case Log.WARN:
				Log.w(tag, sb.toString());
				break;
			case Log.ERROR:
				Log.e(tag, sb.toString());
				break;
			case Log.VERBOSE:
				Log.v(tag, sb.toString());
				break;
		}
	}


	private static String formatMessage(String message, Object... args) {
		if (message == null) {
			return "";
		}
		if (args != null && args.length > 0) {
			try {
				return String.format(message, args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return message;
	}
	//
	//public static void d(String tag, String message, Object... args) {
	//	if (DEBUG)
	//		Log.d(tag, formatMessage(message, args));
	//}
	//
	//public static void i(String tag, String message, Object... args) {
	//	if (DEBUG)
	//		Log.i(tag, formatMessage(message, args));
	//}
	//
	//public static void w(String tag, String message, Object... args) {
	//	if (DEBUG)
	//		Log.w(tag, formatMessage(message, args));
	//}
	//
	//public static void e(String tag, String message, Object... args) {
	//	if (DEBUG)
	//		Log.e(tag, formatMessage(message, args));
	//}
	//
	//public static void v(String tag, String message, Object... args) {
	//	if (DEBUG)
	//		Log.v(tag, formatMessage(message, args));
	//}

	public static void d(String message, Object... args) {
		if (DEBUG)
			log(Log.DEBUG, formatMessage(message, args));
	}

	public static void i(String message, Object... args) {
		if (DEBUG)
			log(Log.INFO, formatMessage(message, args));
	}

	public static void w(String message, Object... args) {
		if (DEBUG)
			log(Log.WARN, formatMessage(message, args));
	}

	public static void e(String message, Object... args) {
		if (DEBUG)
			log(Log.ERROR, formatMessage(message, args));
	}

	public static void v(String message, Object... args) {
		if (DEBUG)
			log(Log.VERBOSE, formatMessage(message, args));
	}

	public static void e(Throwable tr) {
		if (DEBUG) {
			tr.printStackTrace();
		}
	}

}
