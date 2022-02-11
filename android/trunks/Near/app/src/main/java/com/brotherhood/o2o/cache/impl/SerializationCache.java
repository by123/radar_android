package com.brotherhood.o2o.cache.impl;

import com.brotherhood.o2o.cache.AsyncCache;
import com.brotherhood.o2o.task.TaskExecutor;
import com.brotherhood.o2o.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @param <T>
 */
public class SerializationCache< T extends Serializable> implements AsyncCache<String, T> {
	private File dir = null;
	public SerializationCache(File dir) {
		this.dir = dir;
	}
	@Override
	public boolean put(String key, T value) {
		if (key == null || null == value) {
			return false;
		}
		FileOutputStream fout = null;
        ObjectOutputStream oout = null;
		try {
			File f = new File(dir, key);
			if (!f.exists()) {
				f.createNewFile();
			}
			fout = new FileOutputStream(f);
			oout = new ObjectOutputStream(fout);
			oout.writeObject(value);
			oout.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fout != null) {
				try {
					fout.close();
					oout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(String key) {
		if (key == null){
			return null;
		}
		 File f = new File (dir , key);
		 
		 if((!f.exists () || !f.isFile ())) return null;
		 FileInputStream fin = null;
         ObjectInputStream obj = null;
		try {
			fin = new FileInputStream(f);
			obj = new ObjectInputStream(fin);
			return (T) obj.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fin != null) {
				try {
					fin.close();
					obj.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@Override
	public T get(String key, long time) {
		 File f = new File (dir , key);
			if (!f.exists()) {
				return null;
			}
			if (time == -1 || System.currentTimeMillis() - f.lastModified() < time) {
				return get(key);
			}
			return null;
	}

	@Override
	public void remove(String key) {
		 File f = new File (dir , key);
		 if (f.exists()) {
			 f.delete();
		 }
	}

	@Override
	public void clear() {
		FileUtil.deleteDir(dir);
	}

	@Override
	public String getPath(String k) {
		File f = new File(dir , k);
		return f.getAbsolutePath();
	}

	@Override
	public void asyncPut(final String key, final T value) {
		TaskExecutor.executeTask(new Runnable() {

			@Override
			public void run() {
				put(key, value);
			}
		});
	}

	@Override
	public void asyncRemove(final String key) {
		TaskExecutor.executeTask(new Runnable() {

			@Override
			public void run() {
				remove(key);
			}
		});
	}

	@Override
	public void asyncClear() {
		TaskExecutor.executeTask(new Runnable() {
			@Override
			public void run() {
				clear();
			}
		});
	}
	


}
