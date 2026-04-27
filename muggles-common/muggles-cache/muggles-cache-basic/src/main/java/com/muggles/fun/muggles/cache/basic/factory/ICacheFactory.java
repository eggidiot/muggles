package com.muggles.fun.muggles.cache.basic.factory;

import com.muggles.fun.muggles.cache.basic.CacheSetting;
import com.muggles.fun.muggles.cache.basic.ICache;

/**
 * 缓存实例工厂
 */
public interface ICacheFactory {

	/**
	 * 获取某级别的缓存对象
	 *
	 * @param setting 配置信息
	 * @return ICache
	 */
	ICache genCache(CacheSetting setting);
}
