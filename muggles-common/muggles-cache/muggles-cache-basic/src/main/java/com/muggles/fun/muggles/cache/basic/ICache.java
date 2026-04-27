package com.muggles.fun.muggles.cache.basic;


import com.muggles.fun.muggles.cache.CacheConstants;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 缓存的顶级接口
 */
public interface ICache {
	/**
	 * 设置对象放入缓存中
	 *
	 * @param key 键
	 * @param t   值
	 * @param <T> 数据类型
	 * @return boolean
	 */
	default <T> T put(Serializable key, T t) {
		return put(key, t, false);
	}

	/**
	 * 设置对象放入缓存中
	 *
	 * @param key         键
	 * @param t           值
	 * @param putIfAbsent 如果不存在就添加
	 * @param <T>         数据类型
	 * @return boolean
	 */
	default <T> T put(Serializable key, T t, Boolean putIfAbsent) {
		return put(key, t, putIfAbsent, CacheConstants.CUSTOM_VALUE, TimeUnit.MINUTES);
	}

	/**
	 * 设置对象放入缓存中
	 *
	 * @param key  键
	 * @param t    值
	 * @param time 缓存时间
	 * @param unit 时间单位
	 * @param <T>  数据类型
	 * @return boolean
	 */
	default <T> T put(Serializable key, T t, Long time, TimeUnit unit) {
		return put(key, t, false, time, unit);
	}

	/**
	 * 设置对象放入缓存中
	 *
	 * @param key         键
	 * @param t           值
	 * @param putIfAbsent 如果不存在就i添加
	 * @param time        缓存时间
	 * @param unit        时间单位
	 * @param <T>         数据类型
	 * @return boolean
	 */
	<T> T put(Serializable key, T t, Boolean putIfAbsent, Long time, TimeUnit unit);

	/**
	 * 设置对象放入缓存中
	 *
	 * @param key  键
	 * @param t    值
	 * @param time 缓存时间
	 * @param unit 时间单位
	 * @param <T>  数据类型
	 * @return boolean
	 */
	<T> T putIfAbsent(Serializable key, T t, Long time, TimeUnit unit);

	/**
	 * 根据键和类型获取对象
	 *
	 * @param key  键
	 * @param type 需要获取对象的类型
	 * @param <T>  数据类型
	 * @return T
	 */
	default <T> T get(Serializable key, Type type) {
		return get(key, type, null);
	}

	/**
	 * 根据键和类型获取对象
	 *
	 * @param key 键
	 * @return Object
	 */
	default Object get(Serializable key) {
		return get(key, Object.class);
	}

	/**
	 * 根据KEY返回缓存中对应的值，并将其返回类型转换成对应类型，如果对应key不存在则调用valueLoader加载数据
	 *
	 * @param key         缓存key
	 * @param type        返回值类型
	 * @param valueLoader 加载缓存的回调方法
	 * @param <T>         Object
	 * @return 缓存key对应的值
	 */
	<T> T get(Serializable key, Type type, Callable<T> valueLoader);

	/**
	 * 删除某个键
	 *
	 * @param key 键
	 */
	void remove(Serializable key);

	/**
	 * 列举当前缓存的所有内容
	 *
	 * @return List<String>
	 */
	List<CacheEntry> list();
}
