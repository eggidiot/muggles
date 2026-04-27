package com.muggles.fun.muggles.cache.basic.anno;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 表示调用的方法（或类中的所有方法）的结果是可以被缓存的。
 * 当该方法被调用时先检查缓存是否命中，如果没有命中再调用被缓存的方法，并将其返回值放到缓存中。
 * 这里的value和key都支持SpEL 表达式
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MuggleCacheable {
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
	 *
	 * @return String
	 */
	String key() default "";

}
