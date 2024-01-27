package com.muggles.fun.tools.core.collection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author haotang
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.TYPE,ElementType.METHOD})
public @interface JoinProperty {

    /**
     * 指定映射的别名
     */
    String alias() default "";

    /**
     * 指定集合类型
     * @return
     */
    Class<?> classType() default Object.class;

    /**
     * 属性名 为空字符串时取当前字段名 在类上使用时该属性无效
     */
    String fieldName() default "";
}