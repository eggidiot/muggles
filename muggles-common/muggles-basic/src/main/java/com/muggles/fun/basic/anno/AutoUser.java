package com.muggles.fun.basic.anno;

import java.lang.annotation.*;

/**
 * 自动填充用户信息注解
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.TYPE, ElementType.METHOD})
public @interface AutoUser {
}
