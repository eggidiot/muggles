package com.muggles.fun.muggles.cache.basic.anno;

import java.lang.annotation.*;

/**
 * 删除缓存
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MuggleCacheEvict {

	/**
	 * 缓存名称
	 *
	 * @return String[]
	 */
	String[] cacheNames() default {};

	/**
	 * 缓存key，支持SpEL表达式
	 * <ul>
	 * <li>{@code #root.method}, {@code #root.target}, and {@code #root.caches} for
	 * references to the {@link java.lang.reflect.Method method}, target object, and
	 * affected cache(s) respectively.</li>
	 * <li>Shortcuts for the method name ({@code #root.methodName}) and target class
	 * ({@code #root.targetClass}) are also available.
	 * <li>Method arguments can be accessed by index. For instance the second argument
	 * can be accessed via {@code #root.args[1]}, {@code #p1} or {@code #a1}. Arguments
	 * can also be accessed by name if that information is available.</li>
	 * </ul>
	 *
	 * @return String
	 */
	String key() default "";

	/**
	 * 是否删除缓存中所有数据
	 * <p>默认情况下是只删除关联key的缓存数据
	 * <p>注意：当该参数设置成 {@code true} 时 {@link #key} 参数将无效
	 *
	 * @return boolean
	 */
	boolean allEntries() default false;


}
