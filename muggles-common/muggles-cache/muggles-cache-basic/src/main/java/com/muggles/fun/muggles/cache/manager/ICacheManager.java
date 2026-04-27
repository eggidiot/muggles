package com.muggles.fun.muggles.cache.manager;


import com.muggles.fun.muggles.cache.basic.ICache;

import java.util.List;

/**
 * 缓存管理接口
 */
public interface ICacheManager {
	/**
	 * 当前缓存集合
	 *
	 * @return List<ICache>
	 */
	List<ICache> caches();

	/**
	 * 注册缓存
	 *
	 * @param cache 缓存实例
	 * @param level 缓存级别
	 * @return int
	 */
	int regCache(ICache cache, int level);

	/**
	 * 同步多级缓存
	 *
	 * @return 同步时间
	 */
	int sync();
}
