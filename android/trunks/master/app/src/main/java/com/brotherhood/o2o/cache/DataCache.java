package com.brotherhood.o2o.cache;

import com.brotherhood.o2o.manager.DirManager;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.utils.FileUtil;
import com.brotherhood.o2o.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * This class holds our data caches (memory and disk).
 */
public class DataCache {
	public DataCache() {
		mCacheFile = DirManager.getCachesDir("data");
	}

	private File mCacheFile = null;

	/**
	 * 数据对象加入缓存
	 *
	 * @param key
	 * @param data
	 */
	public void addDataToDiskCache(String key, Object data) {
		if (key == null || null == data || mCacheFile == null) {
			return;
		}
		String path = mCacheFile.getAbsolutePath() + File.separator + key;
		try {
			File fiel = new File(path);
			if (!fiel.exists()) {
				fiel.createNewFile();
			}
			FileOutputStream out = new FileOutputStream(fiel.getPath());
			ObjectOutputStream p = new ObjectOutputStream(out);
			p.writeObject(data);
			p.flush();
			out.close();
			p.close();
		} catch (IOException e) {
			LogManager.e(e);

		}

	}

	/**
	 * @param key
	 * @return
	 */
	public Object getDataFromDiskCache(String key) {
		if (key != null && mCacheFile != null) {
			FileInputStream in = null;
			ObjectInputStream p = null;
			try {
				String path = mCacheFile.getAbsolutePath() + File.separator + key;
				File file = new File(path);
				if (file.exists()) {
					in = new FileInputStream(path);
					p = new ObjectInputStream(in);
					return p.readObject();
				}
			} catch (FileNotFoundException e) {
				LogManager.e(e);
			} catch (StreamCorruptedException e) {
				LogManager.e(e);
			} catch (IOException e) {
				LogManager.e(e);
			} catch (ClassNotFoundException e) {
				LogManager.e(e);
			} finally {
				Utils.closeCloseable(in);
				Utils.closeCloseable(p);
			}
		}
		return null;
	}

	public void clearCaches() {
		if (null != mCacheFile) {
			if (mCacheFile.exists()) {
				FileUtil.deleteFile(mCacheFile.getAbsolutePath());
			}
		}
	}

}
