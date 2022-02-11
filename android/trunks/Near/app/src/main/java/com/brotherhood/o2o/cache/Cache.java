package com.brotherhood.o2o.cache;
/**
 * 
 * @author Swei.Jiang 
 * @date 2013-7-31
 */
public interface Cache<K, V> {
	/**
	 * 增加缓存
	 * @param key
	 * @param value
	 * @return
	 */
	boolean put(K key, V value);
	/**
	 * 获取缓存
	 * @param key
	 * @return
	 */
	V get(K key);
	/**
	 * 带过期时间的缓存
	 * @param key
	 * @param time -1 表示永不过期
	 * @return
	 */
	V get(K key, long time);
	/**
	 * 移除缓存
	 * @param key
	 */
	void remove(K key);
	/**
	 * 清理缓存
	 */
	void clear();
	/**
	 * 获取缓存路径
	 * @param k
	 * @return
	 */
	String getPath(K k);

}
