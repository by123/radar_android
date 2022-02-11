package com.brotherhood.o2o.manager;

import android.content.Context;
import android.os.Environment;

import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.config.Constants;

import java.io.File;

/**
 * 目录管理
 */
public class DirManager {

	/**
	 * 获取默认缓存存储路径
	 * @param childDir 子目录名
	 * @return 路径:Android/data/cache/childDir
	 */
	public static File getCachesDir(String childDir) {
		Context context = NearApplication.mInstance;
		File file;
		File baseDir = context.getExternalCacheDir();
		if (baseDir == null) {
			baseDir = context.getCacheDir();
		}
		file = new File(baseDir, childDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		LogManager.d("==============getCachesDir:"+file.getAbsolutePath());
		return file;
	}

	/**
	 * 获取SD卡上普通目录（防止调用第三方应用无权限输出到本应用目录）
	 * @param childDir
	 * @return
	 */
	public static String getExternalStroageDir(String childDir){
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED) || status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
			String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
			File dirFile = new File(storagePath, childDir);
			if (!dirFile.exists()){
				dirFile.mkdirs();
			}
			LogManager.d("==============getExternalStroageDir:"+dirFile.getAbsolutePath());
			return dirFile.getAbsolutePath();
		}
		return "";
	}

	/**
	 * 获取SD卡上普通目录（防止调用第三方应用无权限输出到本应用目录）
	 * @param childDir
	 * @return
	 */
	public static File getExternalStroageDirFile(String childDir){
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED) || status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
			String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
			File dirFile = new File(storagePath, childDir);
			if (!dirFile.exists()){
				dirFile.mkdirs();
			}
			LogManager.d("==============getExternalStroageDir:"+dirFile.getAbsolutePath());
			return dirFile;
		}
		return null;
	}

	/**
	 * 获取默认文件存储路径
	 * @param childDir 子目录名
	 * @return 路径:Android/data/files/childDir
	 */
	public static File getFilesDir(String childDir) {
		Context context = NearApplication.mInstance;
		File baseDir = context.getExternalFilesDir(childDir);
		if (baseDir == null) {
			baseDir = new File(context.getFilesDir(), childDir);
		}
		if (!baseDir.exists()) {
			baseDir.mkdirs();
		}
		LogManager.d("==============getFilesDir:"+baseDir.getAbsolutePath());
		return baseDir;
	}


	/**
	 * 获取图片缓存目录
	 * @return
	 */
	public static File getImageFilesDir() {
		return getFilesDir(Constants.IMAGE_DIR);
	}

	/**
	 * 获取用户信息缓存目录
	 * @return
	 */
	public static File getUserFileDir() {
		return getCachesDir("user");
	}

}
