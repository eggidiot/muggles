package com.muggles.fun.muggles.cache.basic.anno;

import java.lang.annotation.*;

/**
 * 同时使用多个缓存注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MuggleCaching {
	/**
	 * 可缓存配置
	 *
	 * @return FlineCacheable[]
	 */
	MuggleCacheable[] cacheable() default {};

	/**
	 * 这是缓存
	 *
	 * @return FlineCachePut[]
	 */
	MuggleCachePut[] put() default {};

	/**
	 * 淘汰缓存
	 *
	 * @return FlineCacheEvict[]
	 */
	MuggleCacheEvict[] evict() default {};
}
