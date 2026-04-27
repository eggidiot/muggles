package com.muggles.fun.cache.core.config;

import com.muggles.fun.cache.core.ConcurrentHashMapCacheService;
import com.muggles.fun.muggles.cache.basic.ICacheService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 缓存核心模块默认配置。
 */
@AutoConfiguration
public class CacheCoreAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(ICacheService.class)
	public ICacheService cacheService() {
		return new ConcurrentHashMapCacheService();
	}
}
