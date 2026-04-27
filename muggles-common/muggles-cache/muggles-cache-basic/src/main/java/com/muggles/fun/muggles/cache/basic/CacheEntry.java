package com.muggles.fun.muggles.cache.basic;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 缓存对象数据结构
 */
@Data
@Accessors(chain = true)
public class CacheEntry {
	/**
	 * 键
	 */
	Serializable key;
	/**
	 * 值
	 */
	CacheElement element;
}
