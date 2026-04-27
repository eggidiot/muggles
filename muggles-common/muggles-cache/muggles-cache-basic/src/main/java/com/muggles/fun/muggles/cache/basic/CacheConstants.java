package com.muggles.fun.muggles.cache.basic;

/**
 * 缓存常量
 */
public interface CacheConstants {
	/**
	 * 默认缓存超时时间
	 */
	long TIME_VALUE = 0;
	/**
	 * 默认缓存尺寸
	 */
	long SIZE_VALUE = 0;
	/**
	 * 缓存级别
	 */
	int LEVEL_VALUE = 1;
	/**
	 * 自定义值，表示跟随缓存设置进行默认的缓存键值管理
	 */
	long CUSTOM_VALUE = -1;
}
