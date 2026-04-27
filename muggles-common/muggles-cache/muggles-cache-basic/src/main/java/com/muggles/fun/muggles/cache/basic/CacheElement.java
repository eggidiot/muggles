package com.muggles.fun.muggles.cache.basic;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.util.concurrent.Callable;

/**
 * 缓存元素
 */
@Data
@Accessors(chain = true)
public class CacheElement {
	/**
	 * 值
	 */
	Object value;
	/**
	 * 生成时间
	 */
	long t = System.currentTimeMillis();
	/**
	 * 持续时间
	 */
	long time;

	/**
	 * 获取值方法重载
	 *
	 * @param expireCall
	 * @return
	 */
	@SneakyThrows
	public Object getValue(Callable<Object> expireCall) {
		if (isExpire()) {
			expireCall.call();
			return null;
		}
		return getValue();
	}

	/**
	 * 判断当前元素是否过期
	 *
	 * @return boolean
	 */
	public boolean isExpire() {
		return time != CacheConstants.TIME_VALUE && t + time < System.currentTimeMillis();
	}
}
