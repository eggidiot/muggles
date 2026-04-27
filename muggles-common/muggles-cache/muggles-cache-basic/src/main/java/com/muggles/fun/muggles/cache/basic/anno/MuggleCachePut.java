package com.muggles.fun.muggles.cache.basic.anno;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 将对应数据放到缓存中
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MuggleCachePut {
	/**
	 * 设置缓存时间，默认单位毫秒数, 0表示不设置时间
	 */
	long expire() default 0;

	/**
	 * 时间单位
	 *
	 * @return
	 */
	TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

	/**
	 * 描述
	 *
	 * @return String
	 */
	String depict() default "";

	/**
	 * 缓存key，支持SpEL表达式
	 * <p>The SpEL expression evaluates against a dedicated context that provides the
	 * following meta-data:
	 * <ul>
	 * <li>{@code #result} for a reference to the result of the method invocation. For
	 * supported wrappers such as {@code Optional}, {@code #result} refers to the actual
	 * object, not the wrapper</li>
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
}
