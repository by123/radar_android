package com.brotherhood.o2o.cache.impl;

import android.util.Log;

import com.brotherhood.o2o.cache.Cache;
import com.brotherhood.o2o.manager.DirManager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
/**
 * 本地文件缓存（byte）
 * @author Swei.Jiang 
 * @date 2013-7-31
 */
public class FileCache implements Cache<String, byte[]> {

	public static final String FILE_DIR = "FileCache";
	private File dirFile;

	public FileCache(String dirPath) {
		if (dirPath == null) {
			this.dirFile = DirManager.getFilesDir(FILE_DIR);
		} else {
			this.dirFile = new File(dirPath);
		}
		cleanupSimple();
	}

	/**
	 * 文件数量超过1000个时及时清理文件缓存
	 */
	private void cleanupSimple() {
		final int maxNumFiles = 1000;
		final int numFilesToDelete = 50;

		File[] children = dirFile.listFiles();
		if (children != null) {
			if (children.length > maxNumFiles) {
				for (int i = children.length - 1, m = i - numFilesToDelete; i > m; i--) {
					Log.d(FILE_DIR, "  deleting: " + children[i].getName());
					children[i].delete();
				}
			}
		}
	}

	public File getFile(String hash) {
		return new File(dirFile, hash);
	}

	@Override
	public boolean put(String key, byte[] value) {
		File f = getFile(key);
		OutputStream fos = null;
		try {
			fos = new BufferedOutputStream(new FileOutputStream(f));
			fos.write(value);
			fos.flush();
			return true;
		} catch (IOException e) {
			delFile(f);
			return false;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public byte[] get(String key) {
		File f = getFile(key);
		if (f.exists()) {
			try {
				FileInputStream fin = new FileInputStream(f);
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[4096];
				int len = 0;
				while ((len = fin.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}
				fin.close();
				return outStream.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public byte[] get(String key, long time) {
		File f = getFile(key);
		if (!f.exists()) {
			return null;
		}
		if (time == -1 || System.currentTimeMillis() - f.lastModified() < time) {
			return get(key);
		}
		return null;
	}

	@Override
	public void clear() {
		delFile(dirFile);
	}

	@Override
	public void remove(String key) {
		File f = getFile(key);

		if (f.exists()) {
			f.delete();
		}
	}

	private void delFile(File path) {
		if (path.isFile()) {
			path.delete();
		} else if (path.isDirectory()) {
			String[] children = path.list();
			for (String string : children) {
				File child = new File(path, string);
				delFile(child);
			}
			path.delete();
		}
	}

	@Override
	public String getPath(String k) {
		return getFile(k).getAbsolutePath();
	}

}
