package com.brotherhood.o2o.manager;

import android.content.Context;
import android.os.Environment;

import com.brotherhood.o2o.application.MyApplication;
import com.brotherhood.o2o.utils.FileUtil;

import java.io.File;

/**
 * 目录管理
 * Created with Android Studio.
 * <p/>
 * Author:xiaxf
 * <p/>
 * Date:2015/7/20.
 */
public class DirManager {

	/**
	 * 获取默认缓存存储路径
	 * @param childDir 子目录名
	 * @return 路径:Android/data/cache/childDir
	 */
	public static File getCachesDir(String childDir) {
		Context context = MyApplication.mApplication;
		File file;
		File baseDir = context.getExternalCacheDir();
		if (baseDir == null) {
			baseDir = context.getCacheDir();
		}
		file = new File(baseDir, childDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}


	/**
	 * 获取默认文件存储路径
	 * @param childDir 子目录名
	 * @return 路径:Android/data/files/childDir
	 */
	public static File getFilesDir(String childDir) {
		Context context = MyApplication.mApplication;
		File baseDir = context.getExternalFilesDir(childDir);
		if (baseDir == null) {
			baseDir = new File(context.getFilesDir(), childDir);
		}
		if (!baseDir.exists()) {
			baseDir.mkdirs();
		}
		return baseDir;
	}


	/**
	 * 获取图片缓存目录
	 * @return
	 */
	public static File getImageFilesDir() {
		return getFilesDir("images");
	}

	/**
	 * 获取用户信息缓存目录
	 * @return
	 */
	public static File getUserFileDir() {
		return getCachesDir("user");
	}

	/**
	 * 获取默认下载存储路径
	 * @param childDir 子目录名
	 * @return 路径:Android/data/file/Download/childDir
	 */
	public static File getDefaultDownloadDir(String childDir) {
		File file;
		File baseDir = getFilesDir(Environment.DIRECTORY_DOWNLOADS);
		file = new File(baseDir, childDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	/**
	 * 获取默认游戏下载目录
	 * @return
	 */
	public static File getDefaultApkDir(){
		return getDefaultDownloadDir("apks");
	}

	/**
	 * 获取gpk解压目录
	 * @param fileName
	 * @return
	 */
	public static File getGpkParseDir(String fileName){
		File file = new File(getCachesDir("gpk_parse"), FileUtil.getFileNameWithoutExtension(fileName));
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	public static File getDownloadDir() {
		return DirManager.getFilesDir("download");
	}

}
