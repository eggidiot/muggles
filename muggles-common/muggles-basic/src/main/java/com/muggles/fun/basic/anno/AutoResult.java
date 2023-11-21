package com.muggles.fun.basic.anno;

import java.lang.annotation.*;

/**
 * 自定义结果包装器
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.TYPE, ElementType.METHOD})
public @interface AutoResult {
}
