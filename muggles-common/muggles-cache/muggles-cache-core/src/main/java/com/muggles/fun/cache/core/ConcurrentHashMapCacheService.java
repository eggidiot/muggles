package com.muggles.fun.cache.core;

import com.muggles.fun.muggles.cache.basic.CacheConstants;
import com.muggles.fun.muggles.cache.basic.CacheElement;
import com.muggles.fun.muggles.cache.basic.CacheEntry;
import com.muggles.fun.muggles.cache.basic.ICacheService;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 基于 {@link ConcurrentHashMap} 的默认内存缓存实现。
 */
public class ConcurrentHashMapCacheService implements ICacheService {

	private static final Object NULL_VALUE = new Object();

	private final ConcurrentMap<Serializable, CacheElement> cache = new ConcurrentHashMap<>();

	@Override
	public <T> T put(Serializable key, T t, Boolean putIfAbsent, Long time, TimeUnit unit) {
		Objects.requireNonNull(key, "cache key must not be null");
		CacheElement newElement = createElement(t, time, unit);
		if (Boolean.TRUE.equals(putIfAbsent)) {
			return putIfAbsentElement(key, t, newElement);
		}
		cache.put(key, newElement);
		return t;
	}

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
				cache.remove(key, oldElement);
				continue;
			}
			return unwrapValue(oldElement.getValue());
		}
	}

	@Override
	public <T> T get(Serializable key, Type type, Callable<T> valueLoader) {
		Objects.requireNonNull(key, "cache key must not be null");
		CacheElement element = getValidElement(key);
		if (element != null) {
			return castValue(unwrapValue(element.getValue()), type);
		}
		if (valueLoader == null) {
			return null;
		}
		T value = loadValue(key, valueLoader);
		put(key, value);
		return value;
	}

	@Override
	public <T> T computeIfAbsent(Serializable key, Long time, TimeUnit unit, Function<Serializable, ? extends T> mappingFunction) {
		Objects.requireNonNull(key, "cache key must not be null");
		Objects.requireNonNull(mappingFunction, "mapping function must not be null");
		while (true) {
			CacheElement oldElement = getValidElement(key);
			if (oldElement != null) {
				return unwrapValue(oldElement.getValue());
			}
			T value = mappingFunction.apply(key);
			CacheElement newElement = createElement(value, time, unit);
			CacheElement racedElement = cache.putIfAbsent(key, newElement);
			if (racedElement == null) {
				return value;
			}
			if (racedElement.isExpire()) {
				cache.remove(key, racedElement);
			}
		}
	}

	@Override
	public void remove(Serializable key) {
		Objects.requireNonNull(key, "cache key must not be null");
		cache.remove(key);
	}

	@Override
	public List<CacheEntry> list() {
		clearExpired();
		List<CacheEntry> entries = new ArrayList<>(cache.size());
		cache.forEach((key, element) -> entries.add(new CacheEntry().setKey(key).setElement(element)));
		return entries;
	}

	@Override
	public boolean clear() {
		cache.clear();
		return true;
	}

	private <T> T putIfAbsentElement(Serializable key, T value, CacheElement newElement) {
		while (true) {
			CacheElement oldElement = cache.putIfAbsent(key, newElement);
			if (oldElement == null) {
				return value;
			}
			if (oldElement.isExpire()) {
				cache.remove(key, oldElement);
				continue;
			}
			return unwrapValue(oldElement.getValue());
		}
	}

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

	private <T> T loadValue(Serializable key, Callable<T> valueLoader) {
		try {
			return valueLoader.call();
		} catch (Exception e) {
			throw new IllegalStateException("load cache value failed, key: " + key, e);
		}
	}

	private <T> CacheElement createElement(T value, Long time, TimeUnit unit) {
		return new CacheElement()
				.setValue(wrapValue(value))
				.setT(System.currentTimeMillis())
				.setTime(toMillis(time, unit));
	}

	private long toMillis(Long time, TimeUnit unit) {
		if (time == null || time == CacheConstants.TIME_VALUE || time == CacheConstants.CUSTOM_VALUE) {
			return CacheConstants.TIME_VALUE;
		}
		if (time < 0) {
			return CacheConstants.TIME_VALUE;
		}
		TimeUnit actualUnit = unit == null ? TimeUnit.MILLISECONDS : unit;
		return actualUnit.toMillis(time);
	}

	private void clearExpired() {
		cache.entrySet().removeIf(entry -> entry.getValue().isExpire());
	}

	private Object wrapValue(Object value) {
		return value == null ? NULL_VALUE : value;
	}

	@SuppressWarnings("unchecked")
	private <T> T unwrapValue(Object value) {
		return value == NULL_VALUE ? null : (T) value;
	}

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
