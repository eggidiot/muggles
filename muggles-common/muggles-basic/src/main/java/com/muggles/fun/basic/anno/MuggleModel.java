package com.muggles.fun.basic.anno;

import java.lang.annotation.*;

/**
 * 麻瓜模型注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface MuggleModel {

    /**
     * 模型名称
     * @return String
     */
    String name() default "";
}
