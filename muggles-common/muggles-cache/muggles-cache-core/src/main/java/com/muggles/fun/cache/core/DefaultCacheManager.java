package com.muggles.fun.cache.core;

import com.muggles.fun.muggles.cache.basic.ICache;
import com.muggles.fun.muggles.cache.manager.ICacheManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认缓存管理器，负责维护各级缓存实例。
 * <p>level 数值越小，缓存越靠近调用方；例如 L1 本地内存缓存应排在 Redis 等远程缓存之前。
 */
public class DefaultCacheManager implements ICacheManager {

	private final Map<ICache, Integer> cacheLevels = new ConcurrentHashMap<>();

	/**
	 * 当前缓存集合
	 *
	 * @return List<ICache>
	 */
	@Override
	public List<ICache> caches() {
		// 每次返回按 level 排序后的快照，调用方不需要关心注册顺序。
		return cacheLevels.entrySet().stream()
				.sorted(Comparator.comparingInt(Map.Entry::getValue))
				.map(Map.Entry::getKey)
				.toList();
	}

	/**
	 * 注册缓存
	 *
	 * @param cache 缓存实例
	 * @param level 缓存级别
	 * @return int
	 */
	@Override
	public int regCache(ICache cache, int level) {
		if (cache == null) {
			return caches().size();
		}
		cacheLevels.put(cache, level);
		return caches().size();
	}

	/**
	 * 同步多级缓存
	 *
	 * @return 同步时间
	 */
	@Override
	public int sync() {
		List<ICache> caches = new ArrayList<>(caches());
		if (caches.size() < 2) {
			return 0;
		}
		long start = System.currentTimeMillis();
		// 默认以最后一层作为数据源，将其当前内容同步到更靠前的缓存层。
		// 这是基础实现，复杂场景可由具体 CacheManager 接入消息通知或版本号控制。
		ICache source = caches.get(caches.size() - 1);
		for (int i = 0; i < caches.size() - 1; i++) {
			ICache target = caches.get(i);
			source.list().forEach(entry -> target.put(entry.getKey(), entry.getElement().getValue()));
		}
		return (int) (System.currentTimeMillis() - start);
	}
}
