package com.muggles.fun.cache.core;

import com.muggles.fun.muggles.cache.basic.CacheEntry;
import com.muggles.fun.muggles.cache.basic.ICache;
import com.muggles.fun.muggles.cache.basic.ICacheService;
import com.muggles.fun.muggles.cache.manager.ICacheManager;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 面向用户的默认缓存服务，内部通过 {@link ICacheManager} 聚合多个缓存实现。
 * <p>调用方只感知这一套 {@link ICacheService} 接口；缓存层数、读取顺序和回填策略都属于内部实现细节。
 * <p>读路径不做同步回填，避免读取操作产生副作用；多级缓存同步由 {@link ICacheManager#sync()} 或外部异步机制处理。
 */
public class DefaultCacheService implements ICacheService {

	private final ICacheManager cacheManager;

	public DefaultCacheService(ICacheManager cacheManager) {
		this.cacheManager = Objects.requireNonNull(cacheManager, "cache manager must not be null");
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
		T result = t;
		// level 越高表示越远地缓存；写入从远地到近地，避免近地先写成功后远地写失败造成不一致。
		for (ICache cache : reverseCaches()) {
			result = cache.put(key, t, putIfAbsent, time, unit);
		}
		return result;
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
		// 先从多级缓存整体读取，避免某一层缺失时重复写入已存在于其它层的数据。
		T exists = get(key, Object.class);
		if (exists != null) {
			return exists;
		}
		for (ICache cache : reverseCaches()) {
			cache.putIfAbsent(key, t, time, unit);
		}
		return t;
	}

	/**
	 * 获取对象不存在则根据mapping函数计算值
	 *
	 * @param key             键
	 * @param time            缓存时间
	 * @param unit            时间单位
	 * @param mappingFunction 计算函数
	 * @param <T>             类型
	 * @return T
	 */
	@Override
	public <T> T computeIfAbsent(Serializable key, Long time, TimeUnit unit, Function<Serializable, ? extends T> mappingFunction) {
		// computeIfAbsent 的 loader 只执行一次，计算结果再统一写入所有缓存层。
		T value = get(key, Object.class);
		if (value != null) {
			return value;
		}
		T loaded = mappingFunction.apply(key);
		put(key, loaded, false, time, unit);
		return loaded;
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
		List<ICache> writeOrderedCaches = reverseCaches();
		if (writeOrderedCaches.isEmpty()) {
			return loadIfNecessary(key, valueLoader);
		}
		buildCacheChain(writeOrderedCaches);
		ICache readEntry = writeOrderedCaches.getLast();
		// 读取顺序是写入顺序的逆序，因此从写入链表尾节点开始读取。
		T value = readEntry.get(key, type);
		if (value != null || containsKey(readEntry, key)) {
			return value;
		}
		if (valueLoader == null) {
			return null;
		}
		// 全部未命中时才调用业务 loader，并将结果写入所有缓存层。
		T loaded = loadValue(key, valueLoader);
		put(key, loaded);
		return loaded;
	}

	/**
	 * 删除某个键
	 *
	 * @param key 键
	 */
	@Override
	public void remove(Serializable key) {
		// 删除需要传播到所有缓存层，避免高层或低层残留旧值。
		for (ICache cache : cacheManager.caches()) {
			cache.remove(key);
		}
	}

	/**
	 * 列举当前缓存的所有内容
	 *
	 * @return List<String>
	 */
	@Override
	public List<CacheEntry> list() {
		List<CacheEntry> entries = new ArrayList<>();
		for (ICache cache : cacheManager.caches()) {
			entries.addAll(cache.list());
		}
		return entries;
	}

	/**
	 * 清空当前缓存
	 *
	 * @return List<String>
	 */
	@Override
	public boolean clear() {
		for (ICache cache : cacheManager.caches()) {
			cache.list().forEach(entry -> cache.remove(entry.getKey()));
		}
		return true;
	}

	/**
	 * 获取反向缓存集合。
	 * <p>默认 manager 返回近地到远地的顺序；写入需要从远地到近地执行。
	 *
	 * @return 远地到近地的缓存集合
	 */
	private List<ICache> reverseCaches() {
		List<ICache> caches = new ArrayList<>(cacheManager.caches());
		Collections.reverse(caches);
		return caches;
	}

	/**
	 * 按写入顺序构建缓存双向责任链。
	 * <p>只对支持 {@link ICacheChainNode} 的缓存节点设置 previous/next；不支持的缓存实现会保持独立行为。
	 *
	 * @param caches 远地到近地排序的缓存集合
	 */
	private void buildCacheChain(List<ICache> caches) {
		for (int i = 0; i < caches.size(); i++) {
			ICache cache = caches.get(i);
			if (cache instanceof ICacheChainNode chainNode) {
				chainNode.setPreviousCache(i > 0 ? caches.get(i - 1) : null);
				chainNode.setNextCache(i + 1 < caches.size() ? caches.get(i + 1) : null);
			}
		}
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
		// TODO 后续可在 ICache 增加 contains(key)，避免通过 list 扫描判断 null 命中。
		for (CacheEntry entry : cache.list()) {
			if (Objects.equals(entry.getKey(), key)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 在没有缓存节点时按需调用加载函数。
	 *
	 * @param key         缓存 key
	 * @param valueLoader 加载函数
	 * @param <T>         加载值类型
	 * @return 加载结果
	 */
	private <T> T loadIfNecessary(Serializable key, Callable<T> valueLoader) {
		if (valueLoader == null) {
			return null;
		}
		return loadValue(key, valueLoader);
	}

	/**
	 * 调用用户提供的缓存加载函数。
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
}
