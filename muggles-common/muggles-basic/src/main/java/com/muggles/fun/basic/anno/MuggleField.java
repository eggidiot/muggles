package com.muggles.fun.basic.anno;

import com.muggles.fun.basic.Constants;

import java.lang.annotation.*;

/**
 * 麻瓜字段注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface MuggleField {

    /**
     * 模型名称
     * @return String
     */
    String name() default "";

    /**
     * 字段默认值
     * @return String
     */
    String value() default "";
}
