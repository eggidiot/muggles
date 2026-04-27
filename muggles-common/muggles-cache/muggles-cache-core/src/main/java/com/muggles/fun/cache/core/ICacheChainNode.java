package com.muggles.fun.cache.core;

import com.muggles.fun.muggles.cache.basic.ICache;

/**
 * 缓存责任链节点。
 * <p>该接口属于 core 内部编排能力，用于把多个 {@link ICache} 串成双向链路。
 * <p>链路按写入顺序组织；读取时从写入链表尾节点开始，沿 previous 方向读取。
 */
public interface ICacheChainNode {

	/**
	 * 设置上一个缓存节点。
	 *
	 * @param previousCache 上一个缓存节点
	 */
	void setPreviousCache(ICache previousCache);

	/**
	 * 设置下一个缓存节点。
	 *
	 * @param nextCache 下一个缓存节点
	 */
	void setNextCache(ICache nextCache);
}
