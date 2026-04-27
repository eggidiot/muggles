package com.muggles.fun.cache.core.config;

import com.muggles.fun.cache.core.aop.MuggleCacheAspect;
import com.muggles.fun.cache.core.DefaultCacheFactory;
import com.muggles.fun.cache.core.DefaultCacheManager;
import com.muggles.fun.cache.core.DefaultCacheService;
import com.muggles.fun.muggles.cache.CacheConstants;
import com.muggles.fun.muggles.cache.basic.CacheSetting;
import com.muggles.fun.muggles.cache.basic.factory.ICacheFactory;
import com.muggles.fun.muggles.cache.basic.ICacheService;
import com.muggles.fun.muggles.cache.manager.ICacheManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 缓存核心模块默认配置。
 * <p>默认只注册一个内存级缓存；如果业务侧提供自定义 {@link ICacheManager} 或 {@link ICacheService}，
 * Spring 会优先使用业务侧 Bean。
 */
@AutoConfiguration
public class CacheCoreAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(ICacheFactory.class)
	public ICacheFactory cacheFactory() {
		// 默认缓存层使用 ConcurrentHashMap，适合作为最小可用实现和本地一级缓存。
		return new DefaultCacheFactory();
	}

	@Bean
	@ConditionalOnMissingBean(ICacheManager.class)
	public ICacheManager cacheManager(ICacheFactory cacheFactory) {
		ICacheManager cacheManager = new DefaultCacheManager();
		// 默认注册一个 L1 内存缓存；多级缓存可继续向 manager 注册更多 ICache 实例。
		cacheManager.regCache(cacheFactory.genCache(defaultMemoryCacheSetting()), CacheConstants.LEVEL_VALUE);
		return cacheManager;
	}

	@Bean
	@ConditionalOnMissingBean(ICacheService.class)
	public ICacheService cacheService(ICacheManager cacheManager) {
		return new DefaultCacheService(cacheManager);
	}

	@Bean
	@ConditionalOnMissingBean(MuggleCacheAspect.class)
	public MuggleCacheAspect muggleCacheAspect(ICacheService cacheService) {
		return new MuggleCacheAspect(cacheService);
	}

	/**
	 * 创建默认内存缓存配置。
	 *
	 * @return 默认缓存配置
	 */
	private CacheSetting defaultMemoryCacheSetting() {
		// TIME_VALUE 表示不过期，SIZE_VALUE 表示不限制容量；生产环境可通过自定义 Bean 覆盖。
		return new CacheSetting()
				.setCacheName("muggles-memory")
				.setLevel(CacheConstants.LEVEL_VALUE)
				.setExpireTime(CacheConstants.TIME_VALUE)
				.setMaximumSize(CacheConstants.SIZE_VALUE);
	}
}
