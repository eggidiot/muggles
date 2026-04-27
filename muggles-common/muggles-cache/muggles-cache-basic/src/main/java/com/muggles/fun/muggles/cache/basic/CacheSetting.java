package com.muggles.fun.muggles.cache.basic;

import com.muggles.fun.muggles.cache.CacheConstants;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * 多级缓存配置项
 */
@Data
@Accessors(chain = true)
public class CacheSetting implements Serializable {
	/**
	 * 默认连接符
	 */
	private static final String SPLIT = "-";
	/**
	 * 内部缓存名
	 */
	private String cacheName;
	/**
	 * 缓存层级
	 */
	private int level;
	/**
	 * 缓存有效时间
	 */
	private long expireTime = CacheConstants.TIME_VALUE;
	/**
	 * 缓存时间单位
	 */
	private TimeUnit timeUnit = TimeUnit.MINUTES;
	/**
	 * 缓存初始条数Size
	 */
	private long initialCapacity = CacheConstants.SIZE_VALUE;
	/**
	 * 缓存最大条数Size
	 */
	private long maximumSize = CacheConstants.SIZE_VALUE;
}
