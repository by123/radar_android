package com.brotherhood.o2o.cache;

/**
 * 增加异步缓存处理操作
 * @author Swei.Jiang 
 * @date 2013-7-31
 * @param <K>
 * @param <V>
 */
public interface AsyncCache<K,V> extends Cache<K,V>{
	/**
	 * 异步保存缓存
	 * @param key
	 * @param value
	 * @return
	 */
	void asyncPut(K key, V value);


	/**
	 * 异步移除缓存
	 * @param key
	 */
	void asyncRemove(K key);
	/**
	 * 异步清理缓存
	 */
	void asyncClear();
}
