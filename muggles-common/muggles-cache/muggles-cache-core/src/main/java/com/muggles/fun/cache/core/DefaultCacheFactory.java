package com.muggles.fun.cache.core;

import com.muggles.fun.muggles.cache.basic.CacheSetting;
import com.muggles.fun.muggles.cache.basic.ICache;
import com.muggles.fun.muggles.cache.basic.factory.ICacheFactory;

/**
 * 默认内存缓存工厂。
 * <p>该工厂只创建基于 ConcurrentHashMap 的缓存层；后续 Redis、Caffeine 等实现可以提供自己的 ICacheFactory。
 */
public class DefaultCacheFactory implements ICacheFactory {

	/**
	 * 获取某级别的缓存对象
	 *
	 * @param setting 配置信息
	 * @return ICache
	 */
	@Override
	public ICache genCache(CacheSetting setting) {
		return new DefaultCacheImpl(setting);
	}
}
