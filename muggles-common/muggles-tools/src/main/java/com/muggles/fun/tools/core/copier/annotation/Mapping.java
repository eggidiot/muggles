package com.muggles.fun.tools.core.copier.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.muggles.fun.basic.convertor.ITypeConvertor;
import com.muggles.fun.basic.convertor.Null;

/**
 * 映射注解
 *
 * @author tanghao
 * @date 2024/2/17 16:17
 */
@Retention(RetentionPolicy.SOURCE)
public @interface Mapping {
    /**
     * 当前类属性名
     */
    String name();

    /**
     * 来源类属性名
     */
    String sourceName();

    /**
     * 把来源类字段类型转换成目标类型字段类型
     * 
     * @return
     */
    Class<? extends ITypeConvertor> handler() default Null.class;
}
