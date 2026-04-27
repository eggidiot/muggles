package com.muggles.fun.muggles.cache.stats;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.concurrent.atomic.LongAdder;

/**
 * 缓存统计信息实体类
 */
public final class CacheStats implements Serializable {
	/**
	 * 请求缓存总数
	 */
	@Setter
	@Getter
	private LongAdder cacheRequestCount;

	/**
	 * 请求被缓存方法总数
	 */
	@Setter
	@Getter
	private LongAdder cachedMethodRequestCount;

	/**
	 * 请求被缓存方法总耗时(毫秒)
	 */
	@Setter
	@Getter
	private LongAdder cachedMethodRequestTime;

	/**
	 * 无参构造器
	 */
	public CacheStats() {
		this.cacheRequestCount = new LongAdder();
		this.cachedMethodRequestCount = new LongAdder();
		this.cachedMethodRequestTime = new LongAdder();
	}

	/**
	 * 自增请求缓存总数
	 *
	 * @param add 自增数量
	 */
	public void addCacheRequestCount(long add) {
		cacheRequestCount.add(add);
	}

	/**
	 * 自增请求被缓存方法总数
	 *
	 * @param add 自增数量
	 */
	public void addCachedMethodRequestCount(long add) {
		cachedMethodRequestCount.add(add);
	}

	/**
	 * 自增请求被缓存方法总耗时(毫秒)
	 *
	 * @param time 自增数量
	 */
	public void addCachedMethodRequestTime(long time) {
		cachedMethodRequestTime.add(time);
	}

	/**
	 * 获取请求缓存总数
	 *
	 * @return long
	 */
	public long getAndResetCacheRequestCount() {
		long lodValue = cacheRequestCount.longValue();
		cacheRequestCount.reset();
		return lodValue;
	}

	/**
	 * 请求被缓存方法总数
	 *
	 * @return long
	 */
	public long getAndResetCachedMethodRequestCount() {
		long lodValue = cachedMethodRequestCount.longValue();
		cachedMethodRequestCount.reset();
		return lodValue;
	}

	/**
	 * 请求请求被缓存方法总耗时(毫秒)
	 *
	 * @return long
	 */
	public long getAndResetCachedMethodRequestTime() {
		long lodValue = cachedMethodRequestTime.longValue();
		cachedMethodRequestTime.reset();
		return lodValue;
	}

}
