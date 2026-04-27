/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.muggles.fun.muggles.cache.basic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * Cache 接口的抽象实现类，对公共的方法做了一写实现，如是否允许存NULL值
 * <p>如果允许为NULL值，则需要在内部将NULL替换成缓存实现自己的占位对象
 * *
 */
@RequiredArgsConstructor
public abstract class AbstractCache implements ICache {

	/**
	 * 缓存名称
	 */
	@Getter
	protected final String name;

	/**
	 * 缓存key最大数量
	 */
	@Getter
	protected final long maxSize;
	/**
	 * 缓存层级
	 */
	@Getter
	protected final int level;
	/**
	 * 默认过期时间
	 */
	@Getter
	protected final long expireTime;


}
