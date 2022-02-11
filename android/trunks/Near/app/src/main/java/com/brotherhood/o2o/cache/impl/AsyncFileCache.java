package com.brotherhood.o2o.cache.impl;

import com.brotherhood.o2o.cache.AsyncCache;
import com.brotherhood.o2o.task.TaskExecutor;
import com.brotherhood.o2o.util.MD5;

import java.io.File;
/**
 * 增加异步写入、删除、清空
 * @author Swei.Jiang 
 * @date 2013-7-31
 */
public class AsyncFileCache extends FileCache implements AsyncCache<String, byte[]> {

	public AsyncFileCache(String dirPath) {
		super(dirPath);
	}
	
	@Override
	public File getFile(String hash) {
		return super.getFile(MD5.md5sum(hash));
	}

	@Override
	public void asyncPut(final String key, final byte[] value) {
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
