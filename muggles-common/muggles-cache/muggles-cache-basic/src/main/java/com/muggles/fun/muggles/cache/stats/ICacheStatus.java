package com.muggles.fun.muggles.cache.stats;

/**
 * 缓存统计接口
 */
public interface ICacheStatus {
	/**
	 * 获取统计信息
	 *
	 * @return {@link CacheStats}
	 */
	CacheStats getCacheStats();

	/**
	 * 缓存预估size
	 *
	 * @return long
	 */
	long estimatedSize();
}
