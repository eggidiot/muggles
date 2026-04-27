package com.muggles.fun.cache.core;

import com.muggles.fun.muggles.cache.CacheConstants;
import com.muggles.fun.muggles.cache.basic.AbstractCache;
import com.muggles.fun.muggles.cache.basic.CacheElement;
import com.muggles.fun.muggles.cache.basic.CacheEntry;
import com.muggles.fun.muggles.cache.basic.CacheSetting;
import com.muggles.fun.muggles.cache.basic.ICache;
import com.muggles.fun.muggles.cache.stats.CacheStats;
import com.muggles.fun.muggles.cache.stats.ICacheStatus;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * 默认单层内存缓存实现，底层基于 {@link ConcurrentHashMap}。
 * <p>该类只负责某一级缓存的真实读写能力，不关心多级缓存的读取顺序、回填策略和整体失效传播。
 * 这些编排逻辑由 {@link DefaultCacheService} 统一处理。
 */
public class DefaultCacheImpl extends AbstractCache implements ICacheStatus, ICacheChainNode {

	/**
	 * ConcurrentHashMap 不允许存储 null，使用内部占位对象区分“缓存了 null”和“缓存未命中”。
	 */
	private static final Object NULL_VALUE = new Object();

	private final ConcurrentMap<Serializable, CacheElement> cache;

	private final CacheStats cacheStats = new CacheStats();

	private ICache previousCache;

	private ICache nextCache;

	/**
	 * 根据缓存配置创建单层内存缓存。
	 * <p>{@link CacheConstants#CUSTOM_VALUE} 表示单次操作跟随当前缓存实例的默认过期时间。
	 *
	 * @param setting 缓存配置，允许为空；为空时使用默认配置
	 */
	public DefaultCacheImpl(CacheSetting setting) {
		super(cacheName(setting), maximumSize(setting), level(setting), expireMillis(setting));
		CacheSetting actualSetting = setting == null ? new CacheSetting() : setting;
		this.cache = new ConcurrentHashMap<>(initialCapacity(actualSetting.getInitialCapacity()));
	}

	/**
	 * 设置上一个缓存节点。
	 *
	 * @param previousCache 上一个缓存节点
	 */
	@Override
	public void setPreviousCache(ICache previousCache) {
		this.previousCache = previousCache;
	}

	/**
	 * 设置下一个缓存节点。
	 *
	 * @param nextCache 下一个缓存节点
	 */
	@Override
	public void setNextCache(ICache nextCache) {
		this.nextCache = nextCache;
	}

	/**
	 * 设置对象放入缓存中
	 *
	 * @param key         键
	 * @param t           值
	 * @param putIfAbsent 如果不存在就i添加
	 * @param time        缓存时间
	 * @param unit        时间单位
	 * @param <T>         数据类型
	 * @return boolean
	 */
	@Override
	public <T> T put(Serializable key, T t, Boolean putIfAbsent, Long time, TimeUnit unit) {
		Objects.requireNonNull(key, "cache key must not be null");
		CacheElement newElement = createElement(t, time, unit);
		if (Boolean.TRUE.equals(putIfAbsent)) {
			return putIfAbsent(key, t, time, unit);
		}
		putElement(key, newElement);
		return t;
	}

	/**
	 * 设置对象放入缓存中
	 *
	 * @param key  键
	 * @param t    值
	 * @param time 缓存时间
	 * @param unit 时间单位
	 * @param <T>  数据类型
	 * @return boolean
	 */
	@Override
	public <T> T putIfAbsent(Serializable key, T t, Long time, TimeUnit unit) {
		Objects.requireNonNull(key, "cache key must not be null");
		CacheElement newElement = createElement(t, time, unit);
		while (true) {
			CacheElement oldElement = cache.putIfAbsent(key, newElement);
			if (oldElement == null) {
				return t;
			}
			if (oldElement.isExpire()) {
				// 过期数据不应阻止本次写入，先按 CAS 方式删除再重试。
				cache.remove(key, oldElement);
				continue;
			}
			return unwrapValue(oldElement.getValue());
		}
	}

	/**
	 * 根据KEY返回缓存中对应的值，并将其返回类型转换成对应类型，如果对应key不存在则调用valueLoader加载数据
	 *
	 * @param key         缓存key
	 * @param type        返回值类型
	 * @param valueLoader 加载缓存的回调方法
	 * @param <T>         Object
	 * @return 缓存key对应的值
	 */
	@Override
	public <T> T get(Serializable key, Type type, Callable<T> valueLoader) {
		Objects.requireNonNull(key, "cache key must not be null");
		cacheStats.addCacheRequestCount(1);
		CacheElement element = getValidElement(key);
		if (element != null) {
			return castValue(unwrapValue(element.getValue()), type);
		}
		T chainValue = getFromPreviousCache(key, type);
		if (chainValue != null || containsKey(previousCache, key)) {
			put(key, chainValue);
			return chainValue;
		}
		if (valueLoader == null) {
			return null;
		}
		// 单层缓存也支持独立的 loader 场景；多级场景通常由 DefaultCacheService 负责加载和写回。
		long start = System.currentTimeMillis();
		T value = loadValue(key, valueLoader);
		cacheStats.addCachedMethodRequestCount(1);
		cacheStats.addCachedMethodRequestTime(System.currentTimeMillis() - start);
		put(key, value);
		return value;
	}

	/**
	 * 删除某个键
	 *
	 * @param key 键
	 */
	@Override
	public void remove(Serializable key) {
		Objects.requireNonNull(key, "cache key must not be null");
		cache.remove(key);
	}

	/**
	 * 列举当前缓存的所有内容
	 *
	 * @return List<String>
	 */
	@Override
	public List<CacheEntry> list() {
		clearExpired();
		List<CacheEntry> entries = new ArrayList<>(cache.size());
		cache.forEach((key, element) -> entries.add(new CacheEntry().setKey(key).setElement(copyElement(element))));
		return entries;
	}

	/**
	 * 获取统计信息
	 *
	 * @return {@link CacheStats}
	 */
	@Override
	public CacheStats getCacheStats() {
		return cacheStats;
	}

	/**
	 * 缓存预估size
	 *
	 * @return long
	 */
	@Override
	public long estimatedSize() {
		clearExpired();
		return cache.size();
	}

	/**
	 * 将缓存元素写入底层容器。
	 * <p>当配置了最大容量时，写入新 key 前会先尝试清理过期数据。
	 *
	 * @param key     缓存 key
	 * @param element 缓存元素
	 */
	private void putElement(Serializable key, CacheElement element) {
		if (maxSize > CacheConstants.SIZE_VALUE && !cache.containsKey(key) && cache.size() >= maxSize) {
			// 当前实现只做轻量级过期清理，不做 LRU/LFU 淘汰；需要严格容量控制时应替换为 Caffeine 等实现。
			clearExpired();
		}
		cache.put(key, element);
	}

	/**
	 * 获取有效缓存元素。
	 * <p>过期元素采用惰性删除策略，避免引入后台清理线程。
	 *
	 * @param key 缓存 key
	 * @return 未过期的缓存元素；不存在或已过期时返回 null
	 */
	private CacheElement getValidElement(Serializable key) {
		CacheElement element = cache.get(key);
		if (element == null) {
			return null;
		}
		if (element.isExpire()) {
			cache.remove(key, element);
			return null;
		}
		return element;
	}

	/**
	 * 从写入顺序的上一个缓存节点读取数据。
	 * <p>读取顺序是写入顺序的逆序，因此当前节点 miss 后应沿 previous 方向继续读取。
	 *
	 * @param key  缓存 key
	 * @param type 目标类型
	 * @param <T>  目标值类型
	 * @return 上一个缓存节点中的值
	 */
	private <T> T getFromPreviousCache(Serializable key, Type type) {
		if (previousCache == null) {
			return null;
		}
		return previousCache.get(key, type);
	}

	/**
	 * 判断指定缓存层是否包含某个 key。
	 * <p>当前 ICache 未提供 contains 方法，因此通过 list 临时判断；后续建议提升为 ICache 原生能力。
	 *
	 * @param cache 缓存层
	 * @param key   缓存 key
	 * @return true 表示缓存层包含该 key
	 */
	private boolean containsKey(ICache cache, Serializable key) {
		if (cache == null) {
			return false;
		}
		for (CacheEntry entry : cache.list()) {
			if (Objects.equals(entry.getKey(), key)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 创建缓存元素，并将业务值包装为内部可存储结构。
	 *
	 * @param value 业务值
	 * @param time  缓存时间
	 * @param unit  时间单位
	 * @param <T>   业务值类型
	 * @return 缓存元素
	 */
	private <T> CacheElement createElement(T value, Long time, TimeUnit unit) {
		return new CacheElement()
				.setValue(wrapValue(value))
				.setT(System.currentTimeMillis())
				.setTime(resolveExpireMillis(time, unit));
	}

	/**
	 * 复制缓存元素用于外部展示。
	 *
	 * @param element 内部缓存元素
	 * @return 已解包 null 占位值的缓存元素副本
	 */
	private CacheElement copyElement(CacheElement element) {
		// list 返回的是外部可见快照，不能泄漏内部 NULL_VALUE 占位对象。
		return new CacheElement()
				.setValue(unwrapValue(element.getValue()))
				.setT(element.getT())
				.setTime(element.getTime());
	}

	/**
	 * 解析单次写入的过期时间。
	 *
	 * @param time 缓存时间
	 * @param unit 时间单位
	 * @return 毫秒级过期时间，0 表示不过期
	 */
	private long resolveExpireMillis(Long time, TimeUnit unit) {
		// null/CUSTOM_VALUE 表示沿用缓存实例默认 TTL；0 表示不过期。
		if (time == null || time == CacheConstants.CUSTOM_VALUE) {
			return expireTime;
		}
		return toMillis(time, unit);
	}

	/**
	 * 获取配置中的缓存名称。
	 *
	 * @param setting 缓存配置
	 * @return 缓存名称
	 */
	private static String cacheName(CacheSetting setting) {
		return setting == null ? null : setting.getCacheName();
	}

	/**
	 * 获取配置中的最大缓存数量。
	 *
	 * @param setting 缓存配置
	 * @return 最大缓存数量，0 表示不限制
	 */
	private static long maximumSize(CacheSetting setting) {
		return setting == null ? CacheConstants.SIZE_VALUE : setting.getMaximumSize();
	}

	/**
	 * 获取配置中的缓存层级。
	 *
	 * @param setting 缓存配置
	 * @return 缓存层级
	 */
	private static int level(CacheSetting setting) {
		return setting == null ? CacheConstants.LEVEL_VALUE : setting.getLevel();
	}

	/**
	 * 获取配置中的默认过期时间。
	 *
	 * @param setting 缓存配置
	 * @return 毫秒级过期时间，0 表示不过期
	 */
	private static long expireMillis(CacheSetting setting) {
		if (setting == null) {
			return CacheConstants.TIME_VALUE;
		}
		return toMillis(setting.getExpireTime(), setting.getTimeUnit());
	}

	/**
	 * 将时间配置转换为毫秒。
	 *
	 * @param time 时间值
	 * @param unit 时间单位
	 * @return 毫秒值
	 */
	private static long toMillis(long time, TimeUnit unit) {
		if (time <= CacheConstants.TIME_VALUE) {
			return CacheConstants.TIME_VALUE;
		}
		TimeUnit actualUnit = unit == null ? TimeUnit.MILLISECONDS : unit;
		return actualUnit.toMillis(time);
	}

	/**
	 * 解析 ConcurrentHashMap 初始容量。
	 *
	 * @param initialCapacity 配置容量
	 * @return 可用于 ConcurrentHashMap 构造器的容量
	 */
	private int initialCapacity(long initialCapacity) {
		if (initialCapacity <= CacheConstants.SIZE_VALUE) {
			return 16;
		}
		return initialCapacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) initialCapacity;
	}

	/**
	 * 清理所有已过期缓存元素。
	 */
	private void clearExpired() {
		cache.entrySet().removeIf(entry -> entry.getValue().isExpire());
	}

	/**
	 * 调用缓存加载函数。
	 *
	 * @param key         缓存 key
	 * @param valueLoader 加载函数
	 * @param <T>         加载值类型
	 * @return 加载结果
	 */
	private <T> T loadValue(Serializable key, Callable<T> valueLoader) {
		try {
			return valueLoader.call();
		} catch (Exception e) {
			throw new IllegalStateException("load cache value failed, key: " + key, e);
		}
	}

	/**
	 * 包装业务值。
	 *
	 * @param value 业务值
	 * @return 可写入 ConcurrentHashMap 的值
	 */
	private Object wrapValue(Object value) {
		return value == null ? NULL_VALUE : value;
	}

	/**
	 * 解包内部存储值。
	 *
	 * @param value 内部存储值
	 * @param <T>   业务值类型
	 * @return 业务值
	 */
	@SuppressWarnings("unchecked")
	private <T> T unwrapValue(Object value) {
		return value == NULL_VALUE ? null : (T) value;
	}

	/**
	 * 按调用方声明类型转换缓存值。
	 *
	 * @param value 缓存值
	 * @param type  目标类型
	 * @param <T>   目标值类型
	 * @return 转换后的缓存值
	 */
	@SuppressWarnings("unchecked")
	private <T> T castValue(Object value, Type type) {
		if (value == null || type == null || type == Object.class) {
			return (T) value;
		}
		if (type instanceof Class<?> clazz) {
			return (T) wrapPrimitiveType(clazz).cast(value);
		}
		return (T) value;
	}

	/**
	 * 将基础类型转换为对应包装类型。
	 *
	 * @param type 原始类型
	 * @return 可执行 Class#cast 的类型
	 */
	private Class<?> wrapPrimitiveType(Class<?> type) {
		if (!type.isPrimitive()) {
			return type;
		}
		if (type == int.class) {
			return Integer.class;
		}
		if (type == long.class) {
			return Long.class;
		}
		if (type == boolean.class) {
			return Boolean.class;
		}
		if (type == double.class) {
			return Double.class;
		}
		if (type == float.class) {
			return Float.class;
		}
		if (type == short.class) {
			return Short.class;
		}
		if (type == byte.class) {
			return Byte.class;
		}
		if (type == char.class) {
			return Character.class;
		}
		return type;
	}
}
