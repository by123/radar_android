package com.brotherhood.o2o.cache;


import com.brotherhood.o2o.cache.impl.AsyncFileCache;
import com.brotherhood.o2o.cache.impl.FileCache;
import com.brotherhood.o2o.manager.DirManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * 缓存管理单例，默认带http缓存，可设置其它缓存
 * @author Swei.Jiang 
 * @date 2013-7-31
 */
public class CacheManager {

	/** 默认的过期时间半天*/
	public static final long DEFAULT_UPDATE_TIME = 1 * 12 * 3600 * 1000;

	private static AsyncCache<String, byte[]> cache = null;

	/** 可设置cache路径的实例*/
	private Cache<String, byte[]> definCache = null;

	public void setDefinCache(Cache<String, byte[]> definCache) {
		this.definCache = definCache;
	}

	private CacheManager() {
		if (cache == null) {
			cache = new AsyncFileCache(DirManager.getCachesDir("http").getAbsolutePath());
		}
	}

	private static class SingletonHolder {
		private static final CacheManager single = new CacheManager();
	}

	/**
	 * 获取默认缓存实例
	 * 
	 * @return
	 */
	public static CacheManager getInstance() {
		return SingletonHolder.single;
	}

	/**
	 * 配置个�?化缓存对�?
	 * 
	 * @param dirPath 缓存文件根目�?
	 * @return
	 */
	public static CacheManager createDefaultCacheDir(String dirPath) {
		CacheManager defaultCache = new CacheManager();
		defaultCache.definCache = new FileCache(dirPath);
		return defaultCache;
	}

	/**
	 * 写入缓存
	 * @param key
	 * @param value
	 * @notice 如果是自己配置的缓存对象则写入自己配置的缓存对象�?
	 */
	public void putCache(String key, byte[] value) {
		if (definCache != null) {
			definCache.put(key, value);
		} else {
			cache.put(key, value);
		}
	}

	/**
	 * 读取缓存
	 * @param key
	 * @return
	 * @notice 如果是自己配置的缓存对象则写入自己配置的缓存对象�?
	 */
	public byte[] getCache(String key) {
		if (definCache != null) {
			return definCache.get(key);
		} else {
			return cache.get(key);
		}

	}


	public Object getCacheToObject(String key) {
		byte[] bytes = getCache(key);
		if (bytes == null){
			return null;
		}
		Object obj = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
			ObjectInputStream ois = new ObjectInputStream (bis);
			obj = ois.readObject();
			ois.close();
			bis.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return obj;
	}

	/**
	 * 读取缓存
	 * @param key
	 * @param time -1则取默认过期时间
	 * @return
	 * @notice 如果是自己配置的缓存对象则是操作自己配置�?
	 */
	public byte[] getCache(String key, long time) {
		if (time == -1) {
			time = DEFAULT_UPDATE_TIME;
		}
		if (definCache != null) {
			return definCache.get(key, time);
		} else {
			return cache.get(key, time);
		}
	}

	/**
	 * 获取缓存路径,比如�?��缓存文件路径
	 * @param key 
	 * @return
	 */
	public String getCachePath(String key) {
		if (definCache != null) {
			return definCache.getPath(key);
		} else {
			return cache.getPath(key);
		}
	}

	/**
	 * 清空缓存
	 */
	public void clear() {
		if (definCache != null) {
			definCache.clear();
		} else {
			cache.clear();
		}
	}
}
