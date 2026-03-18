package com.muggles.fun.tools.core.bean;

import cn.hutool.core.util.ReflectUtil;
import lombok.Data;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;

import java.lang.invoke.MethodHandles;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态代理对象（ByteBuddy实现），推荐只代理简单的pojo。
 * 功能对标 {@link DynamicBean}（cglib实现）。
 */
@Data
public class DynamicBuddy<T> {
	/**
	 * 目标对象
	 */
	private final T target;

	/**
	 * ByteBuddy实例复用（线程安全）
	 */
	private static final ByteBuddy BYTE_BUDDY = new ByteBuddy();

	/**
	 * 类缓存：(原始类 + 新增属性签名) -> 已生成的增强类
	 */
	private static final ConcurrentHashMap<String, Class<?>> CLASS_CACHE = new ConcurrentHashMap<>();

	/**
	 * 构造器缓存：增强类 -> 无参构造器
	 */
	private static final ConcurrentHashMap<Class<?>, Constructor<?>> CONSTRUCTOR_CACHE = new ConcurrentHashMap<>();

	/**
	 * 使用类反射的构造器
	 * @param superclass	父类
	 * @param propertyMap	属性集合（含原始+新增）
	 */
	public DynamicBuddy(Class<T> superclass, Map<String, Class<?>> propertyMap) {
		this.target = generateBean(superclass, propertyMap);
	}

	/**
	 * 设置属性值
	 * @param property	属性名称
	 * @param value		属性值
	 */
	public void setValue(String property, Object value) {
		ReflectUtil.setFieldValue(target, property, value);
	}

	/**
	 * 获取属性值
	 * @param property	属性名称
	 * @return	Object
	 */
	public Object getValue(String property) {
		return ReflectUtil.getFieldValue(target, property);
	}

	/**
	 * 根据属性生成对象，带类缓存。
	 * @param superclass	父类
	 * @param propertyMap	属性集合
	 * @return	目标对象
	 */
	private T generateBean(Class<T> superclass, Map<String, Class<?>> propertyMap) {
		try {
			Class<?> enhancedClass = getOrCreateClass(superclass, propertyMap);
			return (T) getCachedConstructor(enhancedClass).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Failed to generate enhanced bean", e);
		}
	}

	/**
	 * 根据原始类和属性签名，获取或创建增强类
	 */
	private static Class<?> getOrCreateClass(Class<?> superclass, Map<String, Class<?>> propertyMap) {
		String cacheKey = buildCacheKey(superclass, propertyMap);
		return CLASS_CACHE.computeIfAbsent(cacheKey, k -> createEnhancedClass(superclass, propertyMap));
	}

	/**
	 * 构建缓存key：原始类名 + 排序后的新增属性签名
	 */
	private static String buildCacheKey(Class<?> superclass, Map<String, Class<?>> propertyMap) {
		StringBuilder sb = new StringBuilder(superclass.getName());
		propertyMap.entrySet().stream()
				.filter(e -> !ReflectUtil.hasField(superclass, e.getKey()))
				.sorted(Map.Entry.comparingByKey())
				.forEach(e -> sb.append(';').append(e.getKey()).append(':').append(e.getValue().getName()));
		return sb.toString();
	}

	/**
	 * 使用ByteBuddy动态创建增强子类
	 */
	private static Class<?> createEnhancedClass(Class<?> superclass, Map<String, Class<?>> propertyMap) {
		net.bytebuddy.dynamic.DynamicType.Builder<?> builder = BYTE_BUDDY.subclass(superclass);
		for (Map.Entry<String, Class<?>> entry : propertyMap.entrySet()) {
			String propName = entry.getKey();
			Class<?> propType = entry.getValue();
			if (ReflectUtil.hasField(superclass, propName)) {
				continue;
			}
			String capitalized = propName.substring(0, 1).toUpperCase() + propName.substring(1);
			String getterName = (propType == boolean.class ? "is" : "get") + capitalized;
			String setterName = "set" + capitalized;
			builder = builder
					.defineField(propName, propType, Visibility.PRIVATE)
					.defineMethod(getterName, propType, Visibility.PUBLIC)
					.intercept(FieldAccessor.ofField(propName))
					.defineMethod(setterName, void.class, Visibility.PUBLIC)
					.withParameters(propType)
					.intercept(FieldAccessor.ofField(propName));
		}
		try {
			return builder.make()
					.load(superclass.getClassLoader(), ClassLoadingStrategy.UsingLookup.of(
							MethodHandles.privateLookupIn(superclass, MethodHandles.lookup())))
					.getLoaded();
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Failed to create lookup for class: " + superclass.getName(), e);
		}
	}

	/**
	 * 获取缓存的无参构造器
	 */
	private static Constructor<?> getCachedConstructor(Class<?> clazz) {
		return CONSTRUCTOR_CACHE.computeIfAbsent(clazz, c -> {
			try {
				Constructor<?> ctor = c.getDeclaredConstructor();
				ctor.setAccessible(true);
				return ctor;
			} catch (NoSuchMethodException e) {
				throw new RuntimeException("No default constructor found", e);
			}
		});
	}
}
