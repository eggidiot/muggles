package com.muggles.fun.tools.core.copier.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义转换注解
 * @author haotang
 */
@Target({ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.SOURCE)
public @interface AutoCopy {
    /**
     * 字段映射默认值为空,为空时默认拷贝字段名和字段类型相同的字段
     */
    Mapping[] value() default {};

    /**
     * 选择需要忽略的字段
     * @return
     */
    String[] excludes() default {};
}