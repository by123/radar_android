package com.brotherhood.o2o.chat.cache;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

final class CacheUtils {

	/**
	 * Execute an {@link AsyncTask} on a thread pool
	 * 
	 * @param forceSerial
	 *            True to force the task to run in serial order
	 * @param task
	 *            Task to execute
	 * @param args
	 *            Optional arguments to pass to
	 *            {@link AsyncTask#execute(Object[])}
	 * @param <T>
	 *            Task argument type
	 */
	@SuppressWarnings("unchecked")
	@SuppressLint("NewApi")
	public static <T> void execute(AsyncTask<T, ?, ?> task, final T... args) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.DONUT) {
			throw new UnsupportedOperationException(
					"This class can only be used on API 4 and newer.");
		}
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			task.execute(args);
		} else {
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, args);
		}
	}

	/**
	 * @return Number of bytes available on external storage
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static long getExternalAvailableSpaceInBytes() {
		long availableSpace = -1L;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(sdcardDir.getPath());
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
				availableSpace = stat.getAvailableBlocks()
						* stat.getBlockSize();
			} else {
				availableSpace = stat.getAvailableBytes();
			}
		}

		return availableSpace;
	}
}
